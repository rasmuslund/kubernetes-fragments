package dk.ralu.springtutorial.restapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfiguredMessageEndpoint {

    @Value("${my.greeting}")
    private String greetingString;

    @RequestMapping("/messages/configured")
    public String index() {
        return greetingString;
    }
}
