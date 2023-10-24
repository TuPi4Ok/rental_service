package ivan.prh.app.controller.admin;

import ivan.prh.app.dto.rent.RentDtoRequest;
import ivan.prh.app.service.admin.AdminRentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Admin")
public class AdminRentController {
    @Autowired
    AdminRentService rentService;

    @GetMapping("/Rent/{rentId}")
    public ResponseEntity<?> getRent(@PathVariable("rentId") long id) {
        return ResponseEntity.ok(rentService.getRent(id));
    }

    @GetMapping("/UserHistory/{userId}")
    public ResponseEntity<?> getRentHistory(@PathVariable("userId") long id) {
        return ResponseEntity.ok(rentService.getHistory(id));
    }

    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> getTransportRentHistory(@PathVariable("transportId") long id) {
        return ResponseEntity.ok(rentService.getRentTransportHistory(id));
    }

    @PostMapping("/Rent")
    public ResponseEntity<?> createRent(@Valid @RequestBody RentDtoRequest rentDtoRequest) {
        return ResponseEntity.ok(rentService.createRent(rentDtoRequest));
    }

    @PostMapping("/Rent/End/{rentId}")
    public ResponseEntity<?> endRent(
            @PathVariable("rentId") long id,
            @RequestParam("lat") double lat,
            @RequestParam("long") double longitude) {
        return ResponseEntity.ok(rentService.endRent(id, lat, longitude));
    }

    @PutMapping("/Rent/{id}")
    public ResponseEntity<?> createRent(@PathVariable("id") long id, @Valid @RequestBody RentDtoRequest rentDtoRequest) {
        return ResponseEntity.ok(rentService.updateRent(id, rentDtoRequest));
    }

    @DeleteMapping("/Rent/{rentId}")
    public ResponseEntity<?> createRent(@PathVariable("rentId") long id) {
        return ResponseEntity.ok(rentService.deleteRent(id));
    }
}
