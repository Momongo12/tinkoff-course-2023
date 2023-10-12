package momongo12.fintech.api.controllers;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import momongo12.fintech.api.dto.ErrorResponse;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.api.mappers.WeatherMapper;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

/**
 * @author Momongo12
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeatherControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    WeatherFactory weatherFactory;

    @Autowired
    WeatherMapper weatherMapper;

    Weather testWeatherObject;

    @PostConstruct
    public void init () {
        testWeatherObject = weatherFactory.createWeather("regionName", 10.0);
    }

    @BeforeEach
    public void setUp() {
        weatherRepository.addWeatherData(testWeatherObject);
    }

    @AfterEach
    public void cleanUp() {
        weatherRepository.deleteWeatherDataByRegionId(testWeatherObject.getRegionId());
    }

    @Test
    public void testGetCurrentTemperatureWithExistingData() {
        ResponseEntity<List<WeatherDto>> responseEntity = restTemplate.exchange(
                createURL("/api/weather/" + testWeatherObject.getRegionName()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertFalse(responseEntity.getBody().isEmpty());
        assertEquals(weatherMapper.weatherToWeatherDto(testWeatherObject), responseEntity.getBody().get(0));
    }

    @Test
    public void testGetCurrentTemperatureWithNoData() {
        String regionName = "NonExistentRegion";

        ResponseEntity<ErrorResponse> responseEntity = restTemplate.getForEntity(createURL("/api/weather/" + regionName), ErrorResponse.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateNewRegionWhenDataExists() {
        ResponseEntity<ErrorResponse> responseEntity = restTemplate
                .postForEntity(createURL("/api/weather/" + testWeatherObject.getRegionName()),
                        weatherMapper.weatherToWeatherDto(testWeatherObject),
                            ErrorResponse.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), responseEntity.getBody().getStatusCode());
    }

    @Test
    public void testCreateNewRegionWhenDataDoesNotExist() {
        WeatherDto weatherDto = createTestWeatherDto(2, "NotExistRegion",
                Instant.now(), 10.0);

        ResponseEntity<WeatherDto> responseEntity = restTemplate
                .postForEntity(createURL("/api/weather/" + weatherDto.getRegionName()),
                        weatherDto,
                        WeatherDto.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(weatherDto, responseEntity.getBody());
    }

    @Test
    public void testUpdateTemperatureWhenDataExists() {
        WeatherDto weatherDto = createTestWeatherDto(1, testWeatherObject.getRegionName(),
                testWeatherObject.getMeasuringDate(), 5.0);

        ResponseEntity<WeatherDto> responseEntity = restTemplate.exchange(
                createURL("/api/weather/" + weatherDto.getRegionName()),
                HttpMethod.PUT,
                new HttpEntity<>(weatherDto),
                WeatherDto.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(weatherDto, responseEntity.getBody());
    }

    @Test
    public void testUpdateTemperatureWhenDataDoesNotExist() {
        WeatherDto weatherDto = createTestWeatherDto(2, "NotExistRegion", Instant.now(), 5.0);

        ResponseEntity<WeatherDto> responseEntity = restTemplate.exchange(
                createURL("/api/weather/" + weatherDto.getRegionName()),
                HttpMethod.PUT,
                new HttpEntity<>(weatherDto),
                WeatherDto.class);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(weatherDto, responseEntity.getBody());
    }

    @Test
    public void testDeleteExistRegionWeatherData() {
        Weather weather = weatherFactory.createWeather("NotExistRegion", 20.0);
        weatherRepository.addWeatherData(weather);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                createURL("/api/weather/" + weather.getRegionName()),
                HttpMethod.DELETE,
                null,
                String.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteNotExistRegionWeatherData() {
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.exchange(
                createURL("/api/weather/" + "NotExistRegion"),
                HttpMethod.DELETE,
                null,
                ErrorResponse.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    private String createURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private WeatherDto createTestWeatherDto(int regionId, String regionName, Instant measuringDate, double temperatureValue) {
        return WeatherDto
                .builder()
                .regionId(regionId)
                .regionName(regionName)
                .measuringDate(measuringDate)
                .temperatureValue(temperatureValue)
                .build();
    }
}
