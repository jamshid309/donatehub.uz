package donatehub.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClickPrepareRequest {
    @JsonProperty("click_trans_id")
    private Long clickTransId;

    @JsonProperty("service_id")
    private Integer serviceId;

    @JsonProperty("click_paydoc_id")
    private Long clickPaydocId;

    @JsonProperty("merchant_trans_id")
    private String merchantTransId;

    private Float amount;

    private Integer action;

    private Integer error;

    @JsonProperty("error_note")
    private String errorNote;

    @JsonProperty("sign_time")
    private String signTime;

    @JsonProperty("sign_string")
    private String signString;
}
