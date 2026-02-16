package cat.cccb.tfg.exposicions.artwork;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "artworks")
public class ArtworkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core
    @Column(nullable = false, columnDefinition = "text")
    private String title = "Sense títol"; // fallback per evitar NOT NULL

    @Column(columnDefinition = "text")
    private String author;

    @Column(name = "year_int")
    private Integer yearInt;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "file_url", columnDefinition = "text")
    private String fileUrl;

    // Full fields (alineat amb V2)
    @Column(name = "lender_no")
    private Integer code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lender_id")
    @JsonIgnore
    private cat.cccb.tfg.exposicions.lender.LenderEntity lender;

    @Column(name = "work_no")
    private Integer workNo;

    @Column(name = "author_name", columnDefinition = "text")
    private String authorName;

    @Column(name = "author_surname", columnDefinition = "text")
    private String authorSurname;

    @Column(name = "date_text", columnDefinition = "text")
    private String dateText;

    @Column(name = "original_repro_av", columnDefinition = "text")
    private String originalReproAv;

    @Column(name = "work_type", columnDefinition = "text")
    private String workType;

    @Column(name = "technique_material", columnDefinition = "text")
    private String techniqueMaterial;

    private Double height;
    private Double width;
    private Double depth;

    @Column(name = "framed_dimensions", columnDefinition = "text")
    private String framedDimensions;

    @Column(name = "section", columnDefinition = "text")
    private String section;

    @Column(name = "repro_type", columnDefinition = "text")
    private String reproType;

    @Column(name = "loan_ok", columnDefinition = "text")
    private String loanOk;

    @Column(name = "management_notes", columnDefinition = "text")
    private String managementNotes;

    @Column(name = "inventory_no", columnDefinition = "text")
    private String inventoryNo;

    @Column(name = "credit", columnDefinition = "text")
    private String credit;

    @Column(name = "elements_no")
    private Integer elementsNo;

    private Double valuation;

    @Column(name = "currency")
    private String currency;

    @Column(name = "pickup_address", columnDefinition = "text")
    private String pickupAddress;

    @Column(name = "pickup_city_code", columnDefinition = "text")
    private String pickupCityCode;

    @Column(name = "pickup_country", columnDefinition = "text")
    private String pickupCountry;

    @Column(name = "return_address", columnDefinition = "text")
    private String returnAddress;

    @Column(name = "return_city_code", columnDefinition = "text")
    private String returnCityCode;

    @Column(name = "return_country", columnDefinition = "text")
    private String returnCountry;

    @Column(name = "web", columnDefinition = "text")
    private String web;

    @Column(name = "reviewed", columnDefinition = "text")
    private String reviewed;

    @Column(name = "conservation_notes", columnDefinition = "text")
    private String conservationNotes;

    @Column(name = "transport_notes", columnDefinition = "text")
    private String transportNotes;

    @Column(name = "insurance_notes", columnDefinition = "text")
    private String insuranceNotes;

    @Column(name = "packaging", columnDefinition = "text")
    private String packaging;

    @Column(name = "packaging_dimensions", columnDefinition = "text")
    private String packagingDimensions;

    @Column(name = "label_info", columnDefinition = "text")
    private String labelInfo;

    @Column(name = "av_no", columnDefinition = "text")
    private String avNo;

    @Column(name = "av_support", columnDefinition = "text")
    private String avSupport;

    @Column(name = "current_location", columnDefinition = "text")
    private String currentLocation;

    @Column(name = "catalog_notes", columnDefinition = "text")
    private String catalogNotes;

    @Column(name = "size_notes", columnDefinition = "text")
    private String sizeNotes;

    @Column(name = "placement", columnDefinition = "text")
    private String placement;

    @Column(name = "in_catalog", columnDefinition = "text")
    private String inCatalog;

    @Column(name = "hi_res_image", columnDefinition = "text")
    private String hiResImage;

    @Column(name = "entry_delivery_note", columnDefinition = "text")
    private String entryDeliveryNote;

    @Column(name = "frame_at_cccb", columnDefinition = "text")
    private String frameAtCccb;

    @Column(name = "showcase_pedestal", columnDefinition = "text")
    private String showcasePedestal;

    @Column(name = "management_status", columnDefinition = "text")
    private String managementStatus;

    @Column(name = "other_info", columnDefinition = "text")
    private String otherInfo;

    @Column(name = "dimensions_text", columnDefinition = "text")
    private String dimensionsText;

    @Column(name = "options_text", columnDefinition = "text")
    private String optionsText;

    @Column(name = "def_exposed", columnDefinition = "text")
    private String defExposed;

    @Column(name = "image_at_cccb", nullable = false)
    private boolean imageAtCccb = false;

    @Column(name = "thematic_group_ok", columnDefinition = "text")
    private String thematicGroupOk;

    @Column(name = "sub_section_ok", columnDefinition = "text")
    private String subSectionOk;

    @Column(name = "tender", columnDefinition = "text")
    private String tender;

    @Column(name = "insurance", columnDefinition = "text")
    private String insurance;

    @Column(name = "final_format", columnDefinition = "text")
    private String finalFormat;

    @Column(name = "ambit", columnDefinition = "text")
    private String ambit;

    @Column(name = "subambit", columnDefinition = "text")
    private String subambit;

    // --- IMPORTANT: evita inserir title = null (NOT NULL a DB) ---
    @PrePersist
    @PreUpdate
    private void ensureNonNullTitle() {
        if (title == null || title.isBlank()) {
            if (workNo != null) title = "Obra " + workNo;
            else title = "Sense títol";
        }
    }

    // getters/setters
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getYearInt() { return yearInt; }
    public void setYearInt(Integer yearInt) { this.yearInt = yearInt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public Integer getWorkNo() { return workNo; }
    public void setWorkNo(Integer workNo) { this.workNo = workNo; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorSurname() { return authorSurname; }
    public void setAuthorSurname(String authorSurname) { this.authorSurname = authorSurname; }

    public String getDateText() { return dateText; }
    public void setDateText(String dateText) { this.dateText = dateText; }

    public String getOriginalReproAv() { return originalReproAv; }
    public void setOriginalReproAv(String originalReproAv) { this.originalReproAv = originalReproAv; }

    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }

    public String getTechniqueMaterial() { return techniqueMaterial; }
    public void setTechniqueMaterial(String techniqueMaterial) { this.techniqueMaterial = techniqueMaterial; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    public Double getDepth() { return depth; }
    public void setDepth(Double depth) { this.depth = depth; }

    public String getFramedDimensions() { return framedDimensions; }
    public void setFramedDimensions(String framedDimensions) { this.framedDimensions = framedDimensions; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getReproType() { return reproType; }
    public void setReproType(String reproType) { this.reproType = reproType; }

    public String getLoanOk() { return loanOk; }
    public void setLoanOk(String loanOk) { this.loanOk = loanOk; }

    public String getManagementNotes() { return managementNotes; }
    public void setManagementNotes(String managementNotes) { this.managementNotes = managementNotes; }

    public String getInventoryNo() { return inventoryNo; }
    public void setInventoryNo(String inventoryNo) { this.inventoryNo = inventoryNo; }

    public String getCredit() { return credit; }
    public void setCredit(String credit) { this.credit = credit; }

    public Integer getElementsNo() { return elementsNo; }
    public void setElementsNo(Integer elementsNo) { this.elementsNo = elementsNo; }

    public Double getValuation() { return valuation; }
    public void setValuation(Double valuation) { this.valuation = valuation; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getPickupCityCode() { return pickupCityCode; }
    public void setPickupCityCode(String pickupCityCode) { this.pickupCityCode = pickupCityCode; }

    public String getPickupCountry() { return pickupCountry; }
    public void setPickupCountry(String pickupCountry) { this.pickupCountry = pickupCountry; }

    public String getReturnAddress() { return returnAddress; }
    public void setReturnAddress(String returnAddress) { this.returnAddress = returnAddress; }

    public String getReturnCityCode() { return returnCityCode; }
    public void setReturnCityCode(String returnCityCode) { this.returnCityCode = returnCityCode; }

    public String getReturnCountry() { return returnCountry; }
    public void setReturnCountry(String returnCountry) { this.returnCountry = returnCountry; }

    public String getWeb() { return web; }
    public void setWeb(String web) { this.web = web; }

    public String getReviewed() { return reviewed; }
    public void setReviewed(String reviewed) { this.reviewed = reviewed; }

    public String getConservationNotes() { return conservationNotes; }
    public void setConservationNotes(String conservationNotes) { this.conservationNotes = conservationNotes; }

    public String getTransportNotes() { return transportNotes; }
    public void setTransportNotes(String transportNotes) { this.transportNotes = transportNotes; }

    public String getInsuranceNotes() { return insuranceNotes; }
    public void setInsuranceNotes(String insuranceNotes) { this.insuranceNotes = insuranceNotes; }

    public String getPackaging() { return packaging; }
    public void setPackaging(String packaging) { this.packaging = packaging; }

    public String getPackagingDimensions() { return packagingDimensions; }
    public void setPackagingDimensions(String packagingDimensions) { this.packagingDimensions = packagingDimensions; }

    public String getLabelInfo() { return labelInfo; }
    public void setLabelInfo(String labelInfo) { this.labelInfo = labelInfo; }

    public String getAvNo() { return avNo; }
    public void setAvNo(String avNo) { this.avNo = avNo; }

    public String getAvSupport() { return avSupport; }
    public void setAvSupport(String avSupport) { this.avSupport = avSupport; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getCatalogNotes() { return catalogNotes; }
    public void setCatalogNotes(String catalogNotes) { this.catalogNotes = catalogNotes; }

    public String getSizeNotes() { return sizeNotes; }
    public void setSizeNotes(String sizeNotes) { this.sizeNotes = sizeNotes; }

    public String getPlacement() { return placement; }
    public void setPlacement(String placement) { this.placement = placement; }

    public String getInCatalog() { return inCatalog; }
    public void setInCatalog(String inCatalog) { this.inCatalog = inCatalog; }

    public String getHiResImage() { return hiResImage; }
    public void setHiResImage(String hiResImage) { this.hiResImage = hiResImage; }

    public String getEntryDeliveryNote() { return entryDeliveryNote; }
    public void setEntryDeliveryNote(String entryDeliveryNote) { this.entryDeliveryNote = entryDeliveryNote; }

    public String getFrameAtCccb() { return frameAtCccb; }
    public void setFrameAtCccb(String frameAtCccb) { this.frameAtCccb = frameAtCccb; }

    public String getShowcasePedestal() { return showcasePedestal; }
    public void setShowcasePedestal(String showcasePedestal) { this.showcasePedestal = showcasePedestal; }

    public String getManagementStatus() { return managementStatus; }
    public void setManagementStatus(String managementStatus) { this.managementStatus = managementStatus; }

    public String getOtherInfo() { return otherInfo; }
    public void setOtherInfo(String otherInfo) { this.otherInfo = otherInfo; }

    public String getDimensionsText() { return dimensionsText; }
    public void setDimensionsText(String dimensionsText) { this.dimensionsText = dimensionsText; }

    public String getOptionsText() { return optionsText; }
    public void setOptionsText(String optionsText) { this.optionsText = optionsText; }

    public String getDefExposed() { return defExposed; }
    public void setDefExposed(String defExposed) { this.defExposed = defExposed; }

    public boolean isImageAtCccb() { return imageAtCccb; }
    public void setImageAtCccb(boolean imageAtCccb) { this.imageAtCccb = imageAtCccb; }

    public String getThematicGroupOk() { return thematicGroupOk; }
    public void setThematicGroupOk(String thematicGroupOk) { this.thematicGroupOk = thematicGroupOk; }

    public String getSubSectionOk() { return subSectionOk; }
    public void setSubSectionOk(String subSectionOk) { this.subSectionOk = subSectionOk; }

    public String getTender() { return tender; }
    public void setTender(String tender) { this.tender = tender; }

    public String getInsurance() { return insurance; }
    public void setInsurance(String insurance) { this.insurance = insurance; }

    public String getFinalFormat() { return finalFormat; }
    public void setFinalFormat(String finalFormat) { this.finalFormat = finalFormat; }

    public String getAmbit() { return ambit; }
    public void setAmbit(String ambit) { this.ambit = ambit; }

    public String getSubambit() { return subambit; }
    public void setSubambit(String subambit) { this.subambit = subambit; }

    public cat.cccb.tfg.exposicions.lender.LenderEntity getLender() { return lender; }
    public void setLender(cat.cccb.tfg.exposicions.lender.LenderEntity lender) { this.lender = lender; }
}
