package ivan.prh.app.service.admin;

import ivan.prh.app.dto.transport.AdminTransportDto;
import ivan.prh.app.exception.NotFoundException;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.service.UserService;
import ivan.prh.app.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminTransportService {

    @Autowired
    TransportRepository transportRepository;

    @Autowired
    UserService userService;
    @Autowired
    MapperUtils mapperUtils;

    public List<Transport> getTransport(int start, int count) {
        Pageable pageable = PageRequest.of(0, count + start, Sort.by(Sort.Order.asc("id")));
        List<Transport> TransportList = transportRepository.findAll(pageable);

        List<Transport> resultTransport = new ArrayList<>();
        int offset = 0;
        for(Transport transport : TransportList) {
            if(offset >= start)
                resultTransport.add(transport);
            offset++;
        }

        if (resultTransport.isEmpty())
            throw new NotFoundException("Транспорты не найдены");
        else
            return resultTransport;
    }

    public Transport getTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            throw new NotFoundException("Транспорт не найден");
        return transportRepository.getTransportById(id).get();
    }

    public Transport createTransport(AdminTransportDto transportDto) {
        Transport transport = new Transport();
        mapperUtils.transportDtoToTransport(transportDto, transport);

        userService.findById(transportDto.getOwnerId());
        transport.setUser(userService.findById(transportDto.getOwnerId()));
        return transportRepository.save(transport);
    }


    public Transport updateTransport(long id, AdminTransportDto transportDto) {
        if(transportRepository.getTransportById(id).isEmpty())
            throw new NotFoundException("Транспорт с таким id не найден");
        Transport transport = transportRepository.getTransportById(id).get();
        transport = mapperUtils.transportDtoToTransport(transportDto, transport);
        userService.findById(transportDto.getOwnerId());
        transport.setUser(userService.findById(transportDto.getOwnerId()));
        return transportRepository.save(transport);
    }

    public void deleteTransport(long id) {
        if(transportRepository.getTransportById(id).isEmpty())
            throw new NotFoundException("Транспорт не найден");
        Transport transport = transportRepository.getTransportById(id).get();
        transportRepository.delete(transport);
    }
}
