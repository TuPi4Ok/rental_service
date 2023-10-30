package ivan.prh.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean canBeRented;
    private String transportType;
    private String model;
    private String color;
    private String identifier;
    @Nullable
    private String description;
    private double latitude;
    private double longitude;
    @Nullable
    private Double minutePrice;
    @Nullable
    private Double dayPrice;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "transport")
    private Collection<Rent> rents;
}
