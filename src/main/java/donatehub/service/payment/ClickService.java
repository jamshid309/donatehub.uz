package donatehub.service.payment;

import donatehub.domain.request.ClickCompleteRequest;
import donatehub.domain.request.ClickPrepareRequest;
import donatehub.domain.response.ClickCompleteResponse;
import donatehub.domain.response.ClickPrepareResponse;
import donatehub.domain.response.PaymentResponse;

public interface ClickService {
    ClickPrepareResponse prepare(ClickPrepareRequest clickRequest);

    ClickCompleteResponse complete(ClickCompleteRequest clickRequest);

    PaymentResponse create(Float amount);
}
