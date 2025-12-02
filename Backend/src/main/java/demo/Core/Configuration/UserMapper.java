package demo.Core.Configuration;

import demo.DTO.UserDto;
import demo.Models.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "token", ignore = true)
    UserDto toUserDto(UserEntity user);

}
