package ivan.prh.app.controller.admin;

import io.swagger.annotations.*;
import ivan.prh.app.controller.BaseController;
import ivan.prh.app.dto.admin.AdminRequest;
import ivan.prh.app.dto.user.UserDto;
import ivan.prh.app.model.User;
import ivan.prh.app.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Admin/Account")
@Api(value = "AdminAccountController", description = "Админ-контроллер для управления пользователями")
public class AdminAccountController extends BaseController {
    @Autowired
    AdminService adminService;

    @ApiOperation(value = "Получение списка пользователей", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @GetMapping("")
    public List<User> getUsers(
            @ApiParam(value = "Начало выборки", required = true) @RequestParam("start") int start,
            @ApiParam(value = "Размер выборки", required = true) @RequestParam("count") int count) {
        return adminService.getAllUsersWithParam(start, count);
    }

    @ApiOperation(value = "Получение пользователя по id", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") long id) {
        return adminService.getUser(id);
    }

    @ApiOperation(value = "Создание пользователя", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь создан"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 400, message = "Имя пользователя уже занято")
    })
    @PostMapping("")
    public User createUser(@RequestBody AdminRequest adminRequest) {
        return adminService.createUser(adminRequest);
    }

    @ApiOperation(value = "Обновление пользователя", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь обновлен"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 400, message = "Имя пользователя уже занято"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public User updateUser(
            @ApiParam(value = "id пользователя", required = true) @PathVariable("id") long id,
            @RequestBody AdminRequest adminRequest) {
        return adminService.updateUser(id, adminRequest);
    }
    @ApiOperation(value = "Удаление пользователя", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь удален"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public void deleteUser(@ApiParam(value = "id пользователя", required = true) @PathVariable("id") long id) {
        adminService.deleteUser(id);
    }
}
