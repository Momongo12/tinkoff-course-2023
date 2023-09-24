package momongo12.fintech;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Momongo12
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Weather {

    private final int regionId;
    private final String regionName;
    private final double temperatureValue;
    private final Instant measuringDate;

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
        int regionId = getRegionIdByRegionName(regionName);
        Instant currentDate = Instant.now();
        return new Weather(regionId, regionName, temperatureValue, currentDate);
    }

    private static int getRegionIdByRegionName(String regionName) {
        return regionNameToRegionId.computeIfAbsent(regionName, key -> nextRegionId.getAndIncrement());
    }
}
