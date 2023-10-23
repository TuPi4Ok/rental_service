package ivan.prh.app.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthUserRequest {
    @NotNull(message = "Не может быть пустым")
    private String username;
    @NotNull(message = "Не может быть пустым")
    private String password;
}
