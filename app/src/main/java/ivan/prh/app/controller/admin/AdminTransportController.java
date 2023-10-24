package ivan.prh.app.controller.admin;

import ivan.prh.app.controller.BaseController;
import ivan.prh.app.dto.transport.AdminTransportDto;
import ivan.prh.app.service.admin.AdminTransportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/Admin/Transport")
public class AdminTransportController extends BaseController {

    @Autowired
    AdminTransportService transportService;

    @GetMapping("")
    public ResponseEntity<?> getTransports(@RequestParam("start") int start, @RequestParam("count") int count) {
        return ResponseEntity.ok(transportService.getTransport(start, count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransport(@PathVariable("id") long id) {
        return ResponseEntity.ok(transportService.getTransport(id));
    }

    @PostMapping("")
    public ResponseEntity<?> createTransport(@Valid @RequestBody AdminTransportDto transportDto) {
        return ResponseEntity.ok(transportService.createTransport(transportDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransport(@PathVariable("id") long id, @Valid @RequestBody AdminTransportDto transportDto) {
        return ResponseEntity.ok(transportService.updateTransport(id, transportDto));
    }

    @DeleteMapping("/{id}")
    public void deleteTransport(@PathVariable("id") long id) {
        transportService.deleteTransport(id);
    }

}
