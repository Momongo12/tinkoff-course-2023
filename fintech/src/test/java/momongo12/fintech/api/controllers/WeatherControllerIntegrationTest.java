package momongo12.fintech.api.controllers;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import momongo12.fintech.api.dto.ErrorResponse;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.api.mappers.WeatherMapper;
import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.RegionRepository;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.List;

/**
 * @author Momongo12
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class WeatherControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    @Qualifier("weatherRepositoryForWeatherServiceImpl")
    private WeatherRepository weatherRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private WeatherFactory weatherFactory;

    @Autowired
    private WeatherMapper weatherMapper;

    private Weather testWeatherObject;

    @Container
    public static GenericContainer h2 = new GenericContainer(DockerImageName.parse("oscarfonts/h2"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    public static void setPropertySource(DynamicPropertyRegistry dynamicPropertySource) {
        dynamicPropertySource.add("spring.datasource.url",
                () -> "jdbc:h2:tcp://localhost:%d/test".formatted(h2.getMappedPort(1521)));
    }

    @PostConstruct
    public void init () {
        Region region = Region.builder().id(1).name("regionName").build();
        regionRepository.saveAndFlush(region);
        testWeatherObject = Weather.builder().region(region).temperatureValue(10.0).measuringDate(Instant.now()).build();
        weatherRepository.addWeatherData(testWeatherObject);
    }

    @BeforeEach
    public void setUp() {
        weatherRepository.addWeatherData(testWeatherObject);
    }

    @AfterEach
    public void cleanUp() {
        weatherRepository.deleteWeatherDataByRegionId(testWeatherObject.getRegion().getId());
    }

    @Test
    public void testGetCurrentTemperatureWithExistingData() {
        ResponseEntity<List<WeatherDto>> responseEntity = restTemplate.exchange(
                createURL("/api/weather/" + testWeatherObject.getRegion().getName()),
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
                .postForEntity(createURL("/api/weather/" + testWeatherObject.getRegion().getName()),
                        weatherMapper.weatherToWeatherDto(testWeatherObject),
                            ErrorResponse.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), responseEntity.getBody().getStatusCode());
    }

    @Test
    public void testCreateNewRegionWhenDataDoesNotExist() {
        WeatherDto weatherDto = createTestWeatherDto(1, "NotExistRegion",
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
        WeatherDto weatherDto = createTestWeatherDto(1, testWeatherObject.getRegion().getName(),
                testWeatherObject.getMeasuringDate(), 5.0);

        ResponseEntity<WeatherDto> responseEntity = restTemplate.exchange(
                createURL("/api/weather/" + weatherDto.getRegionName()),
                HttpMethod.PUT,
                new HttpEntity<>(weatherDto),
                WeatherDto.class);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(weatherDto, responseEntity.getBody());
    }

    @Test
    public void testUpdateTemperatureWhenDataDoesNotExist() {
        WeatherDto weatherDto = createTestWeatherDto(1, "NotExistRegion", Instant.now(), 5.0);

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
                createURL("/api/weather/" + weather.getRegion().getName()),
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
