package dk.ralu.springtutorial.restapi;

import dk.ralu.springtutorial.jpa.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // <== create an embedded web server that listens on a random port
public class JpaEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate; // <== Knows address and port of the embedded web server

    @Test
    public void useJpaToFindByLastName() throws Exception {
        Person[] persons = restTemplate.getForObject("/useJpaToFindByLastName/{lastName}", Person[].class, "Johnson");
        Assert.assertEquals(1, persons.length);
        Assert.assertEquals("Rod", persons[0].getFirstName());
    }
}
