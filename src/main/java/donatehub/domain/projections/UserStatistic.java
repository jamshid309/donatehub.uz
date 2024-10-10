package donatehub.domain.projections;

public interface UserStatistic {
    Long getTotalCount();
    Long getEnableTotalCount();
    Long getDailyTotalCount();
    Long getDailyEnableTotalCount();
}
