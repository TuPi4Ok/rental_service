package ivan.prh.app.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
