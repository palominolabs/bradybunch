package com.palominolabs.bradybunch.cli;

import com.palominolabs.bradybunch.BradyBunchConfiguration;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.db.PersonDAO;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUser extends ConfiguredCommand<BradyBunchConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUser.class);

    private final PersonDAO personDao;

    public CreateUser() {
        super("createuser", "Create users for the specified email addresses");

        // TODO
        personDao = null;
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
        subparser.addArgument("emails").nargs("*");
    }

    @Override
    @UnitOfWork
    protected void run(Bootstrap<BradyBunchConfiguration> bootstrap, Namespace namespace, BradyBunchConfiguration configuration) throws Exception {
        for (String email : namespace.<String>getList("emails")) {
            Person person = new Person();
            person.setEmail(email);
            person.setPassword("pal3usrus");
            personDao.create(person);
        }
    }
}
