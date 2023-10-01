package momongo12.fintech.api.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import momongo12.fintech.api.controllers.exceptions.DuplicateResourceException;
import momongo12.fintech.api.controllers.exceptions.NotFoundException;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.api.mappers.WeatherMapper;
import momongo12.fintech.services.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



/**
 * @author Momongo12
 * @version 1.0
 */
@RestController
@RequestMapping("/api/weather/{regionName}")
@Tag(name = "Weather Controller", description = "Endpoints for managing weather data")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherMapper weatherMapper;

    @GetMapping
    @Operation(summary = "Get current temperature data for a specific region", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved weather data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDto.class))),
            @ApiResponse(responseCode = "404", description = "Weather data not found for the specified region")
    })
    public List<WeatherDto> getCurrentTemperature(@PathVariable("regionName") String regionName) {
        return weatherService
                .getCurrentTemperatureByRegionName(regionName)
                .map(weatherMapper::weatherToWeatherDto)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Create new weather data for a specific region", responses = {
            @ApiResponse(responseCode = "201", description = "Weather data created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<WeatherDto> createNewRegion(@PathVariable("regionName") String regionName, @RequestBody WeatherDto weatherDto) {
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
    @Operation(summary = "Update temperature data for a specific region", responses = {
            @ApiResponse(responseCode = "200", description = "Weather data updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<WeatherDto> updateTemperature(String regionName, @RequestBody WeatherDto weatherDto) {
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
    @Operation(summary = "Delete weather data for a specific region", responses = {
            @ApiResponse(responseCode = "200", description = "Weather data deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Weather data not found for the specified region"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteRegion(@PathVariable("regionName") String regionName) {
        return weatherService
                .deleteRegionData(regionName)
                .map(num -> ResponseEntity.status(HttpStatus.OK).body("Removed %d weather objects for %s".formatted(num, regionName)))
                .orElseThrow(() -> new NotFoundException("No temperature data was found for this city"));
    }
}