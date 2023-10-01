package momongo12.fintech.store;


import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Momongo12
 * @version 1.0
 */
@SpringBootTest
public class WeatherRepositoryImplTest {

    private final WeatherRepository weatherRepository;
    private final WeatherFactory weatherFactory;

    @Autowired
    public WeatherRepositoryImplTest(WeatherRepository weatherRepository, WeatherFactory weatherFactory) {
        this.weatherRepository = weatherRepository;
        this.weatherFactory = weatherFactory;
    }


    @Test
    void testAddWeatherDataAndFindTemperatureDataByRegionId() {
        Weather weather = weatherFactory.createWeather("region1", 5.0);
        weatherRepository.addWeatherData(weather);

        assertTrue(weatherRepository
                .findTemperatureDataByRegionId(weather.getRegionId())
                .anyMatch(w -> Double.compare(w.getTemperatureValue(), weather.getTemperatureValue()) == 0));
    }

    @Test
    void testFindTemperatureDataByRegionIdNotFound() {
        assertFalse(weatherRepository.findTemperatureDataByRegionId(100).findFirst().isPresent());
    }

    @Test
    void testFindWeatherByRegionIdNameAndMeasuringDate() {
        Weather weather = weatherFactory.createWeather("region2", 10.0);
        weatherRepository.addWeatherData(weather);

        assertTrue(weatherRepository.findWeatherByRegionIdAndMeasuringDate(weather.getRegionId(), weather.getMeasuringDate()).isPresent());
    }

    @Test
    void testFindWeatherByRegionIdNameAndMeasuringDateNotFound() {
        assertFalse(weatherRepository.findWeatherByRegionIdAndMeasuringDate(2, Instant.now()).isPresent());
    }

    @Test
    void testDeleteWeatherDataByRegionId() {
        Weather weather = weatherFactory.createWeather("region3", 11.0);
        weatherRepository.addWeatherData(weather);

        weatherRepository.deleteWeatherDataByRegionId(weather.getRegionId());

        assertFalse(weatherRepository.findTemperatureDataByRegionId(1).findFirst().isPresent());
    }

    @Test
    void testDeleteWeatherDataByRegionIdNotFound() {
        assertThrows(NoSuchElementException.class, () -> weatherRepository.deleteWeatherDataByRegionId(1000));
    }
}
