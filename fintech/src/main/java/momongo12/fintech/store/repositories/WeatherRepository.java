package momongo12.fintech.store.repositories;

import momongo12.fintech.store.entities.Weather;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Momongo12
 * @version 1.2
 */
public interface WeatherRepository {

    /**
     * Retrieves a list of Weather objects representing temperature data for a specific region.
     *
     * @param regionId The unique identifier of the region.
     * @return A list of Weather objects representing temperature data for the specified region.
     */
    List<Weather> findTemperatureDataByRegionId(int regionId);

    /**
     * Retrieves weather data for a specific region based on the region ID, measuring date, and region name.
     *
     * @param regionId      The unique identifier of the region.
     * @param measuringDate The timestamp indicating when the weather data was measured.
     * @return An Optional containing the Weather object if found, or an empty Optional if not found.
     */
    Optional<Weather> findWeatherByRegionIdAndMeasuringDate(int regionId, Instant measuringDate);

    /**
     * Adds weather data to the data store.
     *
     * @param weather The Weather object representing the weather data to be added.
     */
    Weather addWeatherData(Weather weather);

    /**
     * Updates the temperature value for a weather record based on the specified weather ID.
     *
     * @param weatherId      The unique identifier of the weather record to update.
     * @param newTemperature The new temperature value to update.
     */
    void updateTemperatureById(int weatherId, double newTemperature);

    /**
     * Deletes weather data for a specific region based on the region ID.
     *
     * @param regionId The unique identifier of the region for which weather data will be deleted.
     * @return number deleted weather objects
     * @throws NoSuchElementException If no weather data is found for the specified region ID.
     */
    int deleteWeatherDataByRegionId(int regionId) throws NoSuchElementException;
}
