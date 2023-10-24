package ivan.prh.app.service;

import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.exception.AppError;
import ivan.prh.app.exception.NotFoundException;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;
    @Lazy
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    RoleService roleService;
    @Autowired
    JwtTokenUtils jwtTokenUtils;

    public Optional<User> findByUsername(String username) {
        return accountRepository.findUserByUserName(username);
    }

    public User getCurrentUser() {
        return findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).get();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                "Пользователь" + username + "не найден"
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public ResponseEntity<?> createNewUser(AuthUserRequest authUserRequest) {
        if (findByUsername(authUserRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST
            );
        }

        User user = new User();
        user.setUserName(authUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authUserRequest.getPassword()));
        user.setRoles(List.of(roleService.findRoleByName("ROLE_USER")));
        accountRepository.save(user);
        return ResponseEntity.ok("Пользователь создан");
    }

    public User getUserMe() {
        return getCurrentUser();
    }

    public User updateUser(AuthUserRequest authUserRequest) {
        if (findByUsername(authUserRequest.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Имя пользователя уже занято");
        }
        User currentUser = getCurrentUser();

        currentUser.setUserName(authUserRequest.getUsername());
        currentUser.setPassword(passwordEncoder.encode(authUserRequest.getPassword()));
        return accountRepository.save(currentUser);
    }

    public void signOutUser(String token) {
        jwtTokenUtils.addToBlackList(token);
    }

    public User findById(long id) {
        if (!accountRepository.existsById(id))
            throw new NotFoundException("Пользователь не найден");
        return accountRepository.findUserById(id).get();
    }

}
