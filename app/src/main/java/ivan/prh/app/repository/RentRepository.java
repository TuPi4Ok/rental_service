package ivan.prh.app.repository;

import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentRepository extends CrudRepository<Rent, Long> {
    Optional<Rent> getRentById(long id);
    List<Rent> getRentsByUser(User user);
    List<Rent> getRentsByTransport(Transport transport);
}
