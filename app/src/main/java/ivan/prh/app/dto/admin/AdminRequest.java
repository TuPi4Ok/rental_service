package ivan.prh.app.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminRequest {
    @NotBlank(message = "Не может быть пустым")
    private String username;
    @NotBlank(message = "Не может быть пустым")
    private String password;
    @NotNull(message = "Не может быть пустым")
    private boolean isAdmin;
    @NotNull(message = "Не может быть пустым")
    private double balance;
}
