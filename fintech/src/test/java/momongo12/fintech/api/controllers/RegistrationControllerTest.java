package momongo12.fintech.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import momongo12.fintech.api.dto.ErrorResponse;
import momongo12.fintech.api.dto.RegistrationDto;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author momongo12
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DirtiesContext
    public void testRegisterUser() throws Exception {
        RegistrationDto registrationDto = RegistrationDto.builder()
                .username("testUser")
                .login("testLogin")
                .password("testPassword")
                .build();

        mockMvc.perform(post("/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.login").value("testLogin"));
    }

    @Test
    @DirtiesContext
    public void testRegisterUserWithExistLogin() throws Exception {
        RegistrationDto registrationDto = RegistrationDto.builder()
                .username("testUser")
                .login("testLogin")
                .password("testPassword")
                .build();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(),"User with passed login exist");

        mockMvc.perform(post("/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)));

        mockMvc.perform(post("/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorResponse.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(errorResponse.getStatusCode()));
    }
}