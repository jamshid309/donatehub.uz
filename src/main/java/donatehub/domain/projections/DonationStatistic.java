package donatehub.domain.projections;

public interface DonationStatistic {
    Long getTotalCount();
    Float getTotalAmount();
    Long getDailyCount();
    Float getDailyAmount();
}