package cat.cccb.tfg.exposicions.artwork;

import cat.cccb.tfg.exposicions.artwork.ArtworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtworkRepository extends JpaRepository<ArtworkEntity, Long> {

    // 👇 PROJECCIÓ LLEUGERA PER LLISTATS
    interface ArtworkLite {
        Long getId();
        String getTitle();
        String getAuthor();
        Integer getYearInt();
        String getWorkType();
        String getTechniqueMaterial();
        Integer getCode();
    }

    List<ArtworkEntity> findByLenderId(Long lenderId);
    Optional<ArtworkEntity> findByWorkNo(Integer workNo);
    // 👇 QUERY SEGURA (sense Lazy problems)
    @org.springframework.data.jpa.repository.Query("""
        select 
            a.id as id,
            a.title as title,
            a.author as author,
            a.yearInt as yearInt,
            a.workType as workType,
            a.techniqueMaterial as techniqueMaterial,
            a.code as code
        from ArtworkEntity a
        where a.lender.id = :lenderId
        order by a.id asc
    """)
    List<ArtworkLite> findLiteByLenderId(
            @org.springframework.data.repository.query.Param("lenderId") Long lenderId
    );
    Optional<ArtworkEntity> findFirstByWorkNo(Integer workNo);
    Optional<ArtworkEntity> findFirstByTitleAndAuthorAndYearInt(String title, String author, Integer yearInt);
}
