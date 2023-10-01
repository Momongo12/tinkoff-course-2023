package momongo12.fintech.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import momongo12.fintech.store.entities.Weather;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeatherFactory {

    private static final Map<String, Integer> regionNameToRegionId = new ConcurrentHashMap<>();
    private static final AtomicInteger nextRegionId = new AtomicInteger(1);

    /**
     * Creates a new Weather object for the specified region and temperature value.
     * @param regionName
     * @param temperatureValue
     * @return Weather
     * @implNote The region identifier (regionId) will be determined based on the regionName and will be unique for each unique regionName.
     * @implNote measuringDate will be initialized with the current time stamp
     * @apiNote This method is thread-safe
     */
    public static Weather createWeather(String regionName, double temperatureValue) {
        return Weather
                .builder()
                .regionId(getRegionIdByRegionName(regionName))
                .regionName(regionName)
                .temperatureValue(temperatureValue)
                .measuringDate(Instant.now())
                .build();
    }

    private static int getRegionIdByRegionName(String regionName) {
        return regionNameToRegionId.computeIfAbsent(regionName, key -> nextRegionId.getAndIncrement());
    }
}
