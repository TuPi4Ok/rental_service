package ivan.prh.app.util;

import ivan.prh.app.dto.RentDto;
import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MapperUtils {

    public Transport transportDtoToTransport(TransportDto transportDto, Transport transport) {
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
        return transport;
    }

    public Rent rentDtoToRent(RentDto rentDto, Rent rent) {
        rent.setTimeStart(LocalDateTime.parse(rentDto.getTimeStart()));
        rent.setTimeEnd(LocalDateTime.parse(rentDto.getTimeEnd()));
        rent.setPriceOfUnit(rentDto.getPriceOfUnit());
        rent.setPriceType(rentDto.getPriceType());
        rent.setFinalPrice(rent.getFinalPrice());
        return rent;
    }
}
