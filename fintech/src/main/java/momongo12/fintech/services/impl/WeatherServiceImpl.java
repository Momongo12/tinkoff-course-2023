package momongo12.fintech.services.impl;

import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.services.WeatherService;
import momongo12.fintech.store.entities.Weather;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * @author Momongo12
 * @version 1.0
 */
@Service
public class WeatherServiceImpl implements WeatherService {
    @Override
    public Stream<Weather> getCurrentTemperatureByRegionName(String regionName) {
        return Stream.of();
    }

    @Override
    public Optional<Weather> addNewRegion(String regionName, WeatherDto weatherDto) {
        return Optional.empty();
    }

    @Override
    public Optional<Weather> updateTemperatureByRegionName(String regionName, WeatherDto weatherDto) {
        return Optional.empty();
    }

    @Override
    public Optional<Weather> deleteRegionData(String regionName) {
        return Optional.empty();
    }

    @Override
    public boolean temperatureWithThisDateAtRegionExist(String regionName, LocalDateTime date) {
        return true;
    }
}
