package momongo12.fintech.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.services.WeatherService;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Momongo12
 * @version 1.0
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final WeatherFactory weatherFactory;
    private final WeatherRepository weatherRepository;

    @Override
    public Stream<Weather> getCurrentTemperatureByRegionName(String regionName) {
        log.info("Getting current temperature data for region: {}", regionName);
        return weatherRepository.findTemperatureDataByRegionId(weatherFactory.getRegionIdByRegionName(regionName));
    }

    @Override
    public Optional<Weather> addNewRegion(String regionName, WeatherDto weatherDto) {
        Weather weather;

        if (weatherDto.getMeasuringDate() != null) {
            weather = weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate());
        } else {
            weather = weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue());
        }

        log.info("Adding new weather data for region: {}", regionName);
        weatherRepository.addWeatherData(weather);

        return Optional.of(weather);
    }

    @Override
    public Optional<Weather> updateTemperatureByRegionName(String regionName, WeatherDto weatherDto) {
        int regionId = weatherFactory.getRegionIdByRegionName(regionName);
        Optional<Weather> weatherOptional = weatherRepository.findWeatherByRegionIdAndMeasuringDate(regionId, weatherDto.getMeasuringDate());

        weatherOptional.ifPresent(weather -> {
            log.info("Updating temperature data for region: {}. New temperature: {}", regionName, weatherDto.getTemperatureValue());
            weather.setTemperatureValue(weatherDto.getTemperatureValue());
        });

        return weatherOptional;
    }

    @Override
    public Optional<Long> deleteRegionData(String regionName) {
        try {
            int regionId = weatherFactory.getRegionIdByRegionName(regionName);

            log.info("Deleting weather data for region: {}", regionName);
            return Optional.of(weatherRepository.deleteWeatherDataByRegionId(regionId));
        } catch (NoSuchElementException exception) {
            log.error(exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean temperatureWithThisDateAtRegionExist(String regionName, Instant date) {
        log.debug("Checking if temperature data with date {} exists for region: {}", date, regionName);
        return weatherRepository
                .findTemperatureDataByRegionId(weatherFactory.getRegionIdByRegionName(regionName))
                .anyMatch(weather -> weather.getMeasuringDate().equals(date));
    }
}
