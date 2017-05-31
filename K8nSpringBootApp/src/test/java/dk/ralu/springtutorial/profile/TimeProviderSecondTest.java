package dk.ralu.springtutorial.profile;

import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"TEST"})  // We specify TEST as the (only) active profile
public class TimeProviderSecondTest {

    @Autowired
    private TimeProvider timeProvider; // <== As TEST is an active profile, the bean annotated with @Profile("TEST") will be used

    @Test
    public void testProdVersionOfTimeProvider() {
        Assert.assertEquals(Instant.parse("2007-12-03T10:15:30.00Z"), timeProvider.now());
    }
}