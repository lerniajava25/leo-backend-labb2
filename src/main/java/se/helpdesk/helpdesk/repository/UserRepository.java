package se.helpdesk.helpdesk.repository;

import org.springframework.data.repository.CrudRepository;
import se.helpdesk.helpdesk.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByUsername(String username);
}
