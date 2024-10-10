package donatehub.domain.projections;

import java.time.LocalDate;

public interface WithdrawStatistic {
    LocalDate getDate();
    Long getPendingCount();
    Long getCompletedCount();
    Long getCanceledCount();
    Long getCompletedAmount();
}