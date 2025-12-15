package demo.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import demo.models.ExerciseEntity;

public interface ExerciseRepository extends CrudRepository<ExerciseEntity, Long>,
        PagingAndSortingRepository<ExerciseEntity, Long> {
    Optional<ExerciseEntity> findByDate(LocalDateTime date);

    @NotNull
    Optional<ExerciseEntity> findById(@NotNull Long id);

    @NotNull
    Page<ExerciseEntity> findAll(@NotNull Pageable pageable);

    @Query("""
            SELECT e FROM ExerciseEntity e
            WHERE e.discipline.id = :disciplineId
            AND e.group.id = :groupId
            """)
    List<ExerciseEntity> findByDisciplineIdAndGroupId(@Param("disciplineId") Long disciplineId,
            @Param("groupId") Long groupId);

    @Query("""
            SELECT e FROM ExerciseEntity e
            WHERE e.date = :date
            AND e.group.id = :groupId
            AND e.discipline.id = :disciplineId
            """)
    Optional<ExerciseEntity> findByDateAndGroupIdAndDisciplineId(
            @Param("date") LocalDateTime date,
            @Param("groupId") Long groupId,
            @Param("disciplineId") Long disciplineId);

    @Query("""
            SELECT e FROM ExerciseEntity e
            WHERE e.discipline.id = :disciplineId
            AND e.group.id = :groupId
            """)
    Page<ExerciseEntity> findByDisciplineIdAndGroupId(
            @Param("disciplineId") Long disciplineId,
            @Param("groupId") Long groupId,
            Pageable pageable);

}
