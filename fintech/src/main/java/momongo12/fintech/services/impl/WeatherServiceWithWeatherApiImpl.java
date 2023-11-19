package momongo12.fintech.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import momongo12.fintech.api.dto.WeatherApiResponse;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.api.mappers.WeatherMapper;
import momongo12.fintech.services.WeatherService;
import momongo12.fintech.services.remote.WeatherApiClient;
import momongo12.fintech.store.WeatherLRUCache;
import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.RegionRepository;
import momongo12.fintech.store.repositories.WeatherRepository;
import momongo12.fintech.store.repositories.WeatherTypeRepository;
import momongo12.fintech.utils.WeatherFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * This class implements {@link WeatherService} functions similar to {@link WeatherServiceImpl},
 * but also maintains the current state of weather data in the region using {@link WeatherApiClient} (see getCurrentTemperatureByRegionName function)
 * and updates/saves weather data using {@link WeatherApiClient} if some required data is not passed in.
 *
 * @author Momongo12
 * @version 1.3
 */
@Service
@Log4j2
@RequiredArgsConstructor
@Primary
public class WeatherServiceWithWeatherApiImpl implements WeatherService {

    @Autowired
    @Qualifier("weatherRepositoryForWeatherServiceImpl")
    private WeatherRepository weatherRepository;

    @Value("${cache.course.expiry-time-in-seconds:900}")
    private long expireTimeInSeconds;

    private final WeatherFactory weatherFactory;
    private final RegionRepository regionRepository;
    private final WeatherMapper weatherMapper;
    private final WeatherApiClient weatherApiClient;
    private final WeatherTypeRepository weatherTypeRepository;
    private final TransactionTemplate transactionTemplate;
    private final WeatherLRUCache weatherLRUCache;

    @Override
    public Optional<Weather> getCurrentTemperatureByRegionName(String regionName) {
        log.info("Getting current temperature data for region: {}", regionName);

        Optional<Weather> weather = weatherLRUCache.get(regionName);

        if (weather.isPresent()) {
            return weather;
        }

        weather = getActualWeatherFromDB(regionName);

        if (weather.isEmpty()) {
            weather = updateWeatherDataByRegionName(regionName);
        }

        weather.ifPresent(value -> weatherLRUCache.put(regionName, value));

        return weather;
    }

    @Transactional
    @Override
    public Optional<Weather> addNewRegion(String regionName, WeatherDto weatherDto) {
        log.info("Adding new weather data for region: {}", regionName);
        Weather weather;

        if (weatherDto.getMeasuringDate() != null && weatherDto.getTemperatureValue() != null) {
            weather = weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue(), weatherDto.getMeasuringDate());
        } else if (weatherDto.getTemperatureValue() != null) {
            weather = weatherFactory.createWeather(regionName, weatherDto.getTemperatureValue());
        } else {
            return updateWeatherDataByRegionName(regionName);
        }

        regionRepository.saveAndFlush(weather.getRegion());

        return Optional.of(weatherRepository.addWeatherData(weather));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Optional<Weather> updateTemperatureByRegionName(String regionName, WeatherDto weatherDto) {
        if (weatherDto.getTemperatureValue() == null) {
            Optional<Weather> weatherOptional = updateWeatherDataByRegionName(regionName);

            weatherOptional.ifPresent(weather -> weatherLRUCache.put(regionName, weather));
            return weatherOptional;
        }

        int regionId = weatherFactory.getRegionIdByRegionName(regionName);
        Optional<Weather> weatherOptional = weatherRepository.findWeatherByRegionIdAndMeasuringDate(regionId, weatherDto.getMeasuringDate());

        weatherOptional.ifPresent(weather -> {
            log.info("Updating temperature data for region: {}. New temperature: {}", regionName, weatherDto.getTemperatureValue());

            weather.setTemperatureValue(weatherDto.getTemperatureValue());
            weatherRepository.updateTemperatureById(weather.getId(), weatherDto.getTemperatureValue());
            weatherLRUCache.put(regionName, weather);
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

    @Transactional(readOnly = true)
    @Override
    public boolean temperatureWithThisDateAtRegionExist(String regionName, Instant date) {
        log.debug("Checking if temperature data with date {} exists for region: {}", date, regionName);
        return weatherRepository
                .findTemperatureDataByRegionId(weatherFactory.getRegionIdByRegionName(regionName))
                .stream()
                .anyMatch(weather -> weather.getMeasuringDate().equals(date));
    }

    /**
     * This function uses {@link WeatherApiClient} to update the data
     * <p>
     * If there is a record with such regionId and MeasuringDate in the database,
     * then update the temperature, otherwise add new weather data to the database
     *
     * @param regionName
     * @return An Optional containing the Weather object if weather data found, otherwise an empty Optional.
     */
    private Optional<Weather> updateWeatherDataByRegionName(String regionName) {
        Optional<WeatherApiResponse> responseOptional = weatherApiClient.getCurrentWeather(regionName);

        if (responseOptional.isPresent()) {
            Weather weatherForSaveDb = weatherMapper.weatherApiResponseToWeather(responseOptional.get());
            weatherForSaveDb.getRegion().setId(weatherFactory.getRegionIdByRegionName(regionName));

            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);

            transactionTemplate.executeWithoutResult((transactionStatus -> {
                weatherRepository
                        .findWeatherByRegionIdAndMeasuringDate(
                                weatherFactory.getRegionIdByRegionName(regionName),
                                weatherForSaveDb.getMeasuringDate()
                        )
                        .ifPresentOrElse(
                                weather -> weatherRepository.updateTemperatureById(weather.getId(), weatherForSaveDb.getTemperatureValue()),
                                () -> {
                                    weatherTypeRepository.saveAndFlush(weatherForSaveDb.getWeatherType());
                                    regionRepository.saveAndFlush(weatherForSaveDb.getRegion());
                                    weatherRepository.addWeatherData(weatherForSaveDb);
                                });
            }));

            return Optional.of(weatherForSaveDb);
        }

        return Optional.empty();
    }

    Optional<Weather> getActualWeatherFromDB(String regionName) {
        return weatherRepository
                .findTemperatureDataByRegionId(weatherFactory.getRegionIdByRegionName(regionName))
                .stream()
                .filter(this::weatherIsActual)
                .findFirst();
    }

    boolean weatherIsActual(Weather weather) {
        Instant currentTime = Instant.now();
        Instant weatherMeasuringDate = weather.getMeasuringDate();

        return currentTime.compareTo(weatherMeasuringDate.plusSeconds(expireTimeInSeconds)) >= 0;
    }
}
