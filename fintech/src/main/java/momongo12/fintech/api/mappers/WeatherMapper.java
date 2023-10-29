package momongo12.fintech.api.mappers;

import momongo12.fintech.api.dto.WeatherApiResponse;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Weather;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Momongo12
 * @version 1.2
 */
@Mapper(componentModel = "spring")
public interface WeatherMapper {

    @Mapping(source = "region.id", target = "regionId")
    @Mapping(source = "region.name", target = "regionName")
    WeatherDto weatherToWeatherDto(Weather weather);

    @Mapping(source = "location.regionName", target = "region.name")
    @Mapping(source = "location.localtime", target = "measuringDate")
    @Mapping(source = "weatherData.temperature", target = "temperatureValue")
    @Mapping(source = "weatherData.weatherType.text", target = "weatherType.description")
    @Mapping(source = "weatherData.weatherType.representation", target = "weatherType.representation")
    @Mapping(source = "weatherData.weatherType.code", target = "weatherType.id")
    Weather weatherApiResponseToWeather(WeatherApiResponse weatherApiResponse);
}
