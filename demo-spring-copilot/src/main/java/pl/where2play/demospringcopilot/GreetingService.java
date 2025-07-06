package pl.where2play.demospringcopilot;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.where2play.demospringcopilot.model.Greeting;
import pl.where2play.demospringcopilot.model.GreetingResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
class GreetingService {
    private final GreetingRepository repository;

    GreetingService(GreetingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    List<GreetingResponse> findAll() {
        return repository.findAll().stream()
                .map(g -> new GreetingResponse(g.getId(), g.getMessage()))
                .collect(Collectors.toList());
    }

    @Transactional
    GreetingResponse create(String message) {
        Greeting saved = repository.save(new Greeting(message));
        return new GreetingResponse(saved.getId(), saved.getMessage());
    }
}

