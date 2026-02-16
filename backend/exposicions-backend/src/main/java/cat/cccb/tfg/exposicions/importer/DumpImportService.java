package cat.cccb.tfg.exposicions.importer;

import cat.cccb.tfg.exposicions.artwork.ArtworkEntity;
import cat.cccb.tfg.exposicions.artwork.ArtworkRepository;
import cat.cccb.tfg.exposicions.lender.LenderEntity;
import cat.cccb.tfg.exposicions.lender.LenderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.*;

@ConditionalOnProperty(name = "dump.enabled", havingValue = "true")
@Service
public class DumpImportService {

    private final JdbcTemplate dumpJdbc;          // DB tfg_dump
    private final LenderRepository lenderRepo;    // DB tfg_mvp
    private final ArtworkRepository artworkRepo;  // DB tfg_mvp

    @PersistenceContext
    private EntityManager em;

    public DumpImportService(
            @Qualifier("dumpJdbcTemplate") JdbcTemplate dumpJdbc,
            LenderRepository lenderRepo,
            ArtworkRepository artworkRepo
    ) {
        this.dumpJdbc = dumpJdbc;
        this.lenderRepo = lenderRepo;
        this.artworkRepo = artworkRepo;
    }

    public record ImportReport(
            boolean dryRun,
            int dumpLendersRows,
            int dumpArtworksRows,
            int lendersUpserted,
            int artworksUpdated,
            int artworksInserted,
            int artworksSkipped
    ) {}

    // per si vols cridar /api/admin/import-dump sense params
    @Transactional
    public ImportReport importAll() {
        return importAll(false, 200, 50);
    }

    @Transactional
    public ImportReport importAll(boolean dryRun, int pageSize, int flushEvery) {

        int dumpLendersRows = countDump("\"0 PRESTADORS\"");
        int dumpArtworksRows = countDump("\"0 OBRES\"");

        // 1) Lenders
        LenderImport lenders = importLenders(dryRun);
        Map<String, Long> lenderIdByCode = lenders.lenderIdByCode();
        int lendersUpserted = lenders.upserted();

        // 2) Artworks (paged)
        int updated = 0;
        int inserted = 0;
        int skipped  = 0;

        int offset = 0;
        Map<String, String> col = null;

        while (true) {
            List<Map<String, Object>> rows = dumpJdbc.queryForList(
                    "select * from \"0 OBRES\" limit ? offset ?",
                    pageSize, offset
            );
            if (rows.isEmpty()) break;

            if (col == null) col = normalizedColumnMap(rows.get(0).keySet());

            int i = 0;
            for (Map<String, Object> r : rows) {
                i++;

                Integer workNo = asInt(get(r, col,
                        "nobra","numobra","num_obra","obra","obra_no","idobra","codiobra","codigoobra","workno"
                ));

                String title = asStr(get(r, col, "titol","titulo","titolobra","tituloobra","nomobra","title"));
                String author = asStr(get(r, col, "autor","autoria","artista","author"));
                Integer yearInt = asInt(get(r, col, "any","ano","year","yearint"));

                // IMPORTANT: a dump el prestador és Nº prestador (INTEGER)
                Integer lenderNoInt = asInt(get(r, col,
                        "nprestador","numprestador","prestadorno","prestador_num",
                        "lenderno","lender_no"
                ));
                String lenderCodeStr = (lenderNoInt == null) ? null : String.valueOf(lenderNoInt);

                Optional<ArtworkEntity> opt = Optional.empty();
                if (workNo != null) opt = artworkRepo.findByWorkNo(workNo);

                boolean exists = opt.isPresent();
                ArtworkEntity a = exists ? opt.get() : new ArtworkEntity();

                if (!exists) a.setWorkNo(workNo);

                // title és NOT NULL -> si no hi ha title i és nou, el saltem
                if (!exists && (title == null || title.isBlank())) {
                    skipped++;
                    continue;
                }

                // Completa camps sense trepitjar
                if (title != null && !title.isBlank() && isBlank(a.getTitle())) a.setTitle(title);
                if (author != null && !author.isBlank() && isBlank(a.getAuthor())) a.setAuthor(author);
                if (a.getYearInt() == null && yearInt != null) a.setYearInt(yearInt);
                if (a.getWorkNo() == null && workNo != null) a.setWorkNo(workNo);

                // Link lender: guardem lender_no i lender_id
                if (lenderNoInt != null) {
                    a.setCode(lenderNoInt); // lender_no (columna)

                    Long lenderId = lenderIdByCode.get(lenderCodeStr);
                    if (lenderId != null) {
                        a.setLender(em.getReference(LenderEntity.class, lenderId));
                    }
                }

                if (!dryRun) {
                    artworkRepo.save(a);
                }

                if (exists) updated++;
                else inserted++;

                // flush/clear per evitar OOM en imports grans
                if (!dryRun && flushEvery > 0 && ((offset + i) % flushEvery == 0)) {
                    em.flush();
                    em.clear();
                }
            }

            offset += rows.size();
        }

        if (!dryRun) {
            em.flush();
            em.clear();
        }

        return new ImportReport(
                dryRun,
                dumpLendersRows,
                dumpArtworksRows,
                lendersUpserted,
                updated,
                inserted,
                skipped
        );
    }

    private int countDump(String tableNameWithQuotes) {
        Integer n = dumpJdbc.queryForObject("select count(*) from " + tableNameWithQuotes, Integer.class);
        return n == null ? 0 : n;
    }

    private record LenderImport(Map<String, Long> lenderIdByCode, int upserted) {}

    private LenderImport importLenders(boolean dryRun) {
        List<Map<String, Object>> rows = dumpJdbc.queryForList("select * from \"0 PRESTADORS\"");
        if (rows.isEmpty()) return new LenderImport(new HashMap<>(), 0);

        Map<String, String> col = normalizedColumnMap(rows.get(0).keySet());

        Map<String, Long> out = new HashMap<>();
        int upserted = 0;

        for (Map<String, Object> r : rows) {
            Integer codeInt = asInt(get(r, col,
                    "nprestador","numprestador","prestadorno","prestador_num",
                    "lenderno","lender_no"
            ));
            String codeStr = (codeInt == null) ? null : String.valueOf(codeInt);

            // IMPORTANT: el NOM és "prestador" (text)
            String name = asStr(get(r, col,
                    "prestador",
                    "nomprestador","nombreprestador",
                    "name","nom","nombre","titular"
            ));

            String email = asStr(get(r, col, "email","correu","correo"));
            String phone = asStr(get(r, col, "phone","telefon","telefono","tel"));
            String notes = asStr(get(r, col, "notes","observacions","observaciones","nota"));

            if (name == null || name.isBlank()) continue;

            LenderEntity e = null;

            if (codeStr != null && !codeStr.isBlank()) {
                e = lenderRepo.findByCode(codeStr).orElse(null);
            }

            // fallback per imports antics on code=name
            if (e == null) {
                e = lenderRepo.findByCode(name).orElse(null);
            }

            if (e == null) e = new LenderEntity();

            e.setName(name);
            e.setCode(codeStr);
            if (email != null) e.setEmail(email);
            if (phone != null) e.setPhone(phone);
            if (notes != null) e.setNotes(notes);

            if (!dryRun) {
                e = lenderRepo.save(e);
            }

            upserted++;

            if (codeStr != null && !codeStr.isBlank() && e.getId() != null) {
                out.put(codeStr, e.getId());
            }
        }

        return new LenderImport(out, upserted);
    }

    // --- Helpers column mapping ---

    private static Map<String, String> normalizedColumnMap(Set<String> cols) {
        Map<String, String> m = new HashMap<>();
        for (String c : cols) m.put(norm(c), c);
        return m;
    }

    private static Object get(Map<String, Object> row, Map<String, String> colMap, String... candidates) {
        for (String c : candidates) {
            String real = colMap.get(norm(c));
            if (real != null && row.containsKey(real)) return row.get(real);
        }
        return null;
    }

    private static String asStr(Object v) {
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isBlank() ? null : s;
    }

    private static Integer asInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        String s = String.valueOf(v).trim();
        if (s.isBlank()) return null;
        try {
            if (s.contains(",")) s = s.replace(",", ".");
            return (int) Double.parseDouble(s);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}", "");
        n = n.toLowerCase(Locale.ROOT);
        n = n.replaceAll("[^a-z0-9]+", "");
        return n;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
