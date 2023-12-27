package ivan.prh.app.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
