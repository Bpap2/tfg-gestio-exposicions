package cat.cccb.tfg.exposicions.exhibition;

import cat.cccb.tfg.exposicions.artwork.ArtworkRepository;
import cat.cccb.tfg.exposicions.common.NotFoundException;
import org.springframework.web.bind.annotation.*;
import cat.cccb.tfg.exposicions.artwork.ArtworkDTO;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    private final ExhibitionRepository exhibitionRepo;
    private final ArtworkRepository artworkRepo;

    public ExhibitionController(ExhibitionRepository exhibitionRepo, ArtworkRepository artworkRepo) {
        this.exhibitionRepo = exhibitionRepo;
        this.artworkRepo = artworkRepo;
    }

    public static class ExhibitionRequest {
        public String name;
        public LocalDate startDate;
        public LocalDate endDate;
        public String description;
    }

    private static ExhibitionDTO toDto(ExhibitionEntity e) {
        return new ExhibitionDTO(
                e.getId(),
                e.getName(),
                e.getStartDate(),
                e.getEndDate(),
                e.getDescription()
        );
    }

    // ---- CRUD (retorna DTO, NO entitat) ----

    @GetMapping
    public List<ExhibitionDTO> list() {
        return exhibitionRepo.findAll().stream().map(ExhibitionController::toDto).toList();
    }

    @GetMapping("/{id}")
    public ExhibitionDTO get(@PathVariable Long id) {
        var e = exhibitionRepo.findById(id).orElseThrow(() -> new NotFoundException("Exhibition not found"));
        return toDto(e);
    }

    @GetMapping("/{id}/artworks")
    public List<ArtworkDTO> listArtworks(@PathVariable Long id) {
        var e = exhibitionRepo.findByIdWithArtworks(id)
                .orElseThrow(() -> new NotFoundException("Exhibition not found"));

        return e.getArtworks().stream()
                .map(a -> new ArtworkDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getAuthor(),
                        a.getYearInt(),
                        a.getDescription(),
                        a.getFileUrl()
                ))
                .toList();
    }


    @PostMapping
    public ExhibitionDTO create(@RequestBody ExhibitionRequest r) {
        if (r == null || r.name == null || r.name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        ExhibitionEntity e = new ExhibitionEntity();
        e.setName(r.name);
        e.setStartDate(r.startDate);
        e.setEndDate(r.endDate);
        e.setDescription(r.description);
        return toDto(exhibitionRepo.save(e));
    }

    @PutMapping("/{id}")
    public ExhibitionDTO update(@PathVariable Long id, @RequestBody ExhibitionRequest r) {
        var e = exhibitionRepo.findById(id).orElseThrow(() -> new NotFoundException("Exhibition not found"));
        if (r == null || r.name == null || r.name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        e.setName(r.name);
        e.setStartDate(r.startDate);
        e.setEndDate(r.endDate);
        e.setDescription(r.description);
        return toDto(exhibitionRepo.save(e));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!exhibitionRepo.existsById(id)) throw new NotFoundException("Exhibition not found");
        exhibitionRepo.deleteById(id);
    }

    // ---- Relació: afegir/treure obra (retorna DTO, NO entitat) ----

    @PostMapping("/{id}/artworks/{artworkId}")
    public ExhibitionDTO addArtwork(@PathVariable Long id, @PathVariable Long artworkId) {
        var e = exhibitionRepo.findByIdWithArtworks(id)
                .orElseThrow(() -> new NotFoundException("Exhibition not found"));

        var a = artworkRepo.findById(artworkId)
                .orElseThrow(() -> new NotFoundException("Artwork not found"));

        e.getArtworks().add(a);
        return toDto(exhibitionRepo.save(e));
    }

    @DeleteMapping("/{id}/artworks/{artworkId}")
    public ExhibitionDTO removeArtwork(@PathVariable Long id, @PathVariable Long artworkId) {
        var e = exhibitionRepo.findByIdWithArtworks(id)
                .orElseThrow(() -> new NotFoundException("Exhibition not found"));

        var a = artworkRepo.findById(artworkId)
                .orElseThrow(() -> new NotFoundException("Artwork not found"));

        e.getArtworks().remove(a);
        return toDto(exhibitionRepo.save(e));
    }
}
