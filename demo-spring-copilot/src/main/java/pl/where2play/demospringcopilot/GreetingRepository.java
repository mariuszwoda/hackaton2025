package pl.where2play.demospringcopilot;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.where2play.demospringcopilot.model.Greeting;

interface GreetingRepository extends JpaRepository<Greeting, Long> {
}

