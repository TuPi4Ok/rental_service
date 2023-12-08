package ivan.prh.app.dto.user;

import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Role;
import ivan.prh.app.model.Transport;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
@Data
public class UserDto {
    private Long id;
    private String userName;
    private Collection<Role> roles;
    private double balance;
    private Collection<Transport> transports;
    private Collection<Rent> rents;
}
