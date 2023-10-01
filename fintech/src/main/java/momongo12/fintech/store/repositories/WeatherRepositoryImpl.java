package momongo12.fintech.store.repositories;

import momongo12.fintech.store.entities.Weather;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * The WeatherRepositoryImpl class represents an implementation of the {@link WeatherRepository} interface.
 * It provides methods to manage and store weather data for different regions.
 *
 * <p>This implementation uses an in-memory ConcurrentHashMap to store regional temperature data.
 * Weather data is stored as lists, where each region ID is mapped to a list of Weather objects.
 *
 * @author Momongo12
 * @version 1.0
 */
@Component
public class WeatherRepositoryImpl implements WeatherRepository {

    private static final Map<Integer, List<Weather>> mapOfRegionalTemperatureData = new ConcurrentHashMap<>();

    @Override
    public Stream<Weather> findTemperatureDataByRegionId(int regionId) {
        return Stream.empty();
    }

    @Override
    public Optional<Weather> findWeatherByRegionIdAndMeasuringDate(int regionId, Instant measuringDate) {
        return Optional.empty();
    }

    @Override
    public void addWeatherData(Weather weather) {

    }

    @Override
    public void deleteWeatherDataByRegionId(int regionId) throws NoSuchElementException {

    }
}
