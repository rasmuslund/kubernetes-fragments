package dk.ralu.springtutorial.profile;

import java.time.Instant;

public interface TimeProvider {

    Instant now();
}
