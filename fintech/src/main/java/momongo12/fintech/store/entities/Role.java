package momongo12.fintech.store.entities;

import jakarta.persistence.*;

import lombok.*;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author momongo12
 * @version 1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
    @SequenceGenerator(name = "role_generator", sequenceName = "ROLE_SEQUENCE", allocationSize = 1)
    private Long id;
    private String name;

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
