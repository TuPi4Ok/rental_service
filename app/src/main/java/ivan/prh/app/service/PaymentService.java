package ivan.prh.app.service;

import ivan.prh.app.config.DataLoader;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    DataLoader dataLoader;
    @Autowired
    UserService userService;

    public void overflowBalance(long id) {
        User user = userService.getCurrentUser();
        if (user.getRoles().contains(dataLoader.getRoleAdmin())) {
            overflow(id);
        } else if (user.getRoles().contains(dataLoader.getRoleUser()) && id == user.getId()) {
            overflow(id);
        } else {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Недостаточно прав");
        }

    }

    private void overflow(long id) {
        User user = userService.findById(id);
        double overflow = 250000;
        user.setBalance(user.getBalance() + overflow);
        accountRepository.save(user);
    }

    public void takeDownBalance(Rent rent) {
        User user = rent.getUser();
        user.setBalance(user.getBalance() - rent.getFinalPrice());
        accountRepository.save(user);
    }
}
