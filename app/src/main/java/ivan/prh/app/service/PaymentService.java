package ivan.prh.app.service;

import ivan.prh.app.config.DataLoader;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    DataLoader dataLoader;

    public ResponseEntity<?> overflowBalance(long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = accountRepository.findUserByUserName(username).get();
        if (user.getRoles().contains(dataLoader.getRoleAdmin())) {
            return overflow(id);
        } else if (user.getRoles().contains(dataLoader.getRoleUser()) && id == user.getId()) {
            return overflow(id);
        } else {
            return ResponseEntity.status(403).build();
        }

    }

    private ResponseEntity<?> overflow(long id) {
        if(accountRepository.findUserById(id).isEmpty())
            return ResponseEntity.notFound().build();
        User user = accountRepository.findUserById(id).get();
        double overflow = 250000;
        user.setBalance(user.getBalance() + overflow);
        accountRepository.save(user);
        return ResponseEntity.ok("Баланс пользователя пополнен на " + overflow);
    }
}
