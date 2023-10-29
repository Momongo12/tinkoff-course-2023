package momongo12.fintech.store;

import jakarta.annotation.PostConstruct;

import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.RegionRepository;
import momongo12.fintech.store.repositories.WeatherRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author momongo12
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class WeatherJdbcRepositoryTest {

    @Container
    public static GenericContainer h2 = new GenericContainer(DockerImageName.parse("oscarfonts/h2"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .waitingFor(Wait.defaultWaitStrategy());

    @Autowired
    @Qualifier("WeatherJdbcRepository")
    private WeatherRepository weatherRepository;

    @Autowired
    private RegionRepository regionRepository;
    
    private Region testRegion;

    @DynamicPropertySource
    public static void setPropertySource(DynamicPropertyRegistry dynamicPropertySource) {
        dynamicPropertySource.add("spring.datasource.url",
                () -> "jdbc:h2:tcp://localhost:%d/test".formatted(h2.getMappedPort(1521)));
    }
    
    @PostConstruct
    public void init() {
        testRegion = Region.builder().id(1).name("nsk").build();

        regionRepository.save(testRegion);
    }
    
    @Test
    public void testFindTemperatureDataByRegionIdWithNotExistData() {
        List<Weather> weatherList = weatherRepository.findTemperatureDataByRegionId(100);

        assertTrue(weatherList.isEmpty());
    }
    
    @Test
    public void testAddWeatherData() {
        Weather weather = Weather.builder().id(1).region(testRegion).temperatureValue(10.0).measuringDate(Instant.now()).build();

        weatherRepository.addWeatherData(weather);

        assertNotNull(weatherRepository.findTemperatureDataByRegionId(testRegion.getId()).get(0));
    }

    @Test
    public void testDeleteWeatherDataByNotExistRegionId() {

        assertThrows(NoSuchElementException.class, () -> {
            weatherRepository.deleteWeatherDataByRegionId(100);
        });
    }
}
