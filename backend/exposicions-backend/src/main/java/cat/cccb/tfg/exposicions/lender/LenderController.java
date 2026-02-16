package cat.cccb.tfg.exposicions.lender;


import cat.cccb.tfg.exposicions.artwork.ArtworkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;



@RestController
@RequestMapping("/api/lenders")
public class LenderController {

    public static class LenderRequest {
        public String code;
        public String name;
        public String email;
        public String phone;
        public String notes;
    }


    private final LenderRepository lenderRepo;
    private final ArtworkRepository artworkRepo;

    public LenderController(LenderRepository lenderRepo, ArtworkRepository artworkRepo) {
        this.lenderRepo = lenderRepo;
        this.artworkRepo = artworkRepo;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return lenderRepo.findAllWithArtworksCount().stream().map(l -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId());
            m.put("code", l.getCode());
            m.put("name", l.getName());
            m.put("artworksCount", l.getArtworksCount());
            return m;
        }).toList();
    }

    @GetMapping("/{id}")
    public LenderDTO get(@PathVariable Long id) {
        LenderEntity l = lenderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lender not found"));

        return new LenderDTO(
                l.getId(),
                l.getCode(),
                l.getName(),
                l.getEmail(),
                l.getPhone(),
                l.getNotes()
        );
    }

    @GetMapping("/{id}/artworks")
    public ResponseEntity<?> artworks(@PathVariable Long id) {
        if (!lenderRepo.existsById(id)) return ResponseEntity.notFound().build();

        var list = artworkRepo.findByLenderId(id).stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("workNo", a.getWorkNo());
            m.put("title", a.getTitle());   // pot ser null -> OK
            m.put("author", a.getAuthor()); // pot ser null -> OK
            return m;
        }).toList();

        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LenderRequest r) {
        if (r == null || r.name == null || r.name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        LenderEntity l = new LenderEntity();
        l.setName(r.name.trim());
        l.setCode(r.code != null && !r.code.isBlank() ? r.code.trim() : null);
        l.setEmail(r.email);
        l.setPhone(r.phone);
        l.setNotes(r.notes);

        try {
            LenderEntity saved = lenderRepo.save(l);
            return ResponseEntity.status(HttpStatus.CREATED).body(new LenderDTO(
                    saved.getId(),
                    saved.getCode(),
                    saved.getName(),
                    saved.getEmail(),
                    saved.getPhone(),
                    saved.getNotes()
            ));
        } catch (DataIntegrityViolationException e) {
            // per unique(code)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "lender code already exists");
        }
    }

    @PutMapping("/{id}")
    public LenderDTO update(@PathVariable Long id, @RequestBody LenderRequest r) {
        if (r == null || r.name == null || r.name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        LenderEntity l = lenderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lender not found"));

        l.setName(r.name.trim());
        l.setCode(r.code != null && !r.code.isBlank() ? r.code.trim() : null);
        l.setEmail(r.email);
        l.setPhone(r.phone);
        l.setNotes(r.notes);

        try {
            LenderEntity saved = lenderRepo.save(l);
            return new LenderDTO(
                    saved.getId(),
                    saved.getCode(),
                    saved.getName(),
                    saved.getEmail(),
                    saved.getPhone(),
                    saved.getNotes()
            );
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "lender code already exists");
        }
    }

}
