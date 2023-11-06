package momongo12.fintech.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import momongo12.fintech.api.dto.ErrorResponse;
import momongo12.fintech.api.dto.WeatherDto;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author momongo12
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCurrentTemperatureWithWrongCredentials() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/regionName"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetCurrentTemperatureWithUserRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateNewRegionWithWrongCredentials() throws Exception{
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateNewRegionWithAdminRole() throws Exception {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setRegionName("region1");
        weatherDto.setMeasuringDate(Instant.now());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weatherDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateNewRegionWithUserRole() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access Denied");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }

    @Test
    public void testUpdateTemperatureWithWrongCredentials() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/weather/regionName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateTemperatureWithAdminRole() throws Exception {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setRegionName("notExistRegionName");
        weatherDto.setTemperatureValue(10.0);
        weatherDto.setMeasuringDate(Instant.now());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/weather/notExistRegionName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weatherDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateTemperatureWithUserRole() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access Denied");


        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/weather/notExistRegionName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }

    @Test
    public void testDeleteRegionWithWrongCredentials() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/weather/region1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteRegionWithAdminRole() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "No temperature data was found for this city");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/weather/region1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteRegionWithUserRole() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access Denied");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/weather/region1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("$.message", is(errorResponse.getMessage())));
    }
}