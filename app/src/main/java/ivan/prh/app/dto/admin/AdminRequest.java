package ivan.prh.app.dto.admin;

import lombok.Data;

@Data
public class AdminRequest {
    private String username;
    private String password;
    private boolean isAdmin;
    private double balance;
}
