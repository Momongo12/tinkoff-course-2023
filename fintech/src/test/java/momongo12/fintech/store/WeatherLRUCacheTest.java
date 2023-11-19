package momongo12.fintech.store;

import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author momongo12
 * @version 1.0
 */
class WeatherLRUCacheTest {

    private final WeatherLRUCache weatherLRUCache;

    WeatherLRUCacheTest() {
        weatherLRUCache = new WeatherLRUCache();
        weatherLRUCache.setMaxCacheSize(100);
    }

    @Test
    void testGetWhenWeatherDataForRegionNameNotExistShouldReturnOptionalEmpty() {
        String regionName = "regionName";

        Optional<Weather> weatherOptional = weatherLRUCache.get(regionName);

        assertTrue(weatherOptional.isEmpty());
    }

    @Test
    void testGetWhenWeatherDataForRegionNameExistShouldReturnOptionalWeather() {
        String regionName = "regionName";
        Weather weather = Weather.builder().region(Region.builder().name(regionName).build()).build();
        weatherLRUCache.put(regionName, weather);

        Optional<Weather> weatherOptional = weatherLRUCache.get(regionName);

        assertTrue(weatherOptional.isPresent());
        assertEquals(regionName, weatherOptional.get().getRegion().getName());
    }

    @Test
    void testPutWeatherDataWhenWeatherDataForRegionNameNotExist() {
        String regionName = "regionName";
        Weather weather = Weather.builder().region(Region.builder().name(regionName).build()).build();

        weatherLRUCache.put(regionName, weather);

        Optional<Weather> weatherOptional = weatherLRUCache.get(regionName);
        assertTrue(weatherOptional.isPresent());
        assertEquals(regionName, weatherOptional.get().getRegion().getName());
    }

    @Test
    void testPutWeatherDataWhenWeatherDataForReginNameExistShouldUpdateWeatherData() {
        String regionName = "regionName";
        double newTemperatureValue = 20.0;
        Weather weather = Weather.builder().region(Region.builder().name(regionName).build()).temperatureValue(10.0).build();
        weatherLRUCache.put(regionName, weather);
        weather.setTemperatureValue(newTemperatureValue);

        weatherLRUCache.put(regionName, weather);

        Optional<Weather> weatherOptional = weatherLRUCache.get(regionName);
        assertTrue(weatherOptional.isPresent());
        assertEquals(newTemperatureValue, weatherOptional.get().getTemperatureValue());
    }
}