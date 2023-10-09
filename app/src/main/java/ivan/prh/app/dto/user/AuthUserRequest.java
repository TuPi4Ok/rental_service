package ivan.prh.app.dto.user;

import lombok.Data;

@Data
public class AuthUserRequest {
    private String username;
    private String password;
}
