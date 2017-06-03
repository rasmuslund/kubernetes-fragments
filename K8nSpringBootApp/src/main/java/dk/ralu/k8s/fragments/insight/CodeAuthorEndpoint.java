package dk.ralu.k8s.fragments.insight;

import dk.ralu.k8s.fragments.jpa.Person;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Service;

@Service
public class CodeAuthorEndpoint implements Endpoint<Person> { // <== Let a bean implement Endpoint to expose a new insight endpoint

    @Override
    public String getId() {
        return "author"; // <== The URL path - e.g. access on: http://localhost:8080/author
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return false;
    }

    @Override
    public Person invoke() {
        Person person = new Person();
        person.setFirstName("Rasmus");
        person.setLastName("Lund");
        return person;
    }
}
