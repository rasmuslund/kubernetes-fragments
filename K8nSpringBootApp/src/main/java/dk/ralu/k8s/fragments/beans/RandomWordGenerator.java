package dk.ralu.k8s.fragments.beans;

import org.springframework.stereotype.Service;

@Service
class RandomWordGenerator {

    String generateGreetingWord() {
        return Math.random() < 0.5 ? "Hi" : "Hello";
    }

    String generateLocationWord() {
        return Math.random() < 0.5 ? "World" : "Universe";
    }
}
