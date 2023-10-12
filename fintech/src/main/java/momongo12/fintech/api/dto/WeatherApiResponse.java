package momongo12.fintech.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Momongo12
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeatherApiResponse {

    @JsonProperty("location")
    private Location location;

    @JsonProperty("current")
    private CurrentWeather weatherData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {

        @JsonProperty("region")
        private String regionName;

        @JsonProperty("localtime_epoch")
        private Instant localtime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentWeather {

        @JsonProperty("temp_c")
        private BigDecimal temperature;

        @JsonProperty("wind_kph")
        private BigDecimal windKph;

        @JsonProperty("wind_dir")
        private String windDir;

        @JsonProperty("humidity")
        private Integer humidity;
    }
}
