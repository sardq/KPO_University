package demo.services;

import demo.core.error.NotFoundException;
import demo.models.ExerciseEntity;
import demo.models.GroupEntity;
import demo.models.DisciplineEntity;
import demo.repositories.ExerciseRepository;
import demo.repositories.GroupRepository;
import demo.repositories.DisciplineRepository;
import demo.dto.ExerciseDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);
    private static final String LOG_RESPONSE = "Ответ: {}";
    private static final Sort DEFAULT_SORT = Sort.by("date").descending();

    private final ExerciseRepository repository;
    private final GroupRepository groupRepository;
    private final DisciplineRepository disciplineRepository;
    private final ExerciseService self;

    public ExerciseService(ExerciseRepository repository,
            GroupRepository groupRepository,
            DisciplineRepository disciplineRepository,
            @Lazy ExerciseService self) {
        this.repository = repository;
        this.groupRepository = groupRepository;
        this.disciplineRepository = disciplineRepository;
        this.self = self;
    }

    @Transactional(readOnly = true)
    public List<ExerciseEntity> getAll() {
        logger.info("Получение списка занятий");
        var result = repository.findAll(PageRequest.of(0, Integer.MAX_VALUE, DEFAULT_SORT)).getContent();
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<ExerciseEntity> getAll(int page, int size) {
        logger.info("Получение списка занятий: page={}, size={}", page, size);
        var pageable = PageRequest.of(page, size, DEFAULT_SORT);
        var result = repository.findAll(pageable);
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public ExerciseEntity get(Long id) {
        logger.info("Получение занятия: {}", id);
        var result = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExerciseEntity.class, id));
        logger.info(LOG_RESPONSE, result);
        return result;
    }

    @Transactional(readOnly = true)
    public List<ExerciseEntity> getByDisciplineAndGroup(Long disciplineId, Long groupId) {
        logger.info("Получение занятий по дисциплине {} и группе {}", disciplineId, groupId);
        var result = repository.findByDisciplineIdAndGroupId(disciplineId, groupId);
        logger.info("Найдено {} занятий", result.size());
        return result;
    }

    @Transactional
    public ExerciseEntity create(ExerciseDto dto) {
        logger.info("Создание нового занятия для группы {}, дисциплины {}",
                dto.getGroupId(), dto.getDisciplineId());

        GroupEntity group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new NotFoundException(GroupEntity.class, dto.getGroupId()));

        DisciplineEntity discipline = disciplineRepository.findById(dto.getDisciplineId())
                .orElseThrow(() -> new NotFoundException(DisciplineEntity.class, dto.getDisciplineId()));

        LocalDateTime date = dto.getDateAsLocalDateTime();
        if (date != null) {
            Optional<ExerciseEntity> existingExercise = repository.findByDateAndGroupIdAndDisciplineId(
                    date, dto.getGroupId(), dto.getDisciplineId());
            if (existingExercise.isPresent()) {
                throw new IllegalArgumentException(
                        String.format("Занятие с такой датой уже существует для группы и дисциплины"));
            }
        }

        ExerciseEntity entity = new ExerciseEntity();
        entity.setDate(date);
        entity.setDescription(dto.getDescription());
        entity.setGroup(group);
        entity.setDiscipline(discipline);

        var result = repository.save(entity);
        logger.info("Занятие сохранено: {}", result);
        return result;
    }

    @Transactional
    public ExerciseEntity update(Long id, ExerciseDto dto) {
        logger.info("Попытка обновить занятие: {}", id);
        var existing = self.get(id);

        if (!existing.getGroup().getId().equals(dto.getGroupId())) {
            GroupEntity newGroup = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new NotFoundException(GroupEntity.class, dto.getGroupId()));
            existing.setGroup(newGroup);
        }

        if (!existing.getDiscipline().getId().equals(dto.getDisciplineId())) {
            DisciplineEntity newDiscipline = disciplineRepository.findById(dto.getDisciplineId())
                    .orElseThrow(() -> new NotFoundException(DisciplineEntity.class, dto.getDisciplineId()));
            existing.setDiscipline(newDiscipline);
        }

        LocalDateTime newDate = dto.getDateAsLocalDateTime();
        if (newDate != null && !newDate.equals(existing.getDate())) {
            Long checkGroupId = existing.getGroup().getId();
            Long checkDisciplineId = existing.getDiscipline().getId();

            Optional<ExerciseEntity> duplicate = repository.findByDateAndGroupIdAndDisciplineId(
                    newDate, checkGroupId, checkDisciplineId);
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException(
                        String.format("Занятие с такой датой уже существует для группы %s и дисциплины %s",
                                existing.getGroup().getName(), existing.getDiscipline().getName()));
            }
            existing.setDate(newDate);
        }

        existing.setDescription(dto.getDescription());

        var result = repository.save(existing);
        logger.info("Занятие обновлено: {}", result);
        return result;
    }

    @Transactional
    public ExerciseEntity delete(Long id) {
        logger.info("Удаление занятия {}", id);
        var existing = self.get(id);
        repository.delete(existing);
        logger.info("Занятие удалено");
        return existing;
    }
}