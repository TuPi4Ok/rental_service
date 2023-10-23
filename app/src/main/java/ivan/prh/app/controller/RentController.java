package ivan.prh.app.controller;

import ivan.prh.app.model.Rent;
import ivan.prh.app.service.RentService;
import ivan.prh.app.service.TransportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Rent")
@Validated
public class RentController extends BaseController {
    @Autowired
    RentService rentService;
    @Autowired
    TransportService transportService;
    @GetMapping("/{rentId}")
    public ResponseEntity<?> getRentById(@PathVariable("rentId") long id) {
        return ResponseEntity.ok(rentService.getRent(id));
    }

    @GetMapping("/MyHistory")
    public ResponseEntity<?> getRentHistory() {
        return ResponseEntity.ok(rentService.getRentHistory());
    }

    @GetMapping("/TransportHistory/{transportId}")
    public ResponseEntity<?> getRentTransportHistory(@PathVariable("transportId") long id) {
        return ResponseEntity.ok(rentService.getRentTransportHistory(id));
    }

    @PostMapping("/New/{transportId}")
    public ResponseEntity<?> createRent(@PathVariable("transportId") long id,
                                        @Pattern(regexp = "^(Minutes|Days)$", message = "Введен неверный тип аренды")
                                        @RequestParam("rentType") String rentType) {
        return ResponseEntity.ok(rentService.createRent(id, rentType));
    }

    @PostMapping("/End/{rentId}")
    public ResponseEntity<?> endRent(
            @PathVariable("rentId") long id,
            @RequestParam("lat") double lat,
            @RequestParam("long") double longitude) {
        return ResponseEntity.ok(rentService.endRent(id, lat, longitude));
    }

    @GetMapping("/Transport")
    public ResponseEntity<?> getRentByParam(
            @RequestParam("lat") double lat,
            @RequestParam("long") double longitude,
            @RequestParam("radius") double radius,
            @Pattern(regexp = "^(Car|Bike|Scooter|All)$", message = "Введен неверный тип транспорта")
            @RequestParam("type") String type) {
        return ResponseEntity.ok(transportService.getTransportCanBeRented(lat, longitude, radius, type));
    }
}
