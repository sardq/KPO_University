package demo.services;

import demo.core.error.NotFoundException;
import demo.dto.GradeDto;
import demo.models.GradeEntity;
import demo.models.GradeEnum;
import demo.models.ExerciseEntity;
import demo.models.UserEntity;
import demo.repositories.GradeRepository;
import demo.repositories.ExerciseRepository;
import demo.repositories.UserRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GradeService {

    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);

    private final GradeRepository repository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository studentRepository;
    private final GradeService self;

    public GradeService(GradeRepository repository,
            ExerciseRepository exerciseRepository,
            UserRepository studentRepository,
            @Lazy GradeService self) {
        this.repository = repository;
        this.exerciseRepository = exerciseRepository;
        this.studentRepository = studentRepository;
        this.self = self;
    }

    @Transactional(readOnly = true)
    public Page<GradeEntity> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public GradeEntity get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(GradeEntity.class, id));
    }

    @Transactional(readOnly = true)
    public GradeEntity getByExerciseAndStudent(Long exerciseId, Long studentId) {
        return repository.findByExerciseIdAndStudentId(exerciseId, studentId)
                .orElseThrow(() -> new NotFoundException(GradeEntity.class, exerciseId));
    }

    @Transactional
    public GradeEntity create(GradeDto dto) {
        logger.info("Создание оценки: {}", dto);

        ExerciseEntity exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new NotFoundException(ExerciseEntity.class, dto.getExerciseId()));

        UserEntity student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new NotFoundException(GradeEntity.class, dto.getStudentId()));
        if (!exercise.getGroup().getStudents().contains(student)) {
            throw new IllegalArgumentException("Студент не принадлежит группе занятия");
        }

        GradeEnum value = GradeEnum.fromCode(dto.getValue());

        repository.findExisting(dto.getExerciseId(), dto.getStudentId())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("Оценка уже существует");
                });

        GradeEntity entity = new GradeEntity();
        entity.setExercise(exercise);
        entity.setStudent(student);
        entity.setValue(value);
        entity.setDescription(dto.getDescription());

        return repository.save(entity);
    }

    @Transactional
    public GradeEntity update(Long id, GradeDto dto) {
        logger.info("Обновление оценки {} -> {}", id, dto);

        GradeEntity existing = self.get(id);

        if (!existing.getExercise().getId().equals(dto.getExerciseId())) {
            ExerciseEntity newEx = exerciseRepository.findById(dto.getExerciseId())
                    .orElseThrow(() -> new NotFoundException(ExerciseEntity.class, dto.getExerciseId()));
            existing.setExercise(newEx);
        }

        if (!existing.getStudent().getId().equals(dto.getStudentId())) {
            UserEntity newSt = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new NotFoundException(UserEntity.class, dto.getStudentId()));
            existing.setStudent(newSt);
        }
        if (!existing.getExercise().getGroup().getStudents().contains(existing.getStudent())) {
            throw new IllegalArgumentException("Студент не принадлежит группе занятия");
        }
        existing.setDescription(dto.getDescription());
        existing.setValue(GradeEnum.fromCode(dto.getValue()));

        return repository.save(existing);
    }

    @Transactional
    public GradeEntity delete(Long id) {
        GradeEntity existing = self.get(id);
        repository.delete(existing);
        return existing;
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getByGroupAndDiscipline(Long groupId, Long disciplineId) {
        return repository.findAllByGroupAndDiscipline(groupId, disciplineId)
                .stream()
                .map(GradeDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Double getStudentAverageByDiscipline(Long studentId, Long disciplineId) {
        return repository.getStudentAverageByDiscipline(studentId, disciplineId);
    }

    @Transactional(readOnly = true)
    public Double getDisciplineAverage(Long disciplineId) {
        return repository.getDisciplineAverage(disciplineId);
    }

    @Transactional(readOnly = true)
    public Double getGroupAverage(Long groupId, Long disciplineId) {
        return repository.getStudentsAverages(groupId, disciplineId);
    }

    @Transactional(readOnly = true)
    public List<GradeEntity> getStudentGradesByDiscipline(Long studentId, Long disciplineId) {
        return repository.findByStudentIdAndDisciplineId(studentId, disciplineId);
    }

}
