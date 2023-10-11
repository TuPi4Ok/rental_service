package ivan.prh.app.service;

import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.exception.AppError;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.UserRepository;
import ivan.prh.app.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Lazy
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    RoleService roleService;
    @Autowired
    JwtTokenUtils jwtTokenUtils;

    public Optional<User> findByUsername(String username) {
        return userRepository.findUserByUserName(username);
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
        userRepository.save(user);
        return ResponseEntity.ok("Пользователь создан");
    }

    public ResponseEntity<?> getUser(String token) {
        String userName = jwtTokenUtils.getUsername(token);
        return ResponseEntity.of(userRepository.findUserByUserName(userName));
    }

    public ResponseEntity<?> updateUser(AuthUserRequest authUserRequest, String token) {
        if (findByUsername(authUserRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST
            );
        }
        User currentUser;
        try {
            currentUser = userRepository.findUserByUserName(jwtTokenUtils.getUsername(token)).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Подпись неправильная"), HttpStatus.BAD_REQUEST
            );
        }

        currentUser.setUserName(authUserRequest.getUsername());
        currentUser.setPassword(passwordEncoder.encode(authUserRequest.getPassword()));
        userRepository.save(currentUser);
        return ResponseEntity.ok("Пользователь обновлен");
    }

    public ResponseEntity<?> signOutUser(String token) {
        jwtTokenUtils.addToBlackList(token);
        return ResponseEntity.ok("Пользователь вышел из системы");
    }
}
