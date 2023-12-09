package ivan.prh.app.service;

import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransportService {
    @Autowired
    TransportRepository transportRepository;

    @Autowired
    UserService userService;

    @Autowired
    Mapper mapper;


    public Transport findTransportById(long id) {
        if(!transportRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.valueOf(404),"Транспорт с данным id не найден");
        }
        return transportRepository.getTransportById(id).get();
    }

    public Transport getTransport(long id) {
        return findTransportById(id);
    }

    public Transport createTransport(TransportDto transportDto) {
        Transport transport = mapper.map(transportDto);

        User user = userService.getCurrentUser();
        transport.setUser(user);
        return transportRepository.save(transport);
    }

    public Transport updateTransport(long id, TransportDto transportDto) {
        Transport transport = findTransportById(id);
        User user = userService.getCurrentUser();
        if (user.getId() != transport.getUser().getId())
            throw new ResponseStatusException(HttpStatus.valueOf(403),"Недостаточно прав");

        transport = mapper.update(transportDto, transport);

        return transportRepository.save(transport);
    }

    public void deleteTransport(long id) {
        Transport transport = getTransport(id);
        User user = userService.getCurrentUser();
        if (user.getId() != transport.getUser().getId())
            throw new ResponseStatusException(HttpStatus.valueOf(403),"Недостаточно прав");
        transportRepository.delete(transport);
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
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Транспорт в данном радиусе не найден");
        return result;
    }
}
