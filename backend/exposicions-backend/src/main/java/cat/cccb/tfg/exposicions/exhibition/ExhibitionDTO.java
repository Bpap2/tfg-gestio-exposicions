package cat.cccb.tfg.exposicions.exhibition;

import java.time.LocalDate;

public record ExhibitionDTO(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {}
