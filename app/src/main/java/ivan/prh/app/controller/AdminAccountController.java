package ivan.prh.app.controller;

import ivan.prh.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Admin/Account")
public class AdminAccountController {
    @Autowired
    UserService userService;
    @GetMapping("")
    public void getUsers(@RequestParam("start") int start, @RequestParam("count") int count) {

    }
}
