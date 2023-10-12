package momongo12.fintech.api.dto;

import lombok.*;

import java.time.Instant;

/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDto {
    private int regionId;
    private String regionName;
    private double temperatureValue;
    private Instant measuringDate;
}
