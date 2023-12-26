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

    public User overflowBalance(long id) {
        User user = userService.getCurrentUser();
        if (user.getRoles().contains(dataLoader.getRoleAdmin())) {
            user = overflow(id);
        } else if (user.getRoles().contains(dataLoader.getRoleUser()) && id == user.getId()) {
            user = overflow(id);
        } else {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Недостаточно прав");
        }
        return user;
    }

    private User overflow(long id) {
        User user = userService.findById(id);
        double overflow = 250000;
        user.setBalance(user.getBalance() + overflow);
        return accountRepository.save(user);
    }

    public User takeDownBalance(Rent rent) {
        User user = rent.getUser();
        user.setBalance(user.getBalance() - rent.getFinalPrice());
        return accountRepository.save(user);
    }
}
