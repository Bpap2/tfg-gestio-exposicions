package cat.cccb.tfg.exposicions.artwork;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkReportController {

    private final ArtworkRepository artworkRepo;

    public ArtworkReportController(ArtworkRepository artworkRepo) {
        this.artworkRepo = artworkRepo;
    }

    @GetMapping("/{id}/report.pdf")
    public ResponseEntity<byte[]> artworkReport(@PathVariable Long id) {
        var a = artworkRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Artwork not found"));

        byte[] pdf = buildPdf(a);

        String filename = "obra-" + (a.getWorkNo() != null ? a.getWorkNo() : id) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private byte[] buildPdf(ArtworkEntity a) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 48, 48, 48, 48);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font h1 = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font label = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font text = new Font(Font.HELVETICA, 11);

            doc.add(new Paragraph("Fitxa d’obra", h1));
            doc.add(new Paragraph(" "));

            addRow(doc, label, text, "Títol", nvl(a.getTitle(), "Sense títol"));
            addRow(doc, label, text, "Autor", nvl(a.getAuthor(), "—"));
            addRow(doc, label, text, "Nº obra", a.getWorkNo() != null ? String.valueOf(a.getWorkNo()) : "—");

            // si tens lender a l'entity, mostra'l (sense petar si és null)
            // (Opcional) si tens relació lender, mostra'l
            try {
                var lender = a.getLender(); // si existeix el mètode
                if (lender != null) {
                    addRow(doc, label, text, "Prestador", nvl(lender.getName(), "—"));
                    addRow(doc, label, text, "Codi prestador", nvl(lender.getCode(), "—"));
                }
            } catch (Exception ignored) {
                // si no tens getLender() a l'entity, no passa res
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("— Generat automàticament —", new Font(Font.HELVETICA, 9, Font.ITALIC)));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("No s'ha pogut generar el PDF", e);
        }
    }

    private void addRow(Document doc, Font label, Font text, String k, String v) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(k + ": ", label));
        p.add(new Chunk(v, text));
        p.setSpacingAfter(6);
        doc.add(p);
    }

    private String nvl(String s, String fallback) {
        return (s == null || s.trim().isEmpty()) ? fallback : s.trim();
    }
}
