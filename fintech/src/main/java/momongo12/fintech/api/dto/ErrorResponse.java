package momongo12.fintech.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    int statusCode;
    String message;
}
