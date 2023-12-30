package ivan.prh.app.controller.admin;

import io.swagger.annotations.*;
import ivan.prh.app.dto.rent.RentDtoRequest;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.User;
import ivan.prh.app.service.admin.AdminRentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/Admin")
@Api(value = "AdminRentController", description = "Админ-контроллер для управления арендой")
public class AdminRentController {
    @Autowired
    AdminRentService rentService;

    @ApiOperation(value = "Получение информации об аренде по id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @GetMapping("/Rent/{rentId}")
    public Rent getRent(
            @ApiParam(value = "id аренды", required = true) @PathVariable("rentId") long id) {
        return rentService.getRent(id);
    }

    @ApiOperation(value = "Получение списка аренд пользователя по его id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @GetMapping("/UserHistory/{userId}")
    public List<Rent> getRentHistory(
            @ApiParam(value = "id пользователя", required = true) @PathVariable("userId") long id) {
        return rentService.getHistory(id);
    }

    @ApiOperation(value = "Получение списка аренд у транспорта по его id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @GetMapping("/TransportHistory/{transportId}")
    public List<Rent> getTransportRentHistory(
            @ApiParam(value = "id транспорта", required = true) @PathVariable("transportId") long id) {
        return rentService.getRentTransportHistory(id);
    }
    @ApiOperation(value = "Создание аренды", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @PostMapping("/Rent")
    public Rent createRent(@Valid @RequestBody RentDtoRequest rentDtoRequest) {
        return rentService.createRent(rentDtoRequest);
    }

    @ApiOperation(value = "Окончание аренды", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @PostMapping("/Rent/End/{rentId}")
    public Rent endRent(
            @ApiParam(value = "Id аренды", required = true) @PathVariable("rentId") long id,
            @ApiParam(value = "Широта", required = true) @RequestParam("lat") double lat,
            @ApiParam(value = "Долгота", required = true) @RequestParam("long") double longitude) {
        return rentService.endRent(id, lat, longitude);
    }

    @ApiOperation(value = "Обновление аренды по id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @PutMapping("/Rent/{id}")
    public Rent updateRent(
            @ApiParam(value = "Id аренды", required = true) @PathVariable("id") long id,
            @Valid @RequestBody RentDtoRequest rentDtoRequest) {
        return rentService.updateRent(id, rentDtoRequest);
    }

    @ApiOperation(value = "Удаление аренды по id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @DeleteMapping("/Rent/{rentId}")
    public void deleteRent(
            @ApiParam(value = "Id аренды", required = true) @PathVariable("rentId") long id) {
         rentService.deleteRent(id);
    }
}
