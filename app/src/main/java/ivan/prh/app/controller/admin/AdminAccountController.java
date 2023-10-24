package ivan.prh.app.controller.admin;

import ivan.prh.app.controller.BaseController;
import ivan.prh.app.dto.admin.AdminRequest;
import ivan.prh.app.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Admin/Account")
public class AdminAccountController extends BaseController {
    @Autowired
    AdminService adminService;
    @GetMapping("")
    public ResponseEntity<?> getUsers(@RequestParam("start") int start, @RequestParam("count") int count) {
        return ResponseEntity.ok(adminService.getAllUsersWithParam(start, count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        return ResponseEntity.ok(adminService.getUser(id));
    }
    @PostMapping("")
    public ResponseEntity<?> createUser(@RequestBody AdminRequest adminRequest) {
        return ResponseEntity.ok(adminService.createUser(adminRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody AdminRequest adminRequest) {
        return ResponseEntity.ok(adminService.updateUser(id, adminRequest));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        adminService.deleteUser(id);
    }
}
