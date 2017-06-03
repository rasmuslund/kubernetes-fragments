package dk.ralu.k8s.fragments.restapi;

import dk.ralu.k8s.fragments.jpa.Person;
import dk.ralu.k8s.fragments.jpa.PersonRepository;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JpaEndpoint {

    @Autowired
    private PersonRepository personRepository;

    @RequestMapping("/useJpa")
    public Iterable<Person> useJpa() throws SQLException {
        return personRepository.findAll();
    }

    @RequestMapping("/useJpaToFindByLastName/{lastName}")
    public Iterable<Person> useJpaToFindByLastName(@PathVariable("lastName") String lastName) throws SQLException {
         return personRepository.findByLastName(lastName);
    }

    @RequestMapping("/useJpaToFindFullNameById/{id}")
    public String useJpaToFindFullNameById(@PathVariable("id") Long id) throws SQLException {
         return personRepository.findFullNameById(id);
    }
}
