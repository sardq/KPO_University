package demo.repositories;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import demo.models.GroupEntity;

public interface GroupRepository extends CrudRepository<GroupEntity, Long>,
        PagingAndSortingRepository<GroupEntity, Long> {
    Optional<GroupEntity> findByName(String name);

    @NotNull
    Optional<GroupEntity> findById(@NotNull Long id);

    @NotNull
    Page<GroupEntity> findAll(@NotNull Pageable pageable);

    @Query("""
            SELECT g FROM GroupEntity g
            WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :text, '%'))
            """)
    Page<GroupEntity> searchByText(@Param("text") String text, Pageable pageable);

    @Query("""
            SELECT g FROM GroupEntity g
            LEFT JOIN g.disciplines d
            WHERE (:disciplineId IS NULL OR d.id = :disciplineId)
              AND LOWER(g.name) LIKE LOWER(CONCAT('%', :text, '%'))
            """)
    Page<GroupEntity> searchAndFilter(
            @Param("text") String text,
            @Param("disciplineId") Long disciplineId,
            Pageable pageable);

}
