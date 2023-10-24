package ivan.prh.app.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.service.AuthService;
import ivan.prh.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/Account")
public class UserAccountController extends BaseController {
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
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok(userService.getUserMe());
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateUser(@RequestBody AuthUserRequest authUserRequest) {
        return ResponseEntity.ok(userService.updateUser(authUserRequest));
    }

    @PostMapping("/SignOut")
    public ResponseEntity<?> signOutUser(@RequestHeader HttpHeaders headers) {
        userService.signOutUser(headers.toSingleValueMap().get("authorization").substring(7));
        return ResponseEntity.ok("Пользователь вышел из системы");
    }

}