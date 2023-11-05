package momongo12.fintech.api.mappers;

import momongo12.fintech.api.dto.UserDto;
import momongo12.fintech.store.entities.User;

import org.mapstruct.Mapper;

/**
 * @author momongo12
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);
}
