package ivan.prh.app.controller;

import ivan.prh.app.dto.AuthRequest;
import ivan.prh.app.dto.AuthResponse;
import ivan.prh.app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Account")
public class AccountController {
    @Autowired
    AuthService authService;

    @PostMapping("/SignIn")
    public ResponseEntity<?> signIn(@RequestBody AuthRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }
}