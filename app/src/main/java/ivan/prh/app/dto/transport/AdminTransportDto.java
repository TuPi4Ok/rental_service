package ivan.prh.app.dto.transport;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdminTransportDto extends TransportDto {
    @NotNull(message = "Не может быть пустым")
    private long ownerId;
}
