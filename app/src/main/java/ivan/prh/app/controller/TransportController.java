package ivan.prh.app.controller;

import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.service.TransportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/Transport")
public class TransportController extends BaseController{

    @Autowired
    TransportService transportService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransport(@Valid @PathVariable("id") long id) {
        return ResponseEntity.ok(transportService.getTransport(id));
    }

    @PostMapping("")
    public ResponseEntity<?> createTransport(@Valid @RequestBody TransportDto transportDto) {
        return ResponseEntity.ok(transportService.createTransport(transportDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransport(@Valid @PathVariable("id") long id, @RequestBody TransportDto transportDto) {
        return ResponseEntity.ok(transportService.updateTransport(id, transportDto));
    }

    @DeleteMapping("/{id}")
    public void deleteTransport(@Valid @PathVariable("id") long id) {
        transportService.deleteTransport(id);
    }


}
