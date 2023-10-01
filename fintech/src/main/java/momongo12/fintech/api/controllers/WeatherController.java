package momongo12.fintech.api.controllers;


import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
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
@Api(tags = "Weather Controller")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherMapper weatherMapper;

    @GetMapping
    @ApiOperation(value = "Get current temperature data for a specific region", response = WeatherDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved weather data"),
            @ApiResponse(code = 404, message = "Weather data not found for the specified region")
    })
    public List<WeatherDto> getCurrentTemperature(
            @ApiParam(value = "Name of the region", required = true) @PathVariable("regionName") String regionName) {
        return weatherService
                .getCurrentTemperatureByRegionName(regionName)
                .map(weatherMapper::weatherToWeatherDto)
                .toList();
    }

    @PostMapping
    @ApiOperation(value = "Create new weather data for a specific region", response = WeatherDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Weather data created successfully", response = WeatherDto.class),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<WeatherDto> createNewRegion(
            @ApiParam(value = "Name of the region", required = true) @PathVariable("regionName") String regionName,
            @ApiParam(value = "Weather data", required = true) @RequestBody WeatherDto weatherDto) {
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
    @ApiOperation(value = "Update temperature data for a specific region", response = WeatherDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Weather data updated successfully", response = WeatherDto.class),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<WeatherDto> updateTemperature(
            @ApiParam(value = "Name of the region", required = true) @PathVariable("regionName") String regionName,
            @ApiParam(value = "Weather data", required = true) @RequestBody WeatherDto weatherDto) {
        if (weatherService.temperatureWithThisDateAtRegionExist(regionName, weatherDto.getMeasuringDate())) {
            return weatherService
                    .updateTemperatureByRegionName(regionName, weatherDto)
                    .map(weather -> ResponseEntity.status(HttpStatus.OK).body(weatherMapper.weatherToWeatherDto(weather)))
                    .orElse(ResponseEntity.status(HttpStatus.valueOf(500)).build());
        } else {
            return createNewRegion(regionName, weatherDto);
        }
    }

    @DeleteMapping
    @ApiOperation(value = "Delete weather data for a specific region")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Weather data deleted successfully", response = String.class),
            @ApiResponse(code = 404, message = "Weather data not found for the specified region"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<String> deleteRegion(
            @ApiParam(value = "Name of the region", required = true) @PathVariable("regionName") String regionName) {
        return weatherService
                .deleteRegionData(regionName)
                .map(num -> ResponseEntity.status(HttpStatus.OK).body("Removed %d weather objects for %s".formatted(num, regionName)))
                .orElseThrow(() -> new NotFoundException("No temperature data was found for this city"));
    }
}