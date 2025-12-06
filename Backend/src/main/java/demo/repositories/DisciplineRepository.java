package demo.repositories;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import demo.models.DisciplineEntity;

public interface DisciplineRepository extends CrudRepository<DisciplineEntity, Long>,
        PagingAndSortingRepository<DisciplineEntity, Long> {
    Optional<DisciplineEntity> findByName(String name);

    @NotNull
    Optional<DisciplineEntity> findById(@NotNull Long id);

    @NotNull
    Page<DisciplineEntity> findAll(@NotNull Pageable pageable);

    @Query("""
            SELECT d FROM DisciplineEntity d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :text, '%'))
            """)
    Page<DisciplineEntity> searchByText(@Param("text") String text, Pageable pageable);

    @Query("""
            SELECT d FROM DisciplineEntity d
            LEFT JOIN FETCH d.groups g
            WHERE (:groupId IS NULL OR g.id = :groupId)
              AND LOWER(d.name) LIKE LOWER(CONCAT('%', :text, '%'))
            """)
    Page<DisciplineEntity> searchAndFilter(
            @Param("text") String text,
            @Param("groupId") Long groupId,
            Pageable pageable);

    @Query("""
            SELECT d FROM DisciplineEntity d
            JOIN d.groups g
            WHERE g.id = :groupId
            """)
    List<DisciplineEntity> findByGroupId(@Param("groupId") Long groupId);

    @Query("""
            SELECT d FROM DisciplineEntity d
            JOIN d.groups g
            WHERE g.id = :groupId
            """)
    Page<DisciplineEntity> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);

    @Query("""
                SELECT d FROM DisciplineEntity d
                LEFT JOIN FETCH d.groups
                WHERE d.id = :id
            """)
    Optional<DisciplineEntity> findByIdWithGroups(@Param("id") Long id);
}
