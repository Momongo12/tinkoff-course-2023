package momongo12.fintech.api.mappers;

import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Weather;

import org.mapstruct.Mapper;

/**
 * @author Momongo12
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface WeatherMapper {

    WeatherDto weatherToWeatherDto(Weather weather);
}
