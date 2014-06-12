package com.palominolabs.bradybunch.auth;

import com.google.common.base.Optional;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.db.PersonDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.apache.commons.lang.WordUtils;

import java.util.Arrays;

public class PersonAuthenticator implements Authenticator<BasicCredentials, Person> {
    PersonDAO personDao;

    public PersonAuthenticator(PersonDAO personDao) {
        this.personDao = personDao;
    }

    @Override
    public Optional<Person> authenticate(BasicCredentials credentials) throws AuthenticationException {
        for (String name : Arrays
            .asList("ace", "andrew", "drew", "hayden", "manuel", "marshall", "ron", "ryan", "tyler")) {
            Person person = new Person();
            person.setName(WordUtils.capitalize(name));
            person.setEmail(name + "@palominolabs.com");
            person.setPassword("pal3usrus");
            personDao.create(person);
        }

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
