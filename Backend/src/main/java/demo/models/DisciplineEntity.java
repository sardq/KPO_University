package demo.models;

import demo.core.models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "disciplines")
public class DisciplineEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @ManyToMany(mappedBy = "disciplines")
    private Set<GroupEntity> groups = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "discipline_teachers", joinColumns = @JoinColumn(name = "discipline_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private Set<UserEntity> teachers = new HashSet<>();

    public DisciplineEntity() {
    }

    public DisciplineEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<UserEntity> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<UserEntity> teachers) {
        this.teachers = teachers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<GroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupEntity> groups) {
        this.groups = groups;
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
        DisciplineEntity other = (DisciplineEntity) obj;
        return Objects.equals(other.getId(), id)
                && Objects.equals(other.getName(), name);
    }
}
