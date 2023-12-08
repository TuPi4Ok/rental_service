package ivan.prh.app.service.admin;

import ivan.prh.app.dto.rent.RentDtoRequest;
import ivan.prh.app.exception.NotFoundException;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.RentRepository;
import ivan.prh.app.service.PaymentService;
import ivan.prh.app.service.RentService;
import ivan.prh.app.service.TransportService;
import ivan.prh.app.service.UserService;
import ivan.prh.app.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminRentService {

    @Autowired
    RentRepository rentRepository;
    @Autowired
    UserService userService;
    @Autowired
    TransportService transportService;
    @Autowired
    Mapper mapper;
    @Autowired
    RentService rentService;
    @Autowired
    PaymentService paymentService;

    public Rent getRent(long id) {
        if(!rentRepository.existsById(id))
            throw new NotFoundException("Аренда с таки id не найдена");
        return rentRepository.getRentById(id).get();
    }

    public List<Rent> getHistory(long userId) {
        User user = userService.findById(userId);
        if(rentRepository.getRentsByUser(user).isEmpty())
            throw new NotFoundException("Аренды у пользователя не найдены");
        return rentRepository.getRentsByUser(user);
    }

    public List<Rent> getRentTransportHistory(long id) {
        Transport transport = transportService.findTransportById(id);
        if(rentRepository.getRentsByTransport(transport).isEmpty())
            throw new NotFoundException("Аренды не найдены");
        return rentRepository.getRentsByTransport(transport);
    }

    public Rent createRent(RentDtoRequest rentDtoRequest) {
        var rent = mapper.map(rentDtoRequest);
        Transport transport = transportService.findTransportById(rentDtoRequest.getTransportId());
        if(rent.getTimeEnd() != null) {
            transport.setCanBeRented(false);
            rent.setTransport(transport);
        }
        else {
            transport.setCanBeRented(true);
        }
        if (userService.getCurrentUser().equals(transport.getUser()))
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Нельзя арендовать собственный транспорт");
        rent.setUser(userService.getCurrentUser());
        rentRepository.save(rent);
        return rent;
    }

    public Rent endRent(long id, double lat, double longitude) {
        Rent rent = getRent(id);
        transportService.changeCanBeRented(rent.getTransport(), true);
        transportService.changeCoordinates(rent.getTransport(), lat, longitude);

        rent.setTimeEnd(LocalDateTime.now());
        rent.setFinalPrice(rentService.getCostRent(rent));
        paymentService.takeDownBalance(rent);
        rentRepository.save(rent);
        return rent;
    }

    public Rent updateRent(long id, RentDtoRequest rentDtoRequest) {
        Rent rent = getRent(id);
        rent = mapper.update(rentDtoRequest, rent);
        rent.setUser(userService.findById(rentDtoRequest.getUserId()));

        Transport transport = transportService.findTransportById(rentDtoRequest.getTransportId());
        if(rent.getTimeEnd() != null) {
            transport.setCanBeRented(false);
            rent.setTransport(transport);
        }
        else {
            transport.setCanBeRented(true);
        }
        return rentRepository.save(rent);
    }

    public String  deleteRent(long id) {
        Rent rent = getRent(id);
        rentRepository.delete(rent);
        return "Аренда удалена";
    }
}
