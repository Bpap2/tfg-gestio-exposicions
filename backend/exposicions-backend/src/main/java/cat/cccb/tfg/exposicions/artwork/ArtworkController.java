package cat.cccb.tfg.exposicions.artwork;

import cat.cccb.tfg.exposicions.common.NotFoundException;
import cat.cccb.tfg.exposicions.lender.LenderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkRepository artworkRepo;
    private final LenderRepository lenderRepo;

    public ArtworkController(ArtworkRepository artworkRepo, LenderRepository lenderRepo) {
        this.artworkRepo = artworkRepo;
        this.lenderRepo = lenderRepo;
    }

    // ✅ Llistat lleuger per la taula (més ràpid i usable)
    public record ArtworkSummary(Long id, String title, String author, Integer yearInt) {
        public static ArtworkSummary from(ArtworkEntity a) {
            return new ArtworkSummary(a.getId(), a.getTitle(), a.getAuthor(), a.getYearInt());
        }
    }

    // ✅ Request complet (tots els camps editables)
    public static class ArtworkRequest {
        @NotBlank public String title;

        public String author;
        public Integer yearInt;
        public String description;
        public String fileUrl;

        public Integer code;
        public Integer workNo;

        public String authorName;
        public String authorSurname;

        public String dateText;
        public String originalReproAv;
        public String workType;
        public String techniqueMaterial;

        public Double height;
        public Double width;
        public Double depth;
        public String framedDimensions;

        public String section;
        public String reproType;

        public String loanOk;
        public String managementNotes;
        public String inventoryNo;
        public String credit;
        public Integer elementsNo;
        public Double valuation;
        public String currency;

        public String pickupAddress;
        public String pickupCityCode;
        public String pickupCountry;
        public String returnAddress;
        public String returnCityCode;
        public String returnCountry;

        public String web;
        public String reviewed;

        public String conservationNotes;
        public String transportNotes;
        public String insuranceNotes;

        public String packaging;
        public String packagingDimensions;

        public String labelInfo;

        public String avNo;
        public String avSupport;

        public String currentLocation;

        public String catalogNotes;
        public String sizeNotes;
        public String placement;
        public String inCatalog;

        public String hiResImage;
        public String entryDeliveryNote;
        public String frameAtCccb;
        public String showcasePedestal;

        public String managementStatus;
        public String otherInfo;
        public String dimensionsText;
        public String optionsText;
        public String defExposed;

        public Boolean imageAtCccb;

        public String thematicGroupOk;
        public String subSectionOk;

        public String tender;
        public String insurance;
        public String finalFormat;

        public String ambit;
        public String subambit;
    }

    @GetMapping
    public List<ArtworkSummary> list() {
        return artworkRepo.findAll().stream().map(ArtworkSummary::from).toList();
    }

    @GetMapping("/{id}")
    public ArtworkEntity get(@PathVariable Long id) {
        return artworkRepo.findById(id).orElseThrow(() -> new NotFoundException("Artwork not found"));
    }

    @PostMapping
    public ArtworkEntity create(@Valid @RequestBody ArtworkRequest r) {
        ArtworkEntity a = new ArtworkEntity();
        apply(a, r);
        return artworkRepo.save(a);
    }

    @PutMapping("/{id}")
    public ArtworkEntity update(@PathVariable Long id, @Valid @RequestBody ArtworkRequest r) {
        ArtworkEntity a = artworkRepo.findById(id).orElseThrow(() -> new NotFoundException("Artwork not found"));
        apply(a, r);
        return artworkRepo.save(a);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!artworkRepo.existsById(id)) throw new NotFoundException("Artwork not found");
        artworkRepo.deleteById(id);
    }

    /**
     * ✅ Assigna un prestador (lender) a una obra.
     * Endpoint: POST /api/artworks/{artworkId}/lender/{lenderId}
     */
    @PostMapping("/{artworkId}/lender/{lenderId}")
    public ArtworkEntity setLender(@PathVariable Long artworkId, @PathVariable Long lenderId) {
        var a = artworkRepo.findById(artworkId).orElseThrow(() -> new NotFoundException("Artwork not found"));
        var l = lenderRepo.findById(lenderId).orElseThrow(() -> new NotFoundException("Lender not found"));
        a.setLender(l);
        return artworkRepo.save(a);
    }

    private static void apply(ArtworkEntity a, ArtworkRequest r) {
        a.setTitle(r.title);
        a.setAuthor(r.author);
        a.setYearInt(r.yearInt);
        a.setDescription(r.description);
        a.setFileUrl(r.fileUrl);

        a.setCode(r.code);
        a.setWorkNo(r.workNo);

        a.setAuthorName(r.authorName);
        a.setAuthorSurname(r.authorSurname);

        a.setDateText(r.dateText);
        a.setOriginalReproAv(r.originalReproAv);
        a.setWorkType(r.workType);
        a.setTechniqueMaterial(r.techniqueMaterial);

        a.setHeight(r.height);
        a.setWidth(r.width);
        a.setDepth(r.depth);
        a.setFramedDimensions(r.framedDimensions);

        a.setSection(r.section);
        a.setReproType(r.reproType);

        a.setLoanOk(r.loanOk);
        a.setManagementNotes(r.managementNotes);
        a.setInventoryNo(r.inventoryNo);
        a.setCredit(r.credit);
        a.setElementsNo(r.elementsNo);
        a.setValuation(r.valuation);
        a.setCurrency(r.currency);

        a.setPickupAddress(r.pickupAddress);
        a.setPickupCityCode(r.pickupCityCode);
        a.setPickupCountry(r.pickupCountry);
        a.setReturnAddress(r.returnAddress);
        a.setReturnCityCode(r.returnCityCode);
        a.setReturnCountry(r.returnCountry);

        a.setWeb(r.web);
        a.setReviewed(r.reviewed);

        a.setConservationNotes(r.conservationNotes);
        a.setTransportNotes(r.transportNotes);
        a.setInsuranceNotes(r.insuranceNotes);

        a.setPackaging(r.packaging);
        a.setPackagingDimensions(r.packagingDimensions);

        a.setLabelInfo(r.labelInfo);

        a.setAvNo(r.avNo);
        a.setAvSupport(r.avSupport);

        a.setCurrentLocation(r.currentLocation);

        a.setCatalogNotes(r.catalogNotes);
        a.setSizeNotes(r.sizeNotes);
        a.setPlacement(r.placement);
        a.setInCatalog(r.inCatalog);

        a.setHiResImage(r.hiResImage);
        a.setEntryDeliveryNote(r.entryDeliveryNote);
        a.setFrameAtCccb(r.frameAtCccb);
        a.setShowcasePedestal(r.showcasePedestal);

        a.setManagementStatus(r.managementStatus);
        a.setOtherInfo(r.otherInfo);
        a.setDimensionsText(r.dimensionsText);
        a.setOptionsText(r.optionsText);
        a.setDefExposed(r.defExposed);

        if (r.imageAtCccb != null) a.setImageAtCccb(r.imageAtCccb);

        a.setThematicGroupOk(r.thematicGroupOk);
        a.setSubSectionOk(r.subSectionOk);

        a.setTender(r.tender);
        a.setInsurance(r.insurance);
        a.setFinalFormat(r.finalFormat);

        a.setAmbit(r.ambit);
        a.setSubambit(r.subambit);
    }
}
