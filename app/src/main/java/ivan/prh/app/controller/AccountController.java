package ivan.prh.app.controller;

import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.service.AuthService;
import ivan.prh.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/Account")
public class AccountController {
    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;

    @PostMapping("/SignIn")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthUserRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/SignUp")
    public ResponseEntity<?> createNewUser(@RequestBody AuthUserRequest authRequest) {
        return userService.createNewUser(authRequest);
    }

    @GetMapping("/Me")
    public ResponseEntity<?> getUser(@RequestHeader HttpHeaders headers) {
        return userService.getUser(headers.toSingleValueMap().get("authorization").substring(7));
    }
}