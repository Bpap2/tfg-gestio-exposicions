package cat.cccb.tfg.exposicions.exhibition;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;



public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long> {

    @Query("select e from ExhibitionEntity e left join fetch e.artworks where e.id = :id")
    Optional<ExhibitionEntity> findByIdWithArtworks(@Param("id") Long id);

}
