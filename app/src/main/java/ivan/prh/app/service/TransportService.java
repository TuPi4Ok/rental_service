package ivan.prh.app.service;

import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.exception.NotFoundException;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransportService {
    @Autowired
    TransportRepository transportRepository;

    @Autowired
    UserService userService;

    @Autowired
    MapperUtils mapperUtils;


    public Transport findTransportById(long id) {
        if(!transportRepository.existsById(id)) {
            throw new NotFoundException("Транспорт с данным id не найден");
        }
        return transportRepository.getTransportById(id).get();
    }

    public ResponseEntity<?> getTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transportRepository.getTransportById(id));
    }

    public ResponseEntity<?> createTransport(TransportDto transportDto) {
        Transport transport = new Transport();
        transport = mapperUtils.transportDtoToTransport(transportDto, transport);

        User user = userService.getCurrentUser();
        transport.setUser(user);
        transportRepository.save(transport);
        return ResponseEntity.ok("Транспорт создан");
    }

    public ResponseEntity<?> updateTransport(long id, TransportDto transportDto) {
        if(transportRepository.getTransportById(id).isEmpty())
            return ResponseEntity.notFound().build();
        Transport transport = transportRepository.getTransportById(id).get();
        User user = userService.getCurrentUser();
        if (user.getId() != transport.getUser().getId())
            return ResponseEntity.status(403).body("Недостаточно прав для изменения информации о транспорте");

        transport = mapperUtils.transportDtoToTransport(transportDto, transport);

        transportRepository.save(transport);
        return ResponseEntity.ok("Транспорт обновлен");
    }

    public ResponseEntity<?> deleteTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            return ResponseEntity.notFound().build();
        Transport transport = transportRepository.getTransportById(id).get();
        User user = userService.getCurrentUser();
        if (user.getId() != transport.getUser().getId())
            return ResponseEntity.status(403).body("Недостаточно прав для изменения информации о транспорте");
        transportRepository.delete(transport);
        return ResponseEntity.ok("Транспорт удален");
    }

    public void changeCanBeRented(Transport transport, boolean canBeRented) {
        transport.setCanBeRented(canBeRented);
        transportRepository.save(transport);
    }

    public void changeCoordinates(Transport transport, double lat, double longitude) {
        transport.setLatitude(lat);
        transport.setLongitude(longitude);
        transportRepository.save(transport);
    }

    public List<Transport> getTransportCanBeRented(double lat, double longitude, double radius, String type) {
        List<Transport> transports;
        if(type.equals("All"))
            transports = transportRepository.findAll();
        else
            transports = transportRepository.findTransportsByTransportType(type);

        List<Transport> result = new ArrayList<>();
        for(Transport transport : transports) {
            if(Math.pow(transport.getLatitude() - lat, 2) + Math.pow(transport.getLongitude() - longitude, 2) <= Math.pow(radius, 2))
                result.add(transport);
        }

        if(result.isEmpty())
            throw new NotFoundException("Транспорт в данном радиусе не найден");
        return result;
    }
}
