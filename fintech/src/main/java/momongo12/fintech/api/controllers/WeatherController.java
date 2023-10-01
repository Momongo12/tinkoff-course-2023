package momongo12.fintech.api.controllers;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import momongo12.fintech.api.controllers.exceptions.DuplicateResourceException;
import momongo12.fintech.api.controllers.exceptions.NotFoundException;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.api.mappers.WeatherMapper;
import momongo12.fintech.services.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;



/**
 * @author Momongo12
 * @version 1.0
 */
@RestController
@RequestMapping("/api/weather/{regionName}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WeatherController {

    private WeatherService weatherService;
    private WeatherMapper weatherMapper;

    @GetMapping
    public List<WeatherDto> getCurrentTemperature(@PathVariable("regionName") String regionName) {
        return weatherService
                .getCurrentTemperatureByRegionName(regionName)
                .map(weatherMapper::weatherToWeatherDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<WeatherDto> createNewRegion(@PathVariable("regionName") String regionName,
                                                      @RequestBody WeatherDto weatherDto) {
        try {
            return weatherService
                    .addNewRegion(regionName, weatherDto)
                    .map(weather -> ResponseEntity.status(HttpStatus.CREATED).body(weatherMapper.weatherToWeatherDto(weather)))
                    .get();
        } catch (IllegalArgumentException e) {
            throw new DuplicateResourceException(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<WeatherDto> updateTemperature(@PathVariable("regionName") String regionName, WeatherDto weatherDto) {
        if (weatherService.temperatureWithThisDateAtRegionExist(regionName, LocalDateTime.from(weatherDto.getMeasuringDate()))) {
            return weatherService
                    .updateTemperatureByRegionName(regionName, weatherDto)
                    .map(weather -> ResponseEntity.status(HttpStatus.OK).body(weatherMapper.weatherToWeatherDto(weather)))
                    .orElse(ResponseEntity.status(HttpStatus.valueOf(500)).build());
        }else {
            return createNewRegion(regionName, weatherDto);
        }
    }

    @DeleteMapping
    public ResponseEntity<WeatherDto> deleteRegion(@PathVariable("regionName") String regionName) {
        return weatherService
                .deleteRegionData(regionName)
                .map(weather -> ResponseEntity.status(HttpStatus.OK).body(weatherMapper.weatherToWeatherDto(weather)))
                .orElseThrow(() -> new NotFoundException("No temperature data was found for this city"));
    }
}