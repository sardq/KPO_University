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

import demo.models.UserEntity;
import demo.models.UserRole;

public interface UserRepository extends CrudRepository<UserEntity, Long>,
        PagingAndSortingRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findByEmail(String email);

    @NotNull
    Optional<UserEntity> findById(@NotNull Long id);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByLoginIgnoreCase(String email);

    @NotNull
    Page<UserEntity> findAll(@NotNull Pageable pageable);

    Page<UserEntity> findByRole(UserRole role, Pageable pageable);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE LOWER(u.login) LIKE LOWER(CONCAT('%', :text, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :text, '%'))
            OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :text, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :text, '%'))
            """)
    Page<UserEntity> searchByText(@Param("text") String text, Pageable pageable);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE u.role = :role AND
                    (
                    LOWER(u.login) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :text, '%'))
                    )
            """)
    Page<UserEntity> searchByTextAndRole(
            @Param("text") String text,
            @Param("role") UserRole role,
            Pageable pageable);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE EXISTS (
                SELECT 1 FROM GroupEntity g
                JOIN g.students student
                WHERE student.id = u.id
                AND g.id = :groupId
            )
            """)
    List<UserEntity> findByGroupId(@Param("groupId") Long groupId);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE EXISTS (
                SELECT 1 FROM GroupEntity g
                JOIN g.students student
                WHERE student.id = u.id
                AND g.id = :groupId
            )
            """)
    Page<UserEntity> findByGroupId(@Param("groupId") Long groupId, Pageable pageable);
}
