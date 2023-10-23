package ivan.prh.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RentDto {
    @NotNull(message = "Не может быть пустым")
    private long transportId;
    @NotNull(message = "Не может быть пустым")
    private long userId;
    @NotBlank(message = "Не может быть пустым")
    private String timeStart;
    private String timeEnd;
    @NotNull(message = "Не может быть пустым")
    private double priceOfUnit;
    @Pattern(regexp = "^(Minutes|Days)$", message = "Введен неверный тип аренды")
    @NotBlank(message = "Не может быть пустым")
    private String priceType;
    private double finalPrice;
}
