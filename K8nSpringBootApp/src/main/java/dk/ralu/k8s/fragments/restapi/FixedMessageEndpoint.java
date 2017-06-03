package dk.ralu.k8s.fragments.restapi;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FixedMessageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedMessageEndpoint.class);
    public static final String ENDPOINT_URL = "/messages/fixed";

    @RequestMapping(ENDPOINT_URL)
    public String index() {
        LOGGER.info(ENDPOINT_URL + " was called at " + Instant.now());
        return "Hi World";
    }
}
