package momongo12.fintech.services.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.RegionRepository;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
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

    @Spy
    RegionRepository regionRepository;

    @InjectMocks
    WeatherServiceImpl weatherService;

    @Test
    void testGetCurrentTemperatureByRegionName() {
        int regionId = 1;
        String regionName = "regionName";
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.findTemperatureDataByRegionId(regionId)).thenReturn(List.of());

        Stream<Weather> result = weatherService.getCurrentTemperatureByRegionName(regionName);

        assertEquals(0, result.count());
        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).findTemperatureDataByRegionId(regionId);
    }

    @Test
    void testAddNewRegionWithMeasuringDate() {
        int regionId = 1;
        int weatherId = 1;
        String regionName = "regionName";
        WeatherDto weatherDto = new WeatherDto(regionId, regionName, 10.0, Instant.now());
        Region region = Region.builder().id(regionId).name(regionName).build();
        Weather weather = new Weather(weatherId, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate(), null, region);
        when(weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate())).thenReturn(weather);
        when(weatherRepository.addWeatherData(weather)).thenReturn(weather);

        Optional<Weather> result = weatherService.addNewRegion(regionName, weatherDto);

        assertEquals(weather, result.orElse(null));
        verify(weatherFactory, times(1)).createWeather(regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate());
        verify(weatherRepository, times(1)).addWeatherData(weather);
        verify(regionRepository, times(1)).saveAndFlush(weather.getRegion());
    }

    @Test
    void testAddNewRegionWithoutMeasuringDate() {
        int regionId = 1;
        int weatherId = 1;
        String regionName = "regionName";
        WeatherDto weatherDto = new WeatherDto(regionId, regionName, 10.0, null);
        Region region = Region.builder().id(regionId).name(regionName).build();
        Weather weather = new Weather(weatherId, weatherDto.getTemperatureValue(), null, null, region);
        when(weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue())).thenReturn(weather);
        when(weatherRepository.addWeatherData(weather)).thenReturn(weather);

        Optional<Weather> result = weatherService.addNewRegion(regionName, weatherDto);

        assertEquals(weather, result.orElse(null));
        verify(weatherFactory, times(1)).createWeather(regionName, weatherDto.getTemperatureValue());
        verify(weatherRepository, times(1)).addWeatherData(weather);
        verify(regionRepository, times(1)).saveAndFlush(weather.getRegion());
    }

    @Test
    void testUpdateTemperatureByRegionName() {
        int regionId = 1;
        int weatherId = 1;
        String regionName = "regionName";
        WeatherDto weatherDto = new WeatherDto(regionId, regionName, 10.0, Instant.now());
        Region region = Region.builder().id(regionId).name(regionName).build();
        Weather weather = new Weather(weatherId, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate(), null, region);
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
        when(weatherRepository.deleteWeatherDataByRegionId(regionId)).thenReturn(1);

        Optional<Integer> result = weatherService.deleteRegionData(regionName);

        assertEquals(1, result.orElseThrow());
        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).deleteWeatherDataByRegionId(regionId);
    }

    @Test
    void testDeleteRegionDataForNotExistRegion() {
        int regionId = 1;
        String regionName = "regionName";
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.deleteWeatherDataByRegionId(regionId)).thenThrow(new NoSuchElementException());

        Optional<Integer> result = weatherService.deleteRegionData(regionName);

        assertTrue(result.isEmpty());
        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).deleteWeatherDataByRegionId(regionId);
    }

    @Test
    void testTemperatureWithExistingDateAtRegionShouldReturnTrue() {
        int regionId = 1;
        String regionName = "regionName";
        Instant date = Instant.now();
        Region region = Region.builder().id(regionId).name(regionName).build();
        Weather someWeather = Weather.builder().region(region).measuringDate(date).build();
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.findTemperatureDataByRegionId(regionId)).thenReturn(List.of(someWeather));

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
        when(weatherRepository.findTemperatureDataByRegionId(regionId)).thenReturn(List.of());

        assertFalse(weatherService.temperatureWithThisDateAtRegionExist(regionName, date));

        verify(weatherFactory, times(1)).getRegionIdByRegionName(regionName);
        verify(weatherRepository, times(1)).findTemperatureDataByRegionId(regionId);
    }
}