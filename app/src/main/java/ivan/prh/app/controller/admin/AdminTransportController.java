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
    public ResponseEntity<?> getTransports(/*здесь должны быть параметры!!!!*/) {
        return transportService.getTransport();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransport(@PathVariable("id") long id) {
        return transportService.getTransport(id);
    }

    @PostMapping("")
    public ResponseEntity<?> createTransport(@Valid @RequestBody AdminTransportDto transportDto) {
        return transportService.createTransport(transportDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransport(@PathVariable("id") long id, @Valid @RequestBody AdminTransportDto transportDto) {
        return transportService.updateTransport(id, transportDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransport(@PathVariable("id") long id) {
        return transportService.deleteTransport(id);
    }

}
