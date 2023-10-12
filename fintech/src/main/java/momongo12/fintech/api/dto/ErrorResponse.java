package momongo12.fintech.api.dto;

import lombok.*;

/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int statusCode;
    private String message;
}
