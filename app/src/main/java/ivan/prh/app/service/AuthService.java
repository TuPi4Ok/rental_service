package ivan.prh.app.service;

import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.dto.user.AuthUserResponse;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    AccountRepository accountRepository;

    public AuthUserResponse createAuthToken(AuthUserRequest authRequest) {
        try {
            accountRepository.findUserByUserName(authRequest.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Неправильный логин или пароль");
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return new AuthUserResponse(token);
    }


}
