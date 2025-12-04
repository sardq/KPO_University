package demo.repositories;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import demo.models.UserEntity;
import demo.models.UserRole;

public interface UserRepository extends CrudRepository<UserEntity, Long>,
        PagingAndSortingRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

    Optional<UserEntity> findByEmail(String email);

    @NotNull
    Optional<UserEntity> findById(@NotNull Long id);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    @NotNull
    Page<UserEntity> findAll(@NotNull Pageable pageable);

    Page<UserEntity> findByRole(UserRole role, Pageable pageable);

}
