package cat.cccb.tfg.exposicions.exhibition;

import cat.cccb.tfg.exposicions.artwork.ArtworkEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exhibitions")
public class ExhibitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "exhibition_artworks",
            joinColumns = @JoinColumn(name = "exhibition_id"),
            inverseJoinColumns = @JoinColumn(name = "artwork_id")
    )
    private Set<ArtworkEntity> artworks = new HashSet<>();

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<ArtworkEntity> getArtworks() { return artworks; }
    public void setArtworks(Set<ArtworkEntity> artworks) { this.artworks = artworks; }
}
