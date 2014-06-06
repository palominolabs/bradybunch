package com.palominolabs.bradybunch.db;

import com.palominolabs.bradybunch.core.Person;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

public class PersonDAO extends AbstractDAO<Person> {
    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findByEmail(String email) {
        Query query = namedQuery(Person.class.getCanonicalName() + ".findByEmail");
        query.setString("email", email);
        return Optional.fromNullable(uniqueResult(query));
    }

    public Person create(Person person) {
        return persist(person);
    }
}
