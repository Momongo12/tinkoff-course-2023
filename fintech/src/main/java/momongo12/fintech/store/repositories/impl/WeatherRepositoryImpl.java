package momongo12.fintech.store.repositories.impl;

import momongo12.fintech.store.entities.Weather;
import momongo12.fintech.store.repositories.WeatherRepository;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The WeatherRepositoryImpl class represents an implementation of the {@link WeatherRepository} interface.
 * It provides methods to manage and store weather data for different regions.
 *
 * <p>This implementation uses an in-memory ConcurrentHashMap to store regional temperature data.
 * Weather data is stored as lists, where each region ID is mapped to a list of Weather objects.
 *
 * @author Momongo12
 * @version 1.3
 */
@Repository(value = "WeatherHeapRepository")
public class WeatherRepositoryImpl implements WeatherRepository {

    private static final Map<Integer, List<Weather>> mapOfRegionalTemperatureData = new ConcurrentHashMap<>();

    @Override
    public List<Weather> findTemperatureDataByRegionId(int regionId) {
        return mapOfRegionalTemperatureData
                .getOrDefault(regionId, new ArrayList<>());
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
    public Weather addWeatherData(Weather weather) {
        mapOfRegionalTemperatureData
                .computeIfAbsent(weather.getRegion().getId(), k -> new CopyOnWriteArrayList<>())
                .add(weather);

        return weather;
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
    public int deleteWeatherDataByRegionId(int regionId) throws NoSuchElementException {
        if (!mapOfRegionalTemperatureData.containsKey(regionId)) {
            throw new NoSuchElementException("Weather data for region with regionId=%d not found".formatted(regionId));
        }

        int numberWeatherObjectsAtRegion = findTemperatureDataByRegionId(regionId).size();

        mapOfRegionalTemperatureData.remove(regionId);

        return numberWeatherObjectsAtRegion;
    }

    @Override
    public long countByRegionId(int regionId) {
        throw new UnsupportedOperationException("This operation is not supported for this implementation");
    }

    @Override
    public Double calculateMovingAverage(int regionId, long numberPeriods) {
        throw new UnsupportedOperationException("This operation is not supported for this implementation");
    }
}
