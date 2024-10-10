package donatehub.domain.entities;

import donatehub.domain.embeddables.DonationPayment;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "donations_table")

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DonationEntity extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 111L;

    @ManyToOne
    private UserEntity streamer;

    @Column(name = "donater_name")
    private String donaterName;

    private String message;

    @Embedded
    private DonationPayment payment;
}