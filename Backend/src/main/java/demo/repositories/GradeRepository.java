package demo.repositories;

import demo.dto.GradeDto;
import demo.models.GradeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends
        CrudRepository<GradeEntity, Long>,
        PagingAndSortingRepository<GradeEntity, Long> {

    Optional<GradeEntity> findByExerciseIdAndStudentId(Long exerciseId, Long studentId);

    List<GradeEntity> findByExerciseId(Long exerciseId);

    List<GradeEntity> findByStudentId(Long studentId);

    @Query("""
            SELECT m FROM GradeEntity m
            WHERE m.exercise.id = :exerciseId
              AND m.student.id = :studentId
            """)
    Optional<GradeEntity> findExisting(@Param("exerciseId") Long exerciseId, @Param("studentId") Long studentId);

    @Query("""
            SELECT g FROM GradeEntity g
            JOIN g.student s
            JOIN g.exercise e
            WHERE s.group.id = :groupId
              AND e.discipline.id = :disciplineId
            """)
    List<GradeEntity> findAllByGroupAndDiscipline(
            @Param("groupId") Long groupId,
            @Param("disciplineId") Long disciplineId);

    Page<GradeEntity> findAll(Pageable pageable);

    @Query("""
            SELECT AVG(
                CASE
                    WHEN g.value = demo.models.GradeEnum.ONE THEN 1.0
                    WHEN g.value = demo.models.GradeEnum.TWO THEN 2.0
                    WHEN g.value = demo.models.GradeEnum.THREE THEN 3.0
                    WHEN g.value = demo.models.GradeEnum.FOUR THEN 4.0
                    WHEN g.value = demo.models.GradeEnum.FIVE THEN 5.0
                    ELSE NULL
                END
            )
            FROM GradeEntity g
            WHERE g.student.id = :studentId
            """)
    Double getStudentAverage(@Param("studentId") Long studentId);

    @Query("""
            SELECT AVG(
                CASE
                    WHEN g.value = demo.models.GradeEnum.ONE THEN 1.0
                    WHEN g.value = demo.models.GradeEnum.TWO THEN 2.0
                    WHEN g.value = demo.models.GradeEnum.THREE THEN 3.0
                    WHEN g.value = demo.models.GradeEnum.FOUR THEN 4.0
                    WHEN g.value = demo.models.GradeEnum.FIVE THEN 5.0
                    ELSE NULL
                END
            )
            FROM GradeEntity g
            JOIN g.exercise e
            JOIN e.discipline d
            WHERE d.id = :disciplineId
            """)
    Double getDisciplineAverage(@Param("disciplineId") Long disciplineId);

    @Query("""
            SELECT AVG(
                CASE
                    WHEN g.value = demo.models.GradeEnum.ONE THEN 1.0
                    WHEN g.value = demo.models.GradeEnum.TWO THEN 2.0
                    WHEN g.value = demo.models.GradeEnum.THREE THEN 3.0
                    WHEN g.value = demo.models.GradeEnum.FOUR THEN 4.0
                    WHEN g.value = demo.models.GradeEnum.FIVE THEN 5.0
                    ELSE NULL
                END
            )
            FROM GradeEntity g
            JOIN g.exercise e
            WHERE e.group.id = :groupId
            AND e.discipline.id = :disciplineId
            GROUP BY g.student.id
            """)
    List<GradeDto.StudentAvg> getStudentsAverages(
            @Param("groupId") Long groupId,
            @Param("disciplineId") Long disciplineId);

}
