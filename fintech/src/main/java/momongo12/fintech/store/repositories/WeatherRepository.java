package momongo12.fintech.store.repositories;


import momongo12.fintech.store.entities.Weather;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Momongo12
 * @version 1.0
 */
public interface WeatherRepository {

    /**
     * Retrieves a stream of Weather objects representing temperature data for a specific region.
     *
     * @param regionId The unique identifier of the region.
     * @return A stream of Weather objects representing temperature data for the specified region.
     */
    Stream<Weather> findTemperatureDataByRegionId(int regionId);

    /**
     * Retrieves weather data for a specific region based on the region ID, measuring date, and region name.
     *
     * @param regionId      The unique identifier of the region.
     * @param measuringDate The timestamp indicating when the weather data was measured.
     * @return An Optional containing the Weather object if found, or an empty Optional if not found.
     */
    Optional<Weather> findWeatherByRegionIdAndMeasuringDate (int regionId, Instant measuringDate);

    /**
     * Adds weather data to the data store.
     *
     * @param weather The Weather object representing the weather data to be added.
     */
    void addWeatherData(Weather weather);

    /**
     * Deletes weather data for a specific region based on the region ID.
     *
     * @param regionId The unique identifier of the region for which weather data will be deleted.
     * @throws NoSuchElementException If no weather data is found for the specified region ID.
     * @return number deleted weather objects
     */
    long deleteWeatherDataByRegionId(int regionId) throws NoSuchElementException;
}
