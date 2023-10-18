package momongo12.fintech.services.impl;

import lombok.extern.log4j.Log4j2;

import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.services.WeatherService;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.RegionRepository;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.utils.WeatherFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Momongo12
 * @version 1.2
 */
@Service
@Log4j2
public class WeatherServiceImpl implements WeatherService {
    private final WeatherFactory weatherFactory;
    private final WeatherRepository weatherRepository;
    private final RegionRepository regionRepository;

    public WeatherServiceImpl(WeatherFactory weatherFactory,
                                  @Qualifier("weatherRepositoryForWeatherServiceImpl") WeatherRepository weatherRepository,
                                      RegionRepository regionRepository) {
        this.weatherFactory = weatherFactory;
        this.weatherRepository = weatherRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    public Stream<Weather> getCurrentTemperatureByRegionName(String regionName) {
        log.info("Getting current temperature data for region: {}", regionName);
        return weatherRepository.findTemperatureDataByRegionId(weatherFactory.getRegionIdByRegionName(regionName)).stream();
    }

    @Transactional
    @Override
    public Optional<Weather> addNewRegion(String regionName, WeatherDto weatherDto) {
        Weather weather;

        if (weatherDto.getMeasuringDate() != null) {
            weather = weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate());
        } else {
            weather = weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue());
        }

        log.info("Adding new weather data for region: {}", regionName);
        regionRepository.saveAndFlush(weather.getRegion());

        return Optional.of(weatherRepository.addWeatherData(weather));
    }

    @Transactional
    @Override
    public Optional<Weather> updateTemperatureByRegionName(String regionName, WeatherDto weatherDto) {
        int regionId = weatherFactory.getRegionIdByRegionName(regionName);
        Optional<Weather> weatherOptional = weatherRepository.findWeatherByRegionIdAndMeasuringDate(regionId, weatherDto.getMeasuringDate());

        weatherOptional.ifPresent(weather -> {
            log.info("Updating temperature data for region: {}. New temperature: {}", regionName, weatherDto.getTemperatureValue());

            weather.setTemperatureValue(weatherDto.getTemperatureValue());
            weatherRepository.updateTemperatureById(weather.getId(), weatherDto.getTemperatureValue());
        });

        return weatherOptional;
    }

    @Transactional
    @Override
    public Optional<Integer> deleteRegionData(String regionName) {
        try {
            int regionId = weatherFactory.getRegionIdByRegionName(regionName);

            log.info("Deleting weather data for region: {}", regionName);

            int countDeletedObjects = weatherRepository.deleteWeatherDataByRegionId(regionId);

            return (countDeletedObjects == 0)? Optional.empty() : Optional.of(countDeletedObjects);
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
                .stream()
                .anyMatch(weather -> weather.getMeasuringDate().equals(date));
    }
}
