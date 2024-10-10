package donatehub.domain.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import donatehub.domain.constants.PaymentMethod;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DonationCreateRequest {
    @NotEmpty(message = "Ism bo'sh bo'lmasligi kerak")
    private String donaterName;

    @NotEmpty(message = "Xabar bo'sh bo'lmasligi kerak")
    private String message;

    @Min(value = 100, message = "Minimal donat summasi 100 so'm")
    @Max(value = 100_000_001, message = "Maksimal donat summasi 100_000_001 so'm")
    private Float amount;
}
