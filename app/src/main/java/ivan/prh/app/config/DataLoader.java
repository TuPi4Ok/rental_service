package ivan.prh.app.config;

import ivan.prh.app.model.Role;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.repository.RoleRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Getter
    private Role roleUser, roleAdmin;
    @Override
    public void run(String... args) throws Exception {
        roleUser = new Role();
        roleUser.setId(Long.valueOf("1"));
        roleUser.setName("ROLE_USER");
        roleRepository.save(roleUser);

        roleAdmin = new Role();
        roleAdmin.setId(Long.valueOf("2"));
        roleAdmin.setName("ROLE_ADMIN");
        roleRepository.save(roleAdmin);

        User user = new User();
        user.setId(Long.valueOf("1"));
        user.setUserName("admin");
        user.setPassword("$2a$10$OOh2OpOoEmrHf59JlRurHuzfFzFxfCo4TkmR8QEO5MiCQJxbhdunC");
        user.setBalance(0);
        user.setRoles(List.of(roleAdmin));
        accountRepository.save(user);
    }
}
