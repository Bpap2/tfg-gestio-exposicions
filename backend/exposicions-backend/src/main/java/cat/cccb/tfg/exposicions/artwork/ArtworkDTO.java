package cat.cccb.tfg.exposicions.artwork;

public record ArtworkDTO(
        Long id,
        String title,
        String author,
        Integer yearInt,
        String description,
        String fileUrl
) {}
