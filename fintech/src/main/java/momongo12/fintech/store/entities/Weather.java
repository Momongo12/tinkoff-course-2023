package momongo12.fintech.store.entities;


import lombok.*;
import java.time.Instant;


/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Weather {

    private final int regionId;
    private final String regionName;
    private double temperatureValue;
    private Instant measuringDate;
}
