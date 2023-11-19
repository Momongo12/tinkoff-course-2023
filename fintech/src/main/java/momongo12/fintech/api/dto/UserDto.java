package momongo12.fintech.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author momongo12
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDto {
    private String username;
    private String login;
}