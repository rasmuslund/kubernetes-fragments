package dk.ralu.k8s.fragments.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Spring Data automatically creates an implementation of this interface at startup
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Spring Data can implement this method, as the method follows certain naming rules (defines by Spring Data)
    List<Person> findByLastName(String lastName);

    // Spring Data implements this method by using the @Query annotation (the syntax is ordinary JPA query syntax)
    @Query("SELECT concat(p.firstName, ' ', p.lastName) FROM Person p WHERE p.id = :id")
    String findFullNameById(@Param("id") Long id);
}
