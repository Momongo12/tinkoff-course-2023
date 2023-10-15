package momongo12.fintech.api.mappers;

import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.store.entities.Weather;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Momongo12
 * @version 1.1
 */
@Mapper(componentModel = "spring")
public interface WeatherMapper {

    @Mapping(source = "region.id", target = "regionId")
    @Mapping(source = "region.name", target = "regionName")
    WeatherDto weatherToWeatherDto(Weather weather);
}
