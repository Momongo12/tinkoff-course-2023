package momongo12.fintech;


import lombok.*;
import java.time.Instant;


/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
public class Weather {

    private final int regionId;
    private final String regionName;
    private double temperatureValue;
    private Instant measuringDate;
}
