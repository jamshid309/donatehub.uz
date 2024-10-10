package donatehub.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClickCompleteResponse {
    @JsonProperty("click_trans_id")
    private Long clickTransId;

    @JsonProperty("service_id")
    private Integer serviceId;

    @JsonProperty("merchant_confirm_id")
    private Integer merchantConfirmId;

    private Integer error;

    @JsonProperty("error_note")
    private String errorNote;
}
