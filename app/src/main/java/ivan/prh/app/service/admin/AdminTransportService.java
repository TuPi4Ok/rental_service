package ivan.prh.app.service.admin;

import ivan.prh.app.dto.transport.AdminTransportDto;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.service.UserService;
import ivan.prh.app.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminTransportService {

    @Autowired
    TransportRepository transportRepository;

    @Autowired
    UserService userService;
    @Autowired
    Mapper mapper;
    @Autowired
    AdminRentService rentService;

    public List<Transport> getTransport(int start, int count) {
        var size = count + start - 1;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Order.asc("id")));
        List<Transport> TransportList = transportRepository.findAll(pageable);

        List<Transport> resultTransport = new ArrayList<>();
        int offset = 0;
        for(Transport transport : TransportList) {
            if(offset >= start - 1 && offset < size)
                resultTransport.add(transport);
            offset++;
        }

        if (resultTransport.isEmpty())
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Транспорты не найдены");
        else
            return resultTransport;
    }

    public Transport getTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Транспорт не найден");
        return transportRepository.getTransportById(id).get();
    }

    public Transport createTransport(AdminTransportDto transportDto) {
        Transport transport = new Transport();
        mapper.map(transportDto);

        userService.findById(transportDto.getOwnerId());
        transport.setUser(userService.findById(transportDto.getOwnerId()));
        return transportRepository.save(transport);
    }


    public Transport updateTransport(long id, AdminTransportDto transportDto) {
        if(transportRepository.getTransportById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Транспорт с таким id не найден");
        Transport transport = transportRepository.getTransportById(id).get();
        transport = mapper.update(transportDto, transport);
        userService.findById(transportDto.getOwnerId());
        transport.setUser(userService.findById(transportDto.getOwnerId()));
        return transportRepository.save(transport);
    }

    public void deleteTransport(long id) {
        Transport transport = getTransport(id);
        for(Rent rent : transport.getRents()) {
            rentService.deleteRent(rent.getId());
        }
        transportRepository.delete(transport);
    }
}
