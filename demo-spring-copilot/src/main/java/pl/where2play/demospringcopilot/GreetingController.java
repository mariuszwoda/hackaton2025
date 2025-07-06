package pl.where2play.demospringcopilot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.where2play.demospringcopilot.model.GreetingResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/greetings")
class GreetingController {
    private final GreetingService service;

    GreetingController(GreetingService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<GreetingResponse>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    ResponseEntity<GreetingResponse> create(@RequestBody GreetingCreateRequest request) {
        GreetingResponse response = service.create(request.message());
        return ResponseEntity.status(201).body(response);
    }
}

record GreetingCreateRequest(String message) {}

