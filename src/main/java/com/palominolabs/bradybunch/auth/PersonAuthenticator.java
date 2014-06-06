package com.palominolabs.bradybunch.auth;

import com.google.common.base.Optional;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.db.PersonDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class PersonAuthenticator implements Authenticator<BasicCredentials, Person> {
    PersonDAO personDao;

    public PersonAuthenticator(PersonDAO personDao) {
        this.personDao = personDao;
    }

    @Override
    public Optional<Person> authenticate(BasicCredentials credentials) throws AuthenticationException {
        Optional<Person> optional = personDao.findByEmail(credentials.getUsername());
        if (optional.isPresent()) {
            Person person = optional.get();
            if (person.isCorrectPassword(credentials.getPassword())) {
                return Optional.of(person);
            }
        }

        return Optional.absent();
    }
}
