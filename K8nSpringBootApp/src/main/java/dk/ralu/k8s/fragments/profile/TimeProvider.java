package dk.ralu.k8s.fragments.profile;

import java.time.Instant;

public interface TimeProvider {

    Instant now();
}
