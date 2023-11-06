package momongo12.fintech.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import momongo12.fintech.api.controllers.exceptions.DuplicateResourceException;
import momongo12.fintech.api.controllers.exceptions.InternalServerErrorException;
import momongo12.fintech.api.controllers.exceptions.NotFoundException;
import momongo12.fintech.api.dto.ErrorResponse;
import momongo12.fintech.api.dto.WeatherDto;
import momongo12.fintech.api.mappers.WeatherMapper;
import momongo12.fintech.services.WeatherService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Momongo12
 * @version 1.1
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
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Weather data not found for the specified region",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<WeatherDto> getCurrentTemperature(@PathVariable("regionName") String regionName) {
        List<WeatherDto> weatherDtoList =  weatherService
                .getCurrentTemperatureByRegionName(regionName)
                .map(weatherMapper::weatherToWeatherDto)
                .toList();

        if (!weatherDtoList.isEmpty()) {
            return weatherDtoList;
        }else {
            throw new NotFoundException("Weather data not found for the specified region");
        }
    }

    @PostMapping
    @Operation(summary = "Create new weather data for a specific region", responses = {
            @ApiResponse(responseCode = "201", description = "Weather data created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDto.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate resource",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WeatherDto> createNewRegion(@PathVariable("regionName") String regionName, @RequestBody WeatherDto weatherDto) {
        if (!weatherService.temperatureWithThisDateAtRegionExist(regionName, weatherDto.getMeasuringDate())) {
            return weatherService
                    .addNewRegion(regionName, weatherDto)
                    .map(weather -> ResponseEntity.status(HttpStatus.CREATED).body(weatherMapper.weatherToWeatherDto(weather)))
                    .orElseThrow(() -> new InternalServerErrorException("Internal еrror while adding a region"));
        }else {
            throw new DuplicateResourceException("Weather data for the region already exists. Make a request to update the data");
        }
    }

    @PutMapping
    @Operation(summary = "Update temperature data for a specific region", responses = {
            @ApiResponse(responseCode = "200", description = "Weather data updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDto.class))),
            @ApiResponse(responseCode = "201", description = "Weather data created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WeatherDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WeatherDto> updateTemperature(@PathVariable("regionName") String regionName, @RequestBody WeatherDto weatherDto) {
        if (weatherService.temperatureWithThisDateAtRegionExist(regionName, weatherDto.getMeasuringDate())) {
            return weatherService
                    .updateTemperatureByRegionName(regionName, weatherDto)
                    .map(weather -> ResponseEntity.status(HttpStatus.OK).body(weatherMapper.weatherToWeatherDto(weather)))
                    .orElseThrow(() -> new InternalServerErrorException("Internal еrror while updating data of the region"));
        } else {
            return createNewRegion(regionName, weatherDto);
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete weather data for a specific region", responses = {
            @ApiResponse(responseCode = "200", description = "Weather data deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Weather data not found for the specified region",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteRegion(@PathVariable("regionName") String regionName) {
        return weatherService
                .deleteRegionData(regionName)
                .map(num -> ResponseEntity.status(HttpStatus.OK).body("Removed %d weather objects for %s".formatted(num, regionName)))
                .orElseThrow(() -> new NotFoundException("No temperature data was found for this city"));
    }
}