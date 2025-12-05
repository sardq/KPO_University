package demo.core.configuration;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import demo.dto.GroupDto;
import demo.models.DisciplineEntity;
import demo.models.GroupEntity;
import demo.models.UserEntity;

@Component
public class GroupMapper {

    private final ModelMapper modelMapper;

    public GroupMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public GroupDto toDto(GroupEntity entity) {
        if (entity == null)
            return null;

        GroupDto dto = modelMapper.map(entity, GroupDto.class);

        if (entity.getStudents() != null) {
            List<Long> studentIds = entity.getStudents().stream()
                    .map(UserEntity::getId)
                    .collect(Collectors.toList());
            dto.setStudentIds(studentIds);
        }

        if (entity.getDisciplines() != null) {
            List<Long> disciplineIds = entity.getDisciplines().stream()
                    .map(DisciplineEntity::getId)
                    .collect(Collectors.toList());
            dto.setDisciplineIds(disciplineIds);
        }

        return dto;
    }

    public GroupEntity toEntity(GroupDto dto) {
        return modelMapper.map(dto, GroupEntity.class);
    }
}
