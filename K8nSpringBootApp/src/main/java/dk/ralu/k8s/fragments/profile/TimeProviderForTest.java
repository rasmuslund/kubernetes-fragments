package dk.ralu.k8s.fragments.profile;

import java.time.Instant;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("TEST")
public class TimeProviderForTest implements TimeProvider {

    @Override
    public Instant now() {
        return Instant.parse("2007-12-03T10:15:30.00Z");
    }
}
