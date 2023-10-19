package ivan.prh.app.service;

import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.model.Transport;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.repository.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TransportService {
    @Autowired
    TransportRepository transportRepository;
    @Autowired
    AccountRepository accountRepository;


    public ResponseEntity<?> getTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(transportRepository.getTransportById(id));
    }

    public ResponseEntity<?> createTransport(TransportDto transportDto) {
        Transport transport = new Transport();
        transport.setCanBeRented(transportDto.isCanBeRented());
        transport.setModel(transportDto.getModel());
        transport.setColor(transportDto.getColor());
        transport.setIdentifier(transportDto.getIdentifier());
        transport.setDescription(transportDto.getDescription());
        transport.setLatitude(transportDto.getLatitude());
        transport.setLongitude(transportDto.getLongitude());
        transport.setMinutePrice(transportDto.getMinutePrice());
        transport.setDayPrice(transportDto.getDayPrice());
        transport.setTransportType(transportDto.getTransportType());

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        transport.setUser(accountRepository.findUserByUserName(username).get());
        transportRepository.save(transport);
        return ResponseEntity.ok("Транспорт создан");
    }
}
