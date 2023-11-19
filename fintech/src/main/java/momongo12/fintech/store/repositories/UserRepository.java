package momongo12.fintech.store.repositories;

import momongo12.fintech.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author momongo12
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
}
