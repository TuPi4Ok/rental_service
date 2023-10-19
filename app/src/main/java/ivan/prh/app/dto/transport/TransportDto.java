package ivan.prh.app.dto.transport;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class TransportDto {
    private boolean canBeRented;
    private String transportType;
    private String model;
    private String color;
    private String identifier;
    @Nullable
    private String description;
    private double latitude;
    private double longitude;
    @Nullable
    private Double minutePrice;
    @Nullable
    private Double dayPrice;
}
