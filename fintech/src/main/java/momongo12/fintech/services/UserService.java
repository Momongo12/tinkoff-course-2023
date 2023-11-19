package momongo12.fintech.services;

import momongo12.fintech.api.dto.RegistrationDto;
import momongo12.fintech.store.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * @author momongo12
 * @version 1.0
 */
public interface UserService extends UserDetailsService {

    Optional<User> createUser(RegistrationDto userDto);
}
