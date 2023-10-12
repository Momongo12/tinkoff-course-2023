package momongo12.fintech.api.dto;

import lombok.*;

/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherApiErrorResponse {
    private int code;
    private String message;
}
