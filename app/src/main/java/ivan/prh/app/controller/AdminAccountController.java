package ivan.prh.app.controller;

import ivan.prh.app.dto.admin.AdminRequest;
import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.service.AdminService;
import ivan.prh.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Admin/Account")
public class AdminAccountController {
    @Autowired
    AdminService adminService;
    @GetMapping("")
    public ResponseEntity<?> getUsers(@RequestParam("start") int start, @RequestParam("count") int count) {
        return adminService.getAllUsersWithParam(start, count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        return adminService.getUser(id);
    }
    @PostMapping("")
    public ResponseEntity<?> createUser(@RequestBody AdminRequest adminRequest) {
        return adminService.createUser(adminRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody AdminRequest adminRequest) {
        return adminService.updateUser(id, adminRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        return adminService.deleteUser(id);
    }
}
