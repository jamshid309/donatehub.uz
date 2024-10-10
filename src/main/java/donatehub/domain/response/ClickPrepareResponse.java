package donatehub.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClickPrepareResponse {
    @JsonProperty("click_trans_id")
    private Long clickTransId;

    @JsonProperty("merchant_trans_id")
    private Integer merchantTransId;

    @JsonProperty("merchant_prepare_id")
    private Integer merchantPrepareId;

    private Integer error;

    @JsonProperty("error_note")
    private String errorNote;
}
