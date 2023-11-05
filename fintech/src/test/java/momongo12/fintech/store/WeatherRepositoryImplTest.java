package momongo12.fintech.store;


import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.impl.WeatherRepositoryImpl;
import momongo12.fintech.utils.WeatherFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Momongo12
 * @version 1.1
 */
@SpringBootTest
public class WeatherRepositoryImplTest {

    private final WeatherRepositoryImpl weatherRepository;
    private final WeatherFactory weatherFactory;

    @Autowired
    public WeatherRepositoryImplTest(@Qualifier("WeatherHeapRepository") WeatherRepositoryImpl weatherRepository, WeatherFactory weatherFactory) {
        this.weatherRepository = weatherRepository;
        this.weatherFactory = weatherFactory;
    }


    @Test
    void testSaveAndFindTemperatureDataByRegionId() {
        Weather weather = weatherFactory.createWeather("region1", 5.0);
        weatherRepository.addWeatherData(weather);

        assertTrue(weatherRepository
                .findTemperatureDataByRegionId(weather.getRegion().getId())
                .stream()
                .anyMatch(w -> Double.compare(w.getTemperatureValue(), weather.getTemperatureValue()) == 0));
    }

    @Test
    void testFindTemperatureDataByRegionIdNotFound() {
        assertFalse(weatherRepository.findTemperatureDataByRegionId(100).stream().findFirst().isPresent());
    }

    @Test
    void testFindWeatherByRegionIdAndMeasuringDate() {
        Weather weather = weatherFactory.createWeather("region2", 10.0);
        weatherRepository.addWeatherData(weather);

        assertTrue(weatherRepository.findWeatherByRegionIdAndMeasuringDate(weather.getRegion().getId(), weather.getMeasuringDate()).isPresent());
    }

    @Test
    void testFindWeatherByRegionIdAndMeasuringDateNotFound() {
        assertFalse(weatherRepository.findWeatherByRegionIdAndMeasuringDate(2, Instant.now()).isPresent());
    }

    @Test
    void testDeleteWeatherDataByRegionId() {
        Weather weather = weatherFactory.createWeather("region3", 11.0);
        weatherRepository.addWeatherData(weather);

        weatherRepository.deleteWeatherDataByRegionId(weather.getRegion().getId());

        assertFalse(weatherRepository.findTemperatureDataByRegionId(weather.getRegion().getId()).stream().findFirst().isPresent());
    }

    @Test
    void testDeleteWeatherDataByRegionIdNotFound() {
        assertThrows(NoSuchElementException.class, () -> weatherRepository.deleteWeatherDataByRegionId(1000));
    }
}
