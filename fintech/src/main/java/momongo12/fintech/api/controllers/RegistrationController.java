package momongo12.fintech.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import momongo12.fintech.api.controllers.exceptions.InternalServerErrorException;
import momongo12.fintech.api.dto.ErrorResponse;
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
@Tag(name = "Registration Controller", description = "Endpoints for registration users")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @Operation(summary = "Register user", responses = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "409", description = "User with passed login exist",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> registerUser(@RequestBody RegistrationDto registrationDto) {
        return userService
                .createUser(registrationDto)
                .map((user -> ResponseEntity.status(HttpStatus.CREATED).body(userMapper.userToUserDto(user))))
                .orElseThrow(() -> new InternalServerErrorException("Internal еrror while create new user"));
    }
}
