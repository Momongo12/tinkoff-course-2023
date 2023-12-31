package momongo12.fintech.store.repositories;

import momongo12.fintech.store.entities.Weather;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Momongo12
 * @version 1.2
 */
@Repository(value = "WeatherJpaRepository")
public interface WeatherJpaRepository extends JpaRepository<Weather, Integer>, WeatherRepository {

    /**
     * Retrieves a list of Weather objects representing temperature data for a specific region.
     *
     * @param regionId The unique identifier of the region.
     * @return A stream of Weather objects representing temperature data for the specified region.
     */
    @Query(value = "SELECT * FROM weather WHERE region_id = :regionId", nativeQuery = true)
    List<Weather> findTemperatureDataByRegionId(int regionId);

    /**
     * Retrieves weather data for a specific region based on the region ID, measuring date, and region name.
     *
     * @param regionId      The unique identifier of the region.
     * @param measuringDate The timestamp indicating when the weather data was measured.
     * @return An Optional containing the Weather object if found, or an empty Optional if not found.
     */
    @Query(value = "SELECT * FROM weather WHERE region_id = :regionId AND measuring_date = :measuringDate", nativeQuery = true)
    Optional<Weather> findWeatherByRegionIdAndMeasuringDate(int regionId, Instant measuringDate);

    /**
     * Adds weather data to the data store.
     *
     * @param weather The Weather object representing the weather data to be added.
     */
    default Weather addWeatherData(Weather weather) {
        return save(weather);
    }

    /**
     * Updates the temperature value for a weather record based on the specified weather ID.
     *
     * @param weatherId      The unique identifier of the weather record to update.
     * @param newTemperature The new temperature value to update.
     */
    @Modifying
    @Query(value = "UPDATE weather SET temperature = :newTemperature WHERE id = :weatherId", nativeQuery = true)
    void updateTemperatureById(int weatherId, double newTemperature);

    /**
     * Deletes weather data for a specific region based on the region ID.
     *
     * @param regionId The unique identifier of the region for which weather data will be deleted.
     * @return number deleted weather objects
     */
    @Modifying
    @Query(value = "DELETE FROM weather WHERE region_id = :regionId", nativeQuery = true)
    int deleteWeatherDataByRegionId(int regionId);

    @Query(value = "SELECT COUNT(*) FROM weather WHERE region_id = :regionId", nativeQuery = true)
    long countByRegionId(int regionId);

    @Query(value = """
                    SELECT avg(temperature)
                    FROM (
                      SELECT temperature
                      FROM weather
                      WHERE region_id = :regionId
                      ORDER BY measuring_date DESC
                      LIMIT :numberPeriods
                    ) AS temp_table
                   """, nativeQuery = true)
    Double calculateMovingAverage(int regionId, long numberPeriods);
}
