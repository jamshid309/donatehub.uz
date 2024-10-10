package donatehub.domain.projections;

import java.time.LocalDate;

public interface UserStatisticByGraphic {
    LocalDate getDate();
    Integer getWithdraws();
    Integer getDonations();
}
