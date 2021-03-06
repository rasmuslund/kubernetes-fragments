package dk.ralu.k8s.fragments.k8s.fragments.profile;

import java.time.Instant;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!TEST")
public class TimeProviderForProd implements TimeProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
