package momongo12.fintech.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeatherApiResponse {

    @JsonProperty("location")
    Location location;

    @JsonProperty("current")
    CurrentWeather weatherData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Location {
        @JsonProperty("region")
        String regionName;

        @JsonProperty("localtime_epoch")
        Instant localtime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CurrentWeather {

        @JsonProperty("temp_c")
        BigDecimal temperature;

        @JsonProperty("wind_kph")
        BigDecimal windKph;

        @JsonProperty("wind_dir")
        String windDir;

        @JsonProperty("humidity")
        Integer humidity;
    }
}
