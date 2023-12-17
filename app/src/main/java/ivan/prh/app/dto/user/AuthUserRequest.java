package ivan.prh.app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserRequest {
    @NotNull(message = "Не может быть пустым")
    private String username;
    @NotNull(message = "Не может быть пустым")
    private String password;
}
