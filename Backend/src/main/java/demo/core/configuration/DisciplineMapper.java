package demo.core.configuration;

import demo.dto.DisciplineDto;
import demo.models.DisciplineEntity;
import demo.repositories.GroupRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DisciplineMapper {
    
    private final GroupRepository groupRepository;
    
    public DisciplineMapper(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }
    
    public DisciplineDto toDto(DisciplineEntity entity) {
        if (entity == null) {
            return null;
        }
        
        DisciplineDto dto = new DisciplineDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        
        List<Long> groupIds = groupRepository.findByDisciplineId(entity.getId())
                .stream()
                .map(group -> group.getId())
                .collect(Collectors.toList());
        dto.setGroupIds(groupIds);
        dto.setGroupsCount(groupIds.size());
        
        return dto;
    }
    
    public DisciplineEntity toEntity(DisciplineDto dto) {
        if (dto == null) {
            return null;
        }
        
        DisciplineEntity entity = new DisciplineEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        
        return entity;
    }
}