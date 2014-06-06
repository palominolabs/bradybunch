package com.palominolabs.bradybunch.db;

import com.palominolabs.bradybunch.core.Person;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class PersonDAO extends AbstractDAO<Person> {
    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findByEmail(String email) {
        return Optional.fromNullable(get(email));
    }

    public Person create(Person person) {
        return persist(person);
    }
}
