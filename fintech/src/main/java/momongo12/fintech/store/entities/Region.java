package momongo12.fintech.store.entities;

import jakarta.persistence.*;

import lombok.*;

import java.util.List;

/**
 * @author Momongo12
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "region")
public class Region {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<Weather> weathers;
}
