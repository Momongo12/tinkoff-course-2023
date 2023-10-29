package momongo12.fintech.store.repositories;

import momongo12.fintech.store.entities.WeatherType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Momongo12
 * @version 1.0
 */
@Repository
public interface WeatherTypeRepository extends JpaRepository<WeatherType, Integer> {
}