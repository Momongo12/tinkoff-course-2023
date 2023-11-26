package momongo12.fintech.services.impl;

import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.WeatherLRUCache;
import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author momongo12
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
public class WeatherServiceWithWeatherApiImplTest {

    @Mock
    WeatherRepository weatherRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    WeatherFactory weatherFactory;

    @Mock
    WeatherLRUCache weatherLRUCache;

    @InjectMocks
    WeatherServiceWithWeatherApiImpl weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "weatherRepository", weatherRepository);
    }

    @Test
    void testGetCurrentTemperatureByRegionNameWhenDataExistInCache() {
        String regionName = "regionName";
        int regionId = 1;
        Weather weather = Weather.builder().region(Region.builder().name(regionName).build()).build();
        when(weatherLRUCache.get(regionName)).thenReturn(Optional.of(weather));
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);

        Optional<Weather> weatherOptional = weatherService.getCurrentTemperatureByRegionName(regionName);

        assertTrue(weatherOptional.isPresent());
        assertEquals(regionName, weatherOptional.get().getRegion().getName());
        verify(weatherRepository, times(0)).findTemperatureDataByRegionId(regionId);
    }

    @Test
    void testGetCurrentTemperatureByRegionNameWhenDataNotExistInCache() {
        String regionName = "regionName";
        int regionId = 1;
        Weather weather = Weather.builder()
                .region(Region.builder().name(regionName).build())
                .measuringDate(Instant.now())
                .build();
        when(weatherLRUCache.get(regionName)).thenReturn(Optional.empty());
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        doReturn(List.of(weather)).when(weatherRepository).findTemperatureDataByRegionId(regionId);

        Optional<Weather> weatherOptional = weatherService.getCurrentTemperatureByRegionName(regionName);

        assertTrue(weatherOptional.isPresent());
        assertEquals(regionName, weatherOptional.get().getRegion().getName());
        verify(weatherLRUCache, times(1)).put(regionName, weatherOptional.get());
    }

    @Test
    void testUpdateTemperatureByRegionNameShouldUpdateCache() {
        String regionName = "regionName";
        int regionId = 1;
        WeatherDto weatherDto = WeatherDto.builder().measuringDate(Instant.now()).temperatureValue(10.0).build();
        Weather weather = Weather.builder().measuringDate(Instant.now()).temperatureValue(10.0).build();
        when(weatherFactory.getRegionIdByRegionName(regionName)).thenReturn(regionId);
        when(weatherRepository.findWeatherByRegionIdAndMeasuringDate(regionId, weatherDto.getMeasuringDate()))
                .thenReturn(Optional.of(weather));

        Optional<Weather> weatherOptional = weatherService.updateTemperatureByRegionName(regionName, weatherDto);

        assertTrue(weatherOptional.isPresent());
        assertEquals(weatherDto.getTemperatureValue(), weatherOptional.get().getTemperatureValue());
        verify(weatherLRUCache, times(1)).put(regionName, weather);
    }

}
