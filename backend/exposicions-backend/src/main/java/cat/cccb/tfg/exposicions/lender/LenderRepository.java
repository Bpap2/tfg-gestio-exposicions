package cat.cccb.tfg.exposicions.lender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LenderRepository extends JpaRepository<LenderEntity, Long> {
    Optional<LenderEntity> findByCode(String code);
    @Query(value = """
        select
          l.id as id,
          l.code as code,
          l.name as name,
          coalesce(count(a.id), 0) as artworksCount
        from lenders l
        left join artworks a on a.lender_id = l.id
        group by l.id, l.code, l.name
        order by l.name
        """, nativeQuery = true)
    List<LenderListRow> findAllWithArtworksCount();
}
