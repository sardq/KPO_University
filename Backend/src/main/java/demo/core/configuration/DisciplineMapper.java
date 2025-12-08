package demo.core.configuration;

import demo.core.models.BaseEntity;
import demo.dto.DisciplineDto;
import demo.models.DisciplineEntity;
import demo.repositories.GroupRepository;
import demo.repositories.UserRepository;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DisciplineMapper {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public DisciplineMapper(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
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
                .map(BaseEntity::getId)
                .toList();
        dto.setGroupIds(groupIds);
        dto.setGroupsCount(groupIds.size());
        dto.setTeacherIds(
                userRepository.findTeachersByDisciplineId(entity.getId())
                        .stream()
                        .map(BaseEntity::getId)
                        .toList());

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