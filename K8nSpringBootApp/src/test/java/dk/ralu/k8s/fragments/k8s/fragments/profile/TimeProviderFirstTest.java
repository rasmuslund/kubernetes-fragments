package dk.ralu.k8s.fragments.k8s.fragments.profile;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TimeProviderFirstTest {

    @Autowired
    private TimeProvider timeProvider; // <== As we don't specify a profile, the bean annotated with @Profile("!TEST") will be used (! for NOT)

    @Test
    public void testProdVersionOfTimeProvider() {
        long nowAsEpocMilli = timeProvider.now().toEpochMilli();
        Assert.assertTrue(System.currentTimeMillis() - 10_000 < nowAsEpocMilli);
        Assert.assertTrue(System.currentTimeMillis() + 10_000 > nowAsEpocMilli);
    }
}
