package momongo12.fintech.store.entities;

import jakarta.persistence.*;

import lombok.*;

import java.time.Instant;

/**
 * @author Momongo12
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private int id;

    @Column(name = "temperature")
    private double temperatureValue;

    @Column(name = "measuring_date")
    private Instant measuringDate;

    @OneToOne
    @JoinColumn(name = "weather_type_id")
    private WeatherType weatherType;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn
    private Region region;
}
