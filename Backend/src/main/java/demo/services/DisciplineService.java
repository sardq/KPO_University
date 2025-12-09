package demo.services;

import demo.core.error.NotFoundException;
import demo.models.DisciplineEntity;
import demo.models.ExerciseEntity;
import demo.models.GroupEntity;
import demo.models.UserEntity;
import demo.models.UserRole;
import demo.repositories.DisciplineRepository;
import demo.repositories.GroupRepository;
import demo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisciplineService {

    private static final Logger logger = LoggerFactory.getLogger(DisciplineService.class);
    private static final String LOG_RESPONSE = "Ответ: {}";

    private final DisciplineRepository repository;
    private final DisciplineService self;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public DisciplineService(DisciplineRepository repository, @Lazy DisciplineService self,
            GroupRepository groupRepository, UserRepository userRepository) {
        this.repository = repository;
        this.self = self;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<DisciplineEntity> getAll(int page, int size) {
        logger.info("Получение дисциплин: {}, {}", page, size);
        var result = repository.findAll(PageRequest.of(page, size));
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public DisciplineEntity get(Long id) {
        logger.info("Получение дисциплины {}", id);
        var result = repository.findByIdWithGroups(id)
                .orElseThrow(() -> new NotFoundException(DisciplineEntity.class, id));
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional
    public DisciplineEntity create(DisciplineEntity entity) {
        logger.info("Создание новой дисциплины с именем, длиной {} символов",
                entity.getName() != null ? entity.getName().length() : 0);
        if (repository.findByName(entity.getName()).isPresent()) {
            throw new IllegalArgumentException("Discipline already exists");
        }
        var result = repository.save(entity);
        logger.info("Дисциплина сохранена {}", result);
        return result;
    }

    @Transactional
    public DisciplineEntity update(Long id, DisciplineEntity entity) {
        logger.info("Обновление дисциплины {}", id);
        var exists = self.get(id);

        if (!exists.getName().equals(entity.getName())
                && repository.findByName(entity.getName()).isPresent()) {
            throw new IllegalArgumentException("Дисциплина с таким названием уже существует");
        }

        exists.setName(entity.getName());
        exists.setGroups(entity.getGroups());

        repository.save(exists);
        logger.info("Дисциплина обновлена {}", exists);
        return exists;
    }

    @Transactional
    public DisciplineEntity delete(Long id) {
        DisciplineEntity discipline = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Дисциплина не найдена"));

        for (GroupEntity group : discipline.getGroups()) {
            group.getDisciplines().remove(discipline);
        }
        discipline.getGroups().clear();

        discipline.getTeachers().clear();
        for (ExerciseEntity exercise : new HashSet<>(discipline.getGroups()
                .stream()
                .flatMap(g -> g.getExercises().stream())
                .filter(e -> e.getDiscipline().equals(discipline))
                .toList())) {
            exercise.getGroup().getExercises().remove(exercise);
        }
        repository.delete(discipline);
        return discipline;
    }

    @Transactional
    public DisciplineEntity addGroup(Long disciplineId, Long groupId) {
        logger.info("Добавление группы {} к дисциплине {}", groupId, disciplineId);

        DisciplineEntity discipline = self.get(disciplineId);
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(GroupEntity.class, groupId));

        discipline.getGroups().add(group);
        repository.save(discipline);
        group.getDisciplines().add(discipline);
        groupRepository.save(group);

        logger.info("Группа добавлена к дисциплине");
        return discipline;
    }

    @Transactional
    public DisciplineEntity removeGroup(Long disciplineId, Long groupId) {
        logger.info("Удаление группы {} из дисциплины {}", groupId, disciplineId);

        DisciplineEntity discipline = self.get(disciplineId);
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(GroupEntity.class, groupId));

        boolean removedFromDiscipline = discipline.getGroups().remove(group);
        boolean removedFromGroup = group.getDisciplines().remove(discipline);

        if (removedFromDiscipline || removedFromGroup) {
            repository.save(discipline);
            logger.info("Группа удалена из дисциплины");
        } else {
            logger.warn("Группа {} не найдена в дисциплине {}", groupId, disciplineId);
        }

        return discipline;
    }

    @Transactional(readOnly = true)
    public Page<DisciplineEntity> getAllByFilters(String search, Long groupId, int page, int size) {
        logger.info("Фильтрация дисциплин выполнена, page={}, pageSize={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<DisciplineEntity> result;

        if (groupId != null && search != null && !search.isEmpty()) {
            result = repository.searchAndFilter(search, groupId, pageable);
        } else if (search != null && !search.isEmpty()) {
            result = repository.searchByText(search, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        logger.info(LOG_RESPONSE, result);
        return result;
    }

    public List<GroupEntity> getGroupsByDisciplineId(Long disciplineId) {
        DisciplineEntity discipline = repository.findByIdWithGroups(disciplineId)
                .orElseThrow(() -> new EntityNotFoundException("Discipline not found with id: " + disciplineId));

        return new ArrayList<>(discipline.getGroups());
    }

    @Transactional
    public DisciplineEntity addTeacher(Long disciplineId, Long teacherId) {
        DisciplineEntity discipline = self.get(disciplineId);
        UserEntity teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, teacherId));

        if (!teacher.getRole().equals(UserRole.TEACHER)) {
            throw new IllegalArgumentException("Пользователь не имеет роль TEACHER");
        }

        discipline.getTeachers().add(teacher);
        return repository.save(discipline);
    }

    @Transactional
    public DisciplineEntity removeTeacher(Long disciplineId, Long teacherId) {
        DisciplineEntity discipline = self.get(disciplineId);
        UserEntity teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, teacherId));

        discipline.getTeachers().remove(teacher);
        return repository.save(discipline);
    }

    @Transactional(readOnly = true)
    public List<DisciplineEntity> getDisciplinesByTeacher(Long teacherId) {
        return repository.findByTeacherId(teacherId);
    }
}
