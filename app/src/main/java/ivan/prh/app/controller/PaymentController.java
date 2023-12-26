package ivan.prh.app.controller;

import io.swagger.annotations.*;
import ivan.prh.app.dto.user.UserDto;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.User;
import ivan.prh.app.service.PaymentService;
import ivan.prh.app.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Api(value = "PaymentController", description = "Контроллер для управления балансом пользователей")
public class PaymentController extends BaseController {

    @Autowired
    PaymentService paymentService;
    @Autowired
    Mapper mapper;
    @ApiOperation(value = "Пополнение баланса пользователя по id", response = Rent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Неправильный логин или пароль"),
            @ApiResponse(code = 403, message = "Недостаточно прав"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @PostMapping("/Payment/Hesoyam/{accountId}")
    public UserDto overflowBalanceUser(@ApiParam(value = "Id пользователя", required = true) @PathVariable("accountId") long id) {
        return mapper.map(paymentService.overflowBalance(id));

    }
}
