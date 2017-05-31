package dk.ralu.springtutorial.restapi;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"my.greeting=TADA"}) // <== Overrides default value from src/main/resources/application.properties
public class ConfiguredMessageEndpointTest {

    @Autowired
    private ConfiguredMessageEndpoint configuredMessageEndpoint; // <== REST endpoints can be injected (but a bit crappy to do so...)

    @Test
    public void index() {
        Assert.assertEquals("TADA", configuredMessageEndpoint.index());
    }
}