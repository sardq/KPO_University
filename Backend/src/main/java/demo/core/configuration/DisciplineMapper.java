package demo.core.configuration;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import demo.dto.DisciplineDto;
import demo.models.DisciplineEntity;
import demo.models.GroupEntity;

@Component
public class DisciplineMapper {

    private final ModelMapper modelMapper;

    public DisciplineMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public DisciplineDto toDto(DisciplineEntity entity) {
        if (entity == null)
            return null;

        DisciplineDto dto = modelMapper.map(entity, DisciplineDto.class);

        if (entity.getGroups() != null) {
            List<Long> groupIds = entity.getGroups().stream()
                    .map(GroupEntity::getId)
                    .collect(Collectors.toList());
            dto.setGroupIds(groupIds);
            dto.setGroupsCount(groupIds.size());
        } else {
            dto.setGroupsCount(0);
        }

        return dto;
    }

    public DisciplineEntity toEntity(DisciplineDto dto) {
        return modelMapper.map(dto, DisciplineEntity.class);
    }
}