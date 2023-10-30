package ivan.prh.app.dto.rent;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class RentDtoRequest {
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
