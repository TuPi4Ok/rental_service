package ivan.prh.app.controller;

import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Transport")
public class TransportController {

    @Autowired
    TransportService transportService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransport(@PathVariable("id") long id) {
        return transportService.getTransport(id);
    }

    @PostMapping("")
    public ResponseEntity<?> createTransport(@RequestBody TransportDto transportDto) {
        return transportService.createTransport(transportDto);
    }
}
