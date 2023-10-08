package momongo12.fintech.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeatherApiErrorResponse {
    int code;
    String message;
}
