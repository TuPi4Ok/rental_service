package ivan.prh.app.controller;

import io.swagger.annotations.*;
import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/Transport")
@Api(value = "TransportController", description = "Контроллер для управления транспортом")
public class TransportController extends BaseController{

    @Autowired
    TransportService transportService;

    @ApiOperation(value = "Получение транспорта по Id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Транспорт не найден")
    })
    @GetMapping("/{id}")
    public Transport getTransport(@ApiParam(value = "Id транспорта", required = true) @Valid @PathVariable("id") long id) {
        return transportService.getTransport(id);
    }
    @ApiOperation(value = "Создание транспорта", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Транспорт не найден")
    })
    @PostMapping("")
    public Transport createTransport(@Valid @RequestBody TransportDto transportDto) {
        return transportService.createTransport(transportDto);
    }
    @ApiOperation(value = "Обновление транспорта по Id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Транспорт не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransport(
            @ApiParam(value = "Id транспорта", required = true) @Valid @PathVariable("id") long id, @RequestBody TransportDto transportDto) {
        return ResponseEntity.ok(transportService.updateTransport(id, transportDto));
    }
    @ApiOperation(value = "Удаление транспорта по Id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Транспорт не найден")
    })
    @DeleteMapping("/{id}")
    public void deleteTransport(@ApiParam(value = "Id транспорта", required = true) @Valid @PathVariable("id") long id) {
        transportService.deleteTransport(id);
    }


}
