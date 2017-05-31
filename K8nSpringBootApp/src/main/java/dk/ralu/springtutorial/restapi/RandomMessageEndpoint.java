package dk.ralu.springtutorial.restapi;

import dk.ralu.springtutorial.beans.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RandomMessageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomMessageEndpoint.class);

    @Autowired
    private MessageGenerator messageGenerator;

    @RequestMapping("/messages/random")
    public String index() {
        String generatedMessage = messageGenerator.generateMessage();
        LOGGER.info("Generated message {}", generatedMessage);
        return generatedMessage;
    }
}
