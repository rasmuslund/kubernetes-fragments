package dk.ralu.springtutorial.restapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FixedMessageEndpoint {

    @RequestMapping("/messages/fixed")
    public String index() {
        return "Hello World";
    }
}
