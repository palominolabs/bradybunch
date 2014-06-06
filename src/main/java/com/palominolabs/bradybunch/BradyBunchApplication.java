package com.palominolabs.bradybunch;

import com.palominolabs.bradybunch.auth.UserAuthenticator;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.db.PersonDAO;
import com.palominolabs.bradybunch.resources.ProtectedResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.atmosphere.cpr.AtmosphereServlet;

public class BradyBunchApplication extends Application<BradyBunchConfiguration> {
    public static void main(String[] args) throws Exception {
        new BradyBunchApplication().run(args);
    }

    private final HibernateBundle<BradyBunchConfiguration> hibernateBundle =
            new HibernateBundle<BradyBunchConfiguration>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(BradyBunchConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "bradybunch";
    }

    @Override
    public void initialize(Bootstrap<BradyBunchConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<BradyBunchConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(BradyBunchConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(BradyBunchConfiguration configuration, Environment environment) throws ClassNotFoundException {
        final PersonDAO personDao = new PersonDAO(hibernateBundle.getSessionFactory());

        Person person = new Person();
        person.setEmail("drew@palominolabs.com");
        person.setPassword("foobar");
        personDao.create(person);

        environment.jersey().register(new BasicAuthProvider<Person>(new UserAuthenticator(personDao), "REALM"));
        environment.jersey().register(new ProtectedResource());

        initializeAtmosphere(environment);
    }

    void initializeAtmosphere(Environment environment) {
//        FilterBuilder fconfig = environment.addFilter(CrossOriginFilter.class, "/chat");
//        fconfig.setInitParam(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");

        AtmosphereServlet atmosphereServlet = new AtmosphereServlet();
        atmosphereServlet.framework().addInitParameter("com.sun.jersey.config.property.packages",
            "com.palominolabs.bradybunch.resources.atmosphere");
        atmosphereServlet.framework().addInitParameter("org.atmosphere.websocket.messageContentType",
            "application/json");
        environment.jersey().register(atmosphereServlet);
    }
}
