package ivan.prh.app.dto.transport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AdminTransportDto extends TransportDto {
    @NotNull(message = "Не может быть пустым")
    private long ownerId;
}
