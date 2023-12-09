package ivan.prh.app.service;

import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.RentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentService {
    @Autowired
    RentRepository rentRepository;
    @Autowired
    UserService userService;
    @Autowired
    TransportService transportService;
    @Autowired
    PaymentService paymentService;
    public Rent getRent(long id) {
        if(rentRepository.getRentById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Аренда не найдена");
        Rent rent = rentRepository.getRentById(id).get();
        User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).get();
        if(!(user.equals(rent.getUser()) || user.equals(rent.getTransport().getUser())))
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Недостаточно прав");
        return rent;
    }

    public List<Rent> getRentHistory() {
        User user = userService.getCurrentUser();
        if(rentRepository.getRentsByUser(user).isEmpty())
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Аренды не найдены");
        return rentRepository.getRentsByUser(user);
    }

    public List<Rent> getRentTransportHistory(long id) {
        Transport transport = transportService.findTransportById(id);
        User user = userService.getCurrentUser();
        if(!user.equals(transport.getUser()))
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Недостаточно прав");
        if(rentRepository.getRentsByTransport(transport).isEmpty())
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Аренды не найдены");
        return rentRepository.getRentsByTransport(transport);
    }

    public String createRent(long id, String rentType) {
        Rent rent = new Rent();

        Transport transport = transportService.findTransportById(id);
        if(!transport.isCanBeRented())
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Транспорт уже арендован");
        transport.setCanBeRented(false);
        rent.setTransport(transport);

        if (userService.getCurrentUser().equals(transport.getUser()))
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Нельзя арендовать собственный транспорт");
        rent.setUser(userService.getCurrentUser());
        if (rent.getUser().getBalance() < 0)
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Баланс уже находится в минусе!");

        rent.setPriceType(rentType);
        rent.setPrice();
        rent.setTimeStart(LocalDateTime.now());
        rentRepository.save(rent);
        return "Аренда начата";
    }

    public String endRent(long id, double lat, double longitude) {
        if (rentRepository.getRentById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Аренда не найдена");
        Rent rent = rentRepository.getRentById(id).get();
        if (!userService.getCurrentUser().equals(rent.getUser()))
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Недостаточно прав");
        if (rent.getTimeEnd() != null)
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Аренда уже завершена");

        transportService.changeCanBeRented(rent.getTransport(), true);
        transportService.changeCoordinates(rent.getTransport(), lat, longitude);

        rent.setTimeEnd(LocalDateTime.now());
        rent.setFinalPrice(getCostRent(rent));
        paymentService.takeDownBalance(rent);
        rentRepository.save(rent);
        return "Аренда завершена";
    }

    public double getCostRent(Rent rent) {
        long unit = 1;
        Duration duration = Duration.between(rent.getTimeStart(), rent.getTimeEnd());
        if(rent.getPriceType().equals("Minutes")) {
            unit += duration.toMinutes();
        }
        if(rent.getPriceType().equals("Days")) {
            unit += duration.toDays();
        }

        return rent.getPriceOfUnit() * unit;
    }

}
