package donatehub.domain.projections;

/**
 * Projection for {@link donatehub.domain.entities.WithdrawPayment}
 */
public interface WithdrawPaymentInfo {
    String getCardNumber();

    Float getAmount();

    Float getCommission();
}