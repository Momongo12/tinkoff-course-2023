package momongo12.fintech.api.mappers;


import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Weather;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    WeatherDto weatherToWeatherDto(Weather weather);
}
