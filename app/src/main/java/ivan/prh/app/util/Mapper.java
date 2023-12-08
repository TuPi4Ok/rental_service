package ivan.prh.app.util;

import ivan.prh.app.dto.rent.RentDtoRequest;
import ivan.prh.app.dto.transport.TransportDto;
import ivan.prh.app.dto.user.UserDto;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class Mapper {
    public abstract UserDto map(User model);
    public abstract Transport map(TransportDto model);
    public abstract Rent map(RentDtoRequest model);

    public abstract Transport update(TransportDto dto, @MappingTarget Transport model);
    public abstract Rent update(RentDtoRequest dto, @MappingTarget Rent model);
}
