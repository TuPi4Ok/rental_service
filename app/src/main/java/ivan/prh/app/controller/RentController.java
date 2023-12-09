package ivan.prh.app.controller;

import io.swagger.annotations.*;
import ivan.prh.app.model.Rent;
import ivan.prh.app.service.RentService;
import ivan.prh.app.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping("/Rent")
@Validated
@Api(value = "RentController", description = "Контроллер для управления арендой")
public class RentController extends BaseController {
    @Autowired
    RentService rentService;
    @Autowired
    TransportService transportService;
    @ApiOperation(value = "Получение информации об аренде по id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @GetMapping("/{rentId}")
    public Rent getRentById(@ApiParam(value = "id аренды", required = true) @PathVariable("rentId") long id) {
        return rentService.getRent(id);
    }
    @ApiOperation(value = "Получение списка аренда пользователя", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @GetMapping("/MyHistory")
    public List<Rent> getRentHistory() {
        return rentService.getRentHistory();
    }

    @ApiOperation(value = "Получение списка аренда у транспорта по его id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @GetMapping("/TransportHistory/{transportId}")
    public List<Rent> getRentTransportHistory(@ApiParam(value = "id транспорта", required = true) @PathVariable("transportId") long id) {
        return rentService.getRentTransportHistory(id);
    }

    @PostMapping("/New/{transportId}")
    public Rent createRent(@ApiParam(value = "Id транспорта", required = true) @PathVariable("transportId") long id,
                                        @ApiParam(value = "Тип аренды [Minutes, Days]", required = true) @Pattern(regexp = "^(Minutes|Days)$", message = "Введен неверный тип аренды")
                                        @RequestParam("rentType") String rentType) {
        return rentService.createRent(id, rentType);
    }
    @ApiOperation(value = "Создание аренды", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Аренда не найдена")
    })
    @PostMapping("/End/{rentId}")
    public ResponseEntity<?> endRent(
          @ApiParam(value = "Id аренды", required = true) @PathVariable("rentId") long id,
          @ApiParam(value = "Широта", required = true) @RequestParam("lat") double lat,
          @ApiParam(value = "Долгота", required = true) @RequestParam("long") double longitude) {
        return ResponseEntity.ok(rentService.endRent(id, lat, longitude));
    }

    
    @GetMapping("/Transport")
    public ResponseEntity<?> getRentByParam(
            @ApiParam(value = "Широта", required = true) @RequestParam("lat") double lat,
            @ApiParam(value = "Долгота", required = true) @RequestParam("long") double longitude,
            @ApiParam(value = "Радиус круга поиска транспорта", required = true) @RequestParam("radius") double radius,
            @ApiParam(value = "Тип транспорта [Car, Bike, Scooter, All]", required = true)
            @Pattern(regexp = "^(Car|Bike|Scooter|All)$", message = "Введен неверный тип транспорта") @RequestParam("type") String type) {
        return ResponseEntity.ok(transportService.getTransportCanBeRented(lat, longitude, radius, type));
    }
}
