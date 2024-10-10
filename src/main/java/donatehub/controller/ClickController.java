package donatehub.controller;

import donatehub.domain.request.ClickCompleteRequest;
import donatehub.domain.request.ClickPrepareRequest;
import donatehub.domain.response.ClickCompleteResponse;
import donatehub.domain.response.ClickPrepareResponse;
import donatehub.service.payment.ClickService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/click")
public class ClickController {
    private final ClickService clickService;
    private final Logger log;

    @PostMapping("/prepare")
    public ClickPrepareResponse prepare(@RequestBody ClickPrepareRequest clickPrepareRequest) {
        log.info("Click to pay prepare request: {}", clickPrepareRequest);
        return clickService.prepare(clickPrepareRequest);
    }

    @PostMapping("/complete")
    public ClickCompleteResponse complete(@RequestBody ClickCompleteRequest clickCompleteRequest) {
        log.info("Click to pay complete request: {}", clickCompleteRequest);
        return clickService.complete(clickCompleteRequest);
    }
}
