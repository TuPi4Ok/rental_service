package ivan.prh.app.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthUserRequest {
    @NotNull(message = "Не может быть пустым")
    private String username;
    @NotNull(message = "Не может быть пустым")
    private String password;
}
