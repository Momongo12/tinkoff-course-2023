package momongo12.fintech.services;


import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Weather;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Momongo12
 * @version 1.0
 */
public interface WeatherService {

    Stream<Weather> getCurrentTemperatureByRegionName(String regionName);

    Optional<Weather> addNewRegion(String regionName, WeatherDto weatherDto);

    Optional<Weather> updateTemperatureByRegionName(String regionName, WeatherDto weatherDto);

    Optional<Weather> deleteRegionData(String regionName);

    boolean temperatureWithThisDateAtRegionExist(String regionName, LocalDateTime date);
}
