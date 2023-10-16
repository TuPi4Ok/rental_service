package ivan.prh.app.service;

import ivan.prh.app.dto.admin.AdminRequest;
import ivan.prh.app.model.Role;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    AccountRepository accountRepository;
    @Lazy
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleService roleService;

     public ResponseEntity<?> getAllUsersWithParam(int start, int count) {
        List<User> userList = accountRepository.findAll();
         if (userList.isEmpty())
             return ResponseEntity.notFound().build();
         else
             return ResponseEntity.ok(userList);
     }

    public ResponseEntity<?> getUser(long id) {
         Optional<User> findUser = accountRepository.findUserById(id);
         if (findUser.isPresent())
            return ResponseEntity.ok(findUser);
         else
             return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> createUser(AdminRequest adminRequest) {
         if(accountRepository.findUserByUserName(adminRequest.getUsername()).isPresent())
            return ResponseEntity.status(400).body("Пользователь с таким именем уже существует");

         User user = new User();
         user.setUserName(adminRequest.getUsername());
         user.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
         user.setBalance(adminRequest.getBalance());
         if(adminRequest.isAdmin())
             user.setRoles(List.of(roleService.findRoleByName("ROLE_ADMIN")));
         else
             user.setRoles(List.of(roleService.findRoleByName("ROLE_USER")));
         accountRepository.save(user);
         return ResponseEntity.ok("Пользователь создан");
    }

    public ResponseEntity<?> deleteUser(long id) {
         if (accountRepository.findUserById(id).isPresent()) {
             accountRepository.delete(accountRepository.findUserById(id).get());
             return ResponseEntity.ok("Пользователь удален");
         }
         else {
             return ResponseEntity.notFound().build();
         }
    }

    public ResponseEntity<?> updateUser(long id, AdminRequest adminRequest) {
         if(accountRepository.findUserByUserName(adminRequest.getUsername()).isPresent())
             return ResponseEntity.status(400).body("Пользователь с таким именем уже существует");

         if(accountRepository.findUserById(id).isEmpty())
             return ResponseEntity.notFound().build();

         User currentUser = accountRepository.findUserById(id).get();
         currentUser.setUserName(adminRequest.getUsername());
         currentUser.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
         currentUser.setBalance(adminRequest.getBalance());

         if(adminRequest.isAdmin())
             currentUser.setRoles(List.of(roleService.findRoleByName("ROLE_ADMIN")));
         else
             currentUser.setRoles(List.of(roleService.findRoleByName("ROLE_USER")));
         accountRepository.save(currentUser);
         return ResponseEntity.ok("Пользователь обновлен");
    }
}
