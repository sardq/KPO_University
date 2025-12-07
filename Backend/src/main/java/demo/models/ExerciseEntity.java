package demo.models;

import demo.core.models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "exercises")
public class ExerciseEntity extends BaseEntity {

    @Column(name = "lesson_date", nullable = false)
    @NotNull
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    @NotNull
    private DisciplineEntity discipline;

    public ExerciseEntity() {
    }

    public ExerciseEntity(LocalDateTime date, String description, GroupEntity group, DisciplineEntity discipline) {
        this.date = date;
        this.description = description;
        this.group = group;
        this.discipline = discipline;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date =  date.withSecond(0).withNano(0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public DisciplineEntity getDiscipline() {
        return discipline;
    }

    public void setDiscipline(DisciplineEntity discipline) {
        this.discipline = discipline;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, group, discipline);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ExerciseEntity other = (ExerciseEntity) obj;
        return Objects.equals(other.getId(), id)
                && Objects.equals(other.getDate(), date)
                && Objects.equals(other.getGroup().getId(), group.getId())
                && Objects.equals(other.getDiscipline().getId(), discipline.getId());
    }
}
