package com.palominolabs.bradybunch;

import com.palominolabs.bradybunch.auth.ExampleAuthenticator;
import com.palominolabs.bradybunch.cli.RenderCommand;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.core.Template;
import com.palominolabs.bradybunch.core.User;
import com.palominolabs.bradybunch.db.PersonDAO;
import com.palominolabs.bradybunch.health.TemplateHealthCheck;
import com.palominolabs.bradybunch.resources.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.FilterBuilder;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;
import com.yammer.dropwizard.views.ViewBundle;
import org.atmosphere.cpr.AtmosphereServlet;
import org.eclipse.jetty.servlets.CrossOriginFilter;

public class BradyBunchService extends Service<BradyBunchConfiguration> {
    public static void main(String[] args) throws Exception {
        new BradyBunchService().run(args);
    }

    private final HibernateBundle<BradyBunchConfiguration> hibernateBundle =
            new HibernateBundle<BradyBunchConfiguration>(Person.class) {
                @Override
                public DatabaseConfiguration getDatabaseConfiguration(BradyBunchConfiguration configuration) {
                    return configuration.getDatabaseConfiguration();
                }
            };

    @Override
    public void initialize(Bootstrap<BradyBunchConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<BradyBunchConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(BradyBunchConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle());
    }

    void initializeAtmosphere(BradyBunchConfiguration configuration, Environment environment) {
        FilterBuilder fconfig = environment.addFilter(CrossOriginFilter.class, "/chat");
        fconfig.setInitParam(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");

        AtmosphereServlet atmosphereServlet = new AtmosphereServlet();
        atmosphereServlet.framework().addInitParameter("com.sun.jersey.config.property.packages", "com.palominolabs.bradybunch.resources.atmosphere");
        atmosphereServlet.framework().addInitParameter("org.atmosphere.websocket.messageContentType", "application/json");
        atmosphereServlet.framework().addInitParameter("org.atmosphere.cpr.broadcastFilterClasses", "com.palominolabs.bradybunch.filters.BadWordFilter");
        environment.addServlet(atmosphereServlet, "/chat/*");
    }

    @Override
    public void run(BradyBunchConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {

        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());

        environment.addProvider(new BasicAuthProvider<User>(new ExampleAuthenticator(), "SUPER SECRET STUFF"));

        final Template template = configuration.buildTemplate();

        environment.addHealthCheck(new TemplateHealthCheck(template));
        environment.addResource(new RootResource());
        environment.addResource(new ProtectedResource());
        environment.addResource(new PeopleResource(dao));
        environment.addResource(new PersonResource(dao));

        initializeAtmosphere(configuration, environment);
    }
}
