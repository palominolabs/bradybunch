package com.palominolabs.bradybunch;

import com.palominolabs.bradybunch.auth.PersonAuthenticator;
import com.palominolabs.bradybunch.cli.CreateUser;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.core.Template;
import com.palominolabs.bradybunch.db.PersonDAO;
import com.palominolabs.bradybunch.health.TemplateHealthCheck;
import com.palominolabs.bradybunch.resources.HelloWorldResource;
import com.palominolabs.bradybunch.resources.ProtectedResource;
import com.palominolabs.bradybunch.resources.ViewResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.util.Arrays;

public class BradyBunchApplication extends Application<BradyBunchConfiguration> {
    public static void main(String[] args) throws Exception {
        new BradyBunchApplication().run(args);
    }

    private final HibernateBundle<BradyBunchConfiguration> hibernateBundle = new HibernateBundle<BradyBunchConfiguration>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(BradyBunchConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<BradyBunchConfiguration> bootstrap) {
        bootstrap.addCommand(new CreateUser());

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
        final Template template = configuration.buildTemplate();

        environment.healthChecks().register("template", new TemplateHealthCheck(template));

        environment.jersey().register(new BasicAuthProvider<>(new PersonAuthenticator(personDao), "SUPER SECRET STUFF"));

        environment.jersey().register(new HelloWorldResource(template));
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new ProtectedResource());
    }
}
