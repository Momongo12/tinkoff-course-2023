package momongo12.fintech.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Momongo12
 * @version 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    WeatherRepository weatherRepository;

    @Mock
    WeatherFactory weatherFactory;

    @InjectMocks
    WeatherServiceImpl weatherService;

    @Test
    void testGetCurrentTemperatureByRegionName() {
        int regionId = 1;
        String regionName = "regionName";
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.findTemperatureDataByRegionId(regionId)).thenReturn(Stream.empty());

        Stream<Weather> result = weatherService.getCurrentTemperatureByRegionName(regionName);

        assertEquals(0, result.count());
        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).findTemperatureDataByRegionId(regionId);
    }

    @Test
    void testAddNewRegionWithMeasuringDate() {
        int regionId = 1;
        String regionName = "regionName";
        WeatherDto weatherDto = new WeatherDto(regionId, regionName, 10.0, Instant.now());
        Weather weather = new Weather(regionId, regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate());
        when(weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate())).thenReturn(weather);

        Optional<Weather> result = weatherService.addNewRegion(regionName, weatherDto);

        assertEquals(weather, result.orElse(null));
        verify(weatherFactory, times(1)).createWeather(regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate());
        verify(weatherRepository, times(1)).addWeatherData(weather);
    }

    @Test
    void testAddNewRegionWithoutMeasuringDate() {
        int regionId = 1;
        String regionName = "regionName";
        WeatherDto weatherDto = WeatherDto.builder().regionName(regionName).temperatureValue(10.0).build();
        Weather weather = new Weather(regionId, regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate());
        when(weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue())).thenReturn(weather);

        Optional<Weather> result = weatherService.addNewRegion(regionName, weatherDto);

        assertEquals(weather, result.orElse(null));
        verify(weatherFactory, times(1)).createWeather(regionName, weatherDto.getTemperatureValue());
        verify(weatherRepository, times(1)).addWeatherData(weather);
    }

    @Test
    void testUpdateTemperatureByRegionName() {
        int regionId = 1;
        String regionName = "regionName";
        WeatherDto weatherDto = new WeatherDto(regionId, regionName, 10.0, Instant.now());
        Weather weather = new Weather(regionId, regionName, 15.0, weatherDto.getMeasuringDate());
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.findWeatherByRegionIdAndMeasuringDate(regionId, weatherDto.getMeasuringDate())).thenReturn(Optional.of(weather));

        Optional<Weather> result = weatherService.updateTemperatureByRegionName(regionName, weatherDto);

        assertEquals(weatherDto.getTemperatureValue(), result.orElseThrow().getTemperatureValue());
        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).findWeatherByRegionIdAndMeasuringDate(regionId, weatherDto.getMeasuringDate());
    }

    @Test
    void testDeleteRegionData() {
        int regionId = 1;
        String regionName = "regionName";
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.deleteWeatherDataByRegionId(regionId)).thenReturn(1L);

        Optional<Long> result = weatherService.deleteRegionData(regionName);

        assertEquals(1L, result.orElseThrow());
        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).deleteWeatherDataByRegionId(regionId);
    }

    @Test
    void testDeleteRegionDataForNotExistRegion() {
        int regionId = 1;
        String regionName = "regionName";
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.deleteWeatherDataByRegionId(regionId)).thenThrow(new NoSuchElementException());

        Optional<Long> result = weatherService.deleteRegionData(regionName);

        assertTrue(result.isEmpty());
        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).deleteWeatherDataByRegionId(regionId);
    }

    @Test
    void testTemperatureWithExistingDateAtRegionShouldReturnTrue() {
        int regionId = 1;
        String regionName = "regionName";
        Instant date = Instant.now();
        Weather someWeather = Weather.builder().regionId(regionId).regionName(regionName).measuringDate(date).build();
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.findTemperatureDataByRegionId(regionId)).thenReturn(Stream.of(someWeather));

        assertTrue(weatherService.temperatureWithThisDateAtRegionExist(regionName, date));

        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).findTemperatureDataByRegionId(regionId);
    }

    @Test
    void testTemperatureWithNonExistingDateAtRegionShouldReturnFalse() {
        int regionId = 1;
        String regionName = "regionName";
        Instant date = Instant.now();
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.findTemperatureDataByRegionId(regionId)).thenReturn(Stream.empty());

        assertFalse(weatherService.temperatureWithThisDateAtRegionExist(regionName, date));

        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).findTemperatureDataByRegionId(regionId);
    }
}