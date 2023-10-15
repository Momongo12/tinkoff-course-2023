package momongo12.fintech.store.entities;

import jakarta.persistence.*;

import lombok.*;

/**
 * @author Momongo12
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "weather_type")
public class WeatherType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    public int id;

    @Column(name = "description")
    public String description;

    @Column(name = "representation")
    public String representation;
}