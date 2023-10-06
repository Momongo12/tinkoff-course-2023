package momongo12.fintech.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeatherDto {
    int regionId;
    String regionName;
    double temperatureValue;
    Instant measuringDate;
}
