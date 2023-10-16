package ivan.prh.app.config;

import ivan.prh.app.model.Role;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {
        Role role = new Role();
        role.setId(Long.valueOf("1"));
        role.setName("ROLE_USER");
        roleRepository.save(role);

        role.setId(Long.valueOf("2"));
        role.setName("ROLE_ADMIN");
        roleRepository.save(role);

        User user = new User();
        user.setId(Long.valueOf("1"));
        user.setUserName("admin");
        user.setPassword("$2a$10$OOh2OpOoEmrHf59JlRurHuzfFzFxfCo4TkmR8QEO5MiCQJxbhdunC");
        user.setBalance(0);
        user.setRoles(List.of(role));
        accountRepository.save(user);
    }
}
