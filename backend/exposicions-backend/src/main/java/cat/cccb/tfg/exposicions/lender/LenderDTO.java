package cat.cccb.tfg.exposicions.lender;

public record LenderDTO(
        Long id,
        String code,
        String name,
        String email,
        String phone,
        String notes
) {}
