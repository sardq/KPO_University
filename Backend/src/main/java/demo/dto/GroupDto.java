package demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import java.util.List;

public class GroupDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String name;
    @JsonProperty(access = Access.READ_ONLY)
    private List<Long> studentIds;
    @JsonProperty(access = Access.READ_ONLY)
    private List<Long> disciplineIds;
    private int studentsCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

    public List<Long> getDisciplineIds() {
        return disciplineIds;
    }

    public void setDisciplineIds(List<Long> disciplineIds) {
        this.disciplineIds = disciplineIds;
    }

    public int getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(int studentsCount) {
        this.studentsCount = studentsCount;
    }
}
