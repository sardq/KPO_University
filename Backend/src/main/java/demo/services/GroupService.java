package demo.services;

import demo.core.error.NotFoundException;
import demo.models.GroupEntity;
import demo.models.UserEntity;
import demo.repositories.GroupRepository;
import demo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GroupService {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private static final String LOG_RESPONSE = "Ответ: {}";
    private static final Sort default_sort = Sort.by("name");
    private final GroupRepository repository;
    private final UserRepository userRepository;
    private final GroupService self;

    public GroupService(GroupRepository repository,
            UserRepository userRepository,
            @Lazy GroupService self) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.self = self;
    }

    @Transactional(readOnly = true)
    public List<GroupEntity> getAll() {
        logger.info("Получение списка групп");
        var result = repository.findAll(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<GroupEntity> getAll(int page, int size) {
        logger.info("Получение списка групп: page={}, size={}", page, size);
        var pageable = PageRequest.of(page, size, default_sort);
        var result = repository.findAll(pageable);
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public GroupEntity get(Long id) {
        logger.info("Получение группы: {}", id);
        var result = repository.findByIdWithStudents(id)
                .orElseThrow(() -> new NotFoundException(GroupEntity.class, id));
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional
    public GroupEntity create(GroupEntity entity) {
        logger.info("Создание новой группы с именем, длиной {} символов",
                entity.getName() != null ? entity.getName().length() : 0);
        if (repository.findByName(entity.getName()).isPresent()) {
            throw new IllegalArgumentException("Группа уже существует: " + entity.getName());
        }
        var result = repository.save(entity);
        logger.info("Группа сохранена: {}", result);
        return result;
    }

    @Transactional
    public GroupEntity update(Long id, GroupEntity entity) {
        logger.info("Попытка обновить группу: {}", id);
        var exists = self.get(id);

        if (!exists.getName().equals(entity.getName())
                && repository.findByName(entity.getName()).isPresent()) {
            throw new IllegalArgumentException("Группа с таким названием уже существует");
        }

        exists.setName(entity.getName());
        exists.setDisciplines(entity.getDisciplines());

        repository.save(exists);
        logger.info("Группа обновлена: {}", exists);
        return exists;
    }

    @Transactional
    public GroupEntity delete(Long id) {
        GroupEntity group = self.get(id);

        group.getStudents().forEach(student -> student.setGroup(null));
        group.getStudents().clear();

        group.getDisciplines().clear();

        repository.delete(group);
        return group;
    }

    @Transactional
    public GroupEntity addStudent(Long groupId, Long studentId) {
        logger.info("Добавление студента {} в группу {}", studentId, groupId);

        GroupEntity group = self.get(groupId);

        UserEntity user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, studentId));

        if (user.getGroup() != null) {
            if (user.getGroup().getId().equals(groupId)) {
                throw new ValidationException(
                        String.format("Студент %s уже состоит в группе %s",
                                user.getLogin(),
                                group.getName()));
            } else {
                throw new ValidationException(
                        String.format("Студент %s уже состоит в другой группе %s",
                                user.getLogin(),
                                user.getGroup().getName()));
            }
        }

        user.setGroup(group);
        userRepository.save(user);

        group.getStudents().add(user);
        repository.save(group);

        return group;
    }

    @Transactional
    public GroupEntity removeStudent(Long groupId, Long studentId) {
        logger.info("Удаление студента {} из группы {}", studentId, groupId);

        GroupEntity group = self.get(groupId);

        UserEntity user = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, studentId));

        user.setGroup(null);
        userRepository.save(user);

        group.getStudents().removeIf(u -> Objects.equals(u.getId(), studentId));

        GroupEntity updated = repository.save(group);

        logger.info("Студент {} успешно удалён из группы {}", studentId, groupId);

        return updated;
    }

    @Transactional(readOnly = true)
    public Page<GroupEntity> filter(String search, Long disciplineId, Pageable pageable) {

        var searchText = Optional.ofNullable(search).orElse("");

        Page<GroupEntity> result;

        if (disciplineId != null) {
            result = repository.searchAndFilter(searchText, disciplineId, pageable);
        } else {
            result = repository.searchByText(searchText, pageable);
        }

        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<GroupEntity> filter(String search, Long disciplineId, int page, int size) {
        logger.info("Фильтрация дисциплин выполнена, page={}, pageSize={}", page, size);
        var pageable = PageRequest.of(page, size, default_sort);
        return self.filter(search, disciplineId, pageable);
    }

    public List<UserEntity> getStudentsByGroupId(Long groupId) {
        GroupEntity discipline = repository.findByIdWithStudents(
                groupId)
                .orElseThrow(() -> new EntityNotFoundException("Discipline not found with id: " + groupId));

        return new ArrayList<>(discipline.getStudents());
    }
}
