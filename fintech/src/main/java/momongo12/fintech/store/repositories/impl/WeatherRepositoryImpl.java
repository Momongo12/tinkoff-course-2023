package momongo12.fintech.store.repositories.impl;

import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.WeatherRepository;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * The WeatherRepositoryImpl class represents an implementation of the {@link WeatherRepository} interface.
 * It provides methods to manage and store weather data for different regions.
 *
 * <p>This implementation uses an in-memory ConcurrentHashMap to store regional temperature data.
 * Weather data is stored as lists, where each region ID is mapped to a list of Weather objects.
 *
 * @author Momongo12
 * @version 1.1
 */
@Component
public class WeatherRepositoryImpl implements WeatherRepository {

    private static final Map<Integer, List<Weather>> mapOfRegionalTemperatureData = new ConcurrentHashMap<>();

    @Override
    public Stream<Weather> findTemperatureDataByRegionId(int regionId) {
        return mapOfRegionalTemperatureData
                .getOrDefault(regionId, new ArrayList<>())
                .stream();
    }

    @Override
    public Optional<Weather> findWeatherByRegionIdAndMeasuringDate(int regionId, Instant measuringDate) {
        return mapOfRegionalTemperatureData
                .getOrDefault(regionId, new ArrayList<>())
                .stream()
                .filter(date -> date.getMeasuringDate().equals(measuringDate))
                .findFirst();
    }

    @Override
    public void save(Weather weather) {
        mapOfRegionalTemperatureData
                .computeIfAbsent(weather.getRegion().getId(), k -> new CopyOnWriteArrayList<>())
                .add(weather);
    }

    @Override
    public void updateTemperatureById(int weatherId, double newTemperature) {
        mapOfRegionalTemperatureData.values().forEach(weatherList -> {
            weatherList.forEach(weather -> {
                if (weather.getId() == weatherId) {
                    weather.setTemperatureValue(newTemperature);
                }
            });
        });
    }

    @Override
    public long deleteWeatherDataByRegionId(int regionId) throws NoSuchElementException {
        if (!mapOfRegionalTemperatureData.containsKey(regionId)) {
            throw new NoSuchElementException("Weather data for region with regionId=%d not found".formatted(regionId));
        }

        long numberWeatherObjectsAtRegion = findTemperatureDataByRegionId(regionId).count();

        mapOfRegionalTemperatureData.remove(regionId);

        return numberWeatherObjectsAtRegion;
    }
}
