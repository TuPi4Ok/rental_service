package ivan.prh.app.repository;

import ivan.prh.app.model.Transport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportRepository extends CrudRepository<Transport, Long> {
    Optional<Transport> getTransportById(long id);
    List<Transport> findAll();
    List<Transport> findTransportsByTransportType(String type);
}
