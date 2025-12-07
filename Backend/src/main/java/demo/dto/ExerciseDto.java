package demo.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ExerciseDto {

    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    private String description;
    @NotNull
    private Long groupId;
    @NotNull
    private Long disciplineId;
    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")
    private String date;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public LocalDateTime getDateAsLocalDateTime() {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    String.format("Неверный формат даты: %s. Ожидается: yyyy-MM-dd'T'HH:mm", date), e);
        }
    }

    public void setDateFromLocalDateTime(LocalDateTime dateTime) {
        this.date = dateTime.format(FORMATTER);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(Long disciplineId) {
        this.disciplineId = disciplineId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
