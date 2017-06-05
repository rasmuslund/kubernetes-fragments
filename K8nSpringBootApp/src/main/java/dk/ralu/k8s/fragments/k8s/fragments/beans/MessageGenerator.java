package dk.ralu.k8s.fragments.k8s.fragments.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageGenerator {

    @Autowired
    private RandomWordGenerator randomWordGenerator;

    public String generateMessage() {
        return randomWordGenerator.generateGreetingWord() + " " + randomWordGenerator.generateLocationWord();
    }
}
