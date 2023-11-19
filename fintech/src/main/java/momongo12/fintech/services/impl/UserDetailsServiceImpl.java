package momongo12.fintech.services.impl;

import lombok.RequiredArgsConstructor;

import momongo12.fintech.api.controllers.exceptions.DuplicateResourceException;
import momongo12.fintech.api.dto.RegistrationDto;
import momongo12.fintech.services.UserService;
import momongo12.fintech.store.entities.Role;
import momongo12.fintech.store.entities.User;
import momongo12.fintech.store.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * @author momongo12
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository
                .findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login=%s not found".formatted(login)));
    }

    @Override
    public Optional<User> createUser(RegistrationDto registrationDto) {
        Optional<User> userOptional = userRepository.findByLogin(registrationDto.getLogin());

        if (userOptional.isPresent()) {
            throw new DuplicateResourceException("User with passed login exist");
        }

        User user = User.builder()
                .username(registrationDto.getUsername())
                .login(registrationDto.getLogin())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .roles(Collections.singleton(new Role("ROLE_USER")))
                .build();

        return Optional.of(userRepository.save(user));
    }
}
