package momongo12.fintech.api.controllers;

import lombok.RequiredArgsConstructor;

import momongo12.fintech.api.controllers.exceptions.InternalServerErrorException;
import momongo12.fintech.api.dto.RegistrationDto;
import momongo12.fintech.api.dto.UserDto;
import momongo12.fintech.api.mappers.UserMapper;
import momongo12.fintech.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author momongo12
 * @version 1.0
 */
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody RegistrationDto registrationDto) {
        return userService
                .createUser(registrationDto)
                .map((user -> ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userToUserDto(user))))
                .orElseThrow(() -> new InternalServerErrorException("Internal еrror while create new user"));
    }
}
