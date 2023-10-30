package ivan.prh.app.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.dto.user.AuthUserResponse;
import ivan.prh.app.model.User;
import ivan.prh.app.service.AuthService;
import ivan.prh.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/Account")
@Api(value = "UserAccountController", description = "Контроллер для управления пользователями")
public class UserAccountController extends BaseController {
    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;

    @ApiOperation(value = "Получение токена авторизации", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @PostMapping("/SignIn")
    public AuthUserResponse createAuthToken(@Valid @RequestBody AuthUserRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @ApiOperation(value = "Создание нового пользователя", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 400, message = "Имя пользователя уже занято")
    })
    @PostMapping("/SignUp")
    public User createNewUser(@Valid @RequestBody AuthUserRequest authRequest) {
        return userService.createNewUser(authRequest);
    }

    @ApiOperation(value = "Получение данных о текущем пользователе", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @GetMapping("/Me")
    public User getUser() {
        return userService.getUserMe();
    }

    @ApiOperation(value = "Обновление пользователя", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @PutMapping("/Update")
    public User updateUser(@Valid @RequestBody AuthUserRequest authUserRequest) {
        return userService.updateUser(authUserRequest);
    }

    @ApiOperation(value = "Выход из аккаунта", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @PostMapping("/SignOut")
    public void signOutUser(@RequestHeader HttpHeaders headers) {
        userService.signOutUser(headers.toSingleValueMap().get("authorization").substring(7));
    }

}