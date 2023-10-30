package ivan.prh.app.dto.transport;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class TransportDto {
    @NotNull(message = "Не может быть пустым")
    private boolean canBeRented;
    @NotBlank(message = "Не может быть пустым")
    @Pattern(regexp = "^(Car|Bike|Scooter)$", message = "Введен неверный тип транспорта")
    private String transportType;
    @NotBlank(message = "Не может быть пустым")
    private String model;
    @NotBlank(message = "Не может быть пустым")
    private String color;
    @NotBlank(message = "Не может быть пустым")
    private String identifier;

    private String description;
    @NotNull(message = "Не может быть пустым")
    private double latitude;
    @NotNull(message = "Не может быть пустым")
    private double longitude;

    private Double minutePrice;

    private Double dayPrice;

}
