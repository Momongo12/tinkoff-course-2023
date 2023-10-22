package momongo12.fintech.utils;

import lombok.NoArgsConstructor;

import momongo12.fintech.store.entities.Region;
import momongo12.fintech.store.entities.Weather;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Momongo12
 * @version 1.3
 */
@Component
@NoArgsConstructor
public class WeatherFactory {

    private final Map<String, Integer> regionNameToRegionId = new ConcurrentHashMap<>();
    private final AtomicInteger nextRegionId = new AtomicInteger(1);

    /**
     * Creates a new Weather object for the specified region and temperature value.
     *
     * @param regionName
     * @param temperatureValue
     * @param measuringDate
     * @return Weather
     * @implNote The region identifier (regionId) will be determined based on the regionName and will be unique for each unique regionName.
     * @implNote measuringDate will be initialized with the current time stamp
     * @apiNote This method is thread-safe
     */
    public Weather createWeather(String regionName, double temperatureValue, Instant measuringDate) {
        Region region = Region.builder().name(regionName).id(getRegionIdByRegionName(regionName)).build();

        return Weather
                .builder()
                .region(region)
                .temperatureValue(temperatureValue)
                .measuringDate(measuringDate)
                .build();
    }

    /**
     * Creates a new Weather object for the specified region and temperature value.
     *
     * @param regionName
     * @param temperatureValue
     * @return Weather
     * @implNote The region identifier (regionId) will be determined based on the regionName and will be unique for each unique regionName.
     * @implNote measuringDate will be initialized with the current time stamp
     * @apiNote This method is thread-safe
     */
    public Weather createWeather(String regionName, double temperatureValue) {
        return createWeather(regionName, temperatureValue, Instant.now());
    }

    public int getRegionIdByRegionName(String regionName) {
        return regionNameToRegionId.computeIfAbsent(regionName, key -> nextRegionId.getAndIncrement());
    }
}
