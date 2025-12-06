package demo.core.configuration;

import demo.dto.GroupDto;
import demo.models.GroupEntity;
import demo.repositories.UserRepository;
import demo.repositories.DisciplineRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupMapper {
    
    private final UserRepository userRepository;
    private final DisciplineRepository disciplineRepository;
    
    public GroupMapper(UserRepository userRepository, DisciplineRepository disciplineRepository) {
        this.userRepository = userRepository;
        this.disciplineRepository = disciplineRepository;
    }
    
    public GroupDto toDto(GroupEntity entity) {
        if (entity == null) {
            return null;
        }
        
        GroupDto dto = new GroupDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        
        List<Long> studentIds = userRepository.findByGroupId(entity.getId())
                .stream()
                .map(user -> user.getId())
                .collect(Collectors.toList());
        dto.setStudentIds(studentIds);
        dto.setStudentsCount(studentIds.size());
        
        List<Long> disciplineIds = disciplineRepository.findByGroupId(entity.getId())
                .stream()
                .map(discipline -> discipline.getId())
                .collect(Collectors.toList());
        dto.setDisciplineIds(disciplineIds);
        
        return dto;
    }
    
    public GroupEntity toEntity(GroupDto dto) {
        if (dto == null) {
            return null;
        }
        
        GroupEntity entity = new GroupEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        
        return entity;
    }
}