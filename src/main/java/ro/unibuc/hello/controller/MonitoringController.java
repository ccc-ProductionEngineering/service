package ro.unibuc.hello.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/simulate-403")
    public ResponseEntity<String> simulateForbidden() {
        meterRegistry.counter("http_403_responses_total").increment();
        return ResponseEntity.status(403).body("Forbidden access");
    }
}
