package ivan.prh.app.repository;

import ivan.prh.app.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUserName(String userName);
}
