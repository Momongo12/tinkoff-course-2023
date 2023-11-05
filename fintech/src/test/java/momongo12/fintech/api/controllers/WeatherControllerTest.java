package momongo12.fintech.api.controllers;

import momongo12.fintech.api.dto.WeatherDto;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

/**
 * @author momongo12
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetWeatherByRegionName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateNewWeather() throws Exception {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setRegionName("region1");
        weatherDto.setMeasuringDate(Instant.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weatherDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testCreateDuplicateWeather() throws Exception {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setRegionName("region1");
        weatherDto.setTemperatureValue(10.0);
        weatherDto.setMeasuringDate(Instant.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weatherDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/weather/{regionName}", "region1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weatherDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
