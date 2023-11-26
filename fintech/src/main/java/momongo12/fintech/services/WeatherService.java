package momongo12.fintech.services;

import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Weather;

import java.time.Instant;
import java.util.Optional;

/**
 * @author Momongo12
 * @version 1.1
 */
public interface WeatherService {

    /**
     * Retrieves the current temperature data for a specific region by its name.
     *
     * @param regionName The name of the region to retrieve temperature data for.
     * @return An Optional containing the weather object, or empty if the operation fails.
     */
    Optional<Weather> getCurrentTemperatureByRegionName(String regionName);

    /**
     * Adds new weather data for a specific region.
     *
     * @param regionName The name of the region to add weather data to.
     * @param weatherDto The WeatherDto object containing the weather data to be added.
     * @return An Optional containing the added Weather object
     */
    Optional<Weather> addNewRegion(String regionName, WeatherDto weatherDto);

    /**
     * Updates the temperature data for a specific region by its name and temperature measuring Date.
     *
     * @param regionName The name of the region to update temperature data for.
     * @param weatherDto The WeatherDto object containing the updated weather data.
     * @return An Optional containing the updated Weather object, or empty if the operation fails.
     */
    Optional<Weather> updateTemperatureByRegionName(String regionName, WeatherDto weatherDto);

    /**
     * Deletes all weather data for a specific region by its name.
     *
     * @param regionName The name of the region to delete weather data for.
     * @return An Optional containing the deleted Weather object count, or empty if the operation fails or
     * weather data for a specific region not found.
     */
    Optional<Integer> deleteRegionData(String regionName);

    /**
     * Checks if temperature data for a specific date and region exists.
     *
     * @param regionName The name of the region to check temperature data for.
     * @param date       The LocalDateTime object representing the date to check for temperature data.
     * @return True if temperature data for the specified date and region exists, false otherwise.
     */
    boolean temperatureWithThisDateAtRegionExist(String regionName, Instant date);
}
