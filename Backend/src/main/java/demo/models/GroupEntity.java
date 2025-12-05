package demo.models;

import demo.core.models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "groups")
public class GroupEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @OneToMany
    @JoinColumn(name = "group_id")
    private Set<UserEntity> students = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "group_disciplines", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "discipline_id"))
    private Set<DisciplineEntity> disciplines = new HashSet<>();

    public GroupEntity() {
    }

    public GroupEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserEntity> getStudents() {
        return students;
    }

    public void setStudents(Set<UserEntity> students) {
        this.students = students;
    }

    public Set<DisciplineEntity> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(Set<DisciplineEntity> disciplines) {
        this.disciplines = disciplines;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GroupEntity other = (GroupEntity) obj;
        return Objects.equals(other.getId(), id)
                && Objects.equals(other.getName(), name);
    }
}
