package ivan.prh.app.controller;

import ivan.prh.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class PaymentController extends BaseController{

    @Autowired
    PaymentService paymentService;

    @PostMapping("/Payment/Hesoyam/{accountId}")
    public ResponseEntity<?> overflowBalanceUser(@PathVariable("accountId") long id) {
        paymentService.overflowBalance(id);
        return ResponseEntity.ok("Баланс пользователя пополнен");
    }
}
