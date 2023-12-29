package ivan.prh.app.dto.transport;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
public class AdminTransportDto extends TransportDto {
    @NotNull(message = "Не может быть пустым")
    long ownerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AdminTransportDto that = (AdminTransportDto) o;
        return ownerId == that.ownerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ownerId);
    }
}
