package ivan.prh.app.service.admin;

import ivan.prh.app.dto.transport.AdminTransportDto;
import ivan.prh.app.model.Transport;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.service.UserService;
import ivan.prh.app.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminTransportService {

    @Autowired
    TransportRepository transportRepository;

    @Autowired
    UserService userService;
    @Autowired
    MapperUtils mapperUtils;

    public ResponseEntity<?> getTransport() {
        return ResponseEntity.ok(transportRepository.findAll());
    }

    public ResponseEntity<?> getTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transportRepository.getTransportById(id));
    }

    public ResponseEntity<?> createTransport(AdminTransportDto transportDto) {
        Transport transport = new Transport();
        mapperUtils.transportDtoToTransport(transportDto, transport);

        userService.findById(transportDto.getOwnerId());
        transport.setUser(userService.findById(transportDto.getOwnerId()));
        transportRepository.save(transport);
        return ResponseEntity.ok("Транспорт создан");
    }


    public ResponseEntity<?> updateTransport(long id, AdminTransportDto transportDto) {
        if(transportRepository.getTransportById(id).isEmpty())
            return ResponseEntity.notFound().build();
        Transport transport = transportRepository.getTransportById(id).get();
        transport = mapperUtils.transportDtoToTransport(transportDto, transport);
        userService.findById(transportDto.getOwnerId());
        transport.setUser(userService.findById(transportDto.getOwnerId()));
        transportRepository.save(transport);
        return ResponseEntity.ok("Транспорт обновлен");
    }

    public ResponseEntity<?> deleteTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            return ResponseEntity.notFound().build();
        Transport transport = transportRepository.getTransportById(id).get();
        transportRepository.delete(transport);
        return ResponseEntity.ok("Транспорт удален");
    }
}
