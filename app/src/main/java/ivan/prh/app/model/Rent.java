package ivan.prh.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class Rent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Transport transport;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private double priceOfUnit;
    private String priceType;
    private Double finalPrice;


    public void setPrice() {
        if(transport.getMinutePrice() == null || transport.getDayPrice() == null)
            return;
        if(priceType.equals("Minutes"))
            priceOfUnit = transport.getMinutePrice();
        if(priceType.equals("Days"))
            priceOfUnit = transport.getDayPrice();
    }
}
