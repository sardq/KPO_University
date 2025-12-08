package demo.dto;

import demo.models.GradeEntity;

public class GradeDto {

    private Long id;
    private String value;

    private String description;
    private Long exerciseId;
    private Long studentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public static GradeDto fromEntity(GradeEntity e) {
        GradeDto dto = new GradeDto();
        dto.setId(e.getId());
        dto.setStudentId(e.getStudent().getId());
        dto.setExerciseId(e.getExercise().getId());
        dto.setValue(e.getValue().getCode());
        return dto;
    }
}
