package com.palominolabs.bradybunch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palominolabs.bradybunch.auth.PersonAuthenticator;
import com.palominolabs.bradybunch.cli.CreateUser;
import com.palominolabs.bradybunch.core.Person;
import com.palominolabs.bradybunch.db.PersonDAO;
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
import org.atmosphere.cpr.AtmosphereServlet;

import javax.servlet.ServletRegistration;
import javax.ws.rs.core.MediaType;
import java.util.Properties;

public class BradyBunchApplication extends Application<BradyBunchConfiguration> {
    public static void main(String[] args) throws Exception {
//        Properties properties = System.getProperties();
//        properties.setProperty("com.sun.jersey.core.util.ReaderWriter.BufferSize", "131072");
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
        final ObjectMapper objectMapper = new ObjectMapper();

        environment.jersey().register(new BasicAuthProvider<>(new PersonAuthenticator(personDao), "SUPER SECRET STUFF"));

        environment.jersey().register(new ViewResource());
        environment.jersey().register(new ProtectedResource(personDao, objectMapper));

        initializeAtmosphere(configuration, environment);
    }

    private void initializeAtmosphere(BradyBunchConfiguration configuration, Environment environment) {
        AtmosphereServlet atmosphereServlet = new AtmosphereServlet();
        atmosphereServlet.framework().addInitParameter("com.sun.jersey.config.property.packages", "com.palominolabs.bradybunch.resources.atmosphere");
        atmosphereServlet.framework().addInitParameter("org.atmosphere.websocket.messageContentType", MediaType.APPLICATION_JSON);
//        atmosphereServlet.framework().addInitParameter("org.atmosphere.websocket.bufferSize", "131072");
//        atmosphereServlet.framework().addInitParameter("org.atmosphere.websocket.webSocketBufferingMaxSize ", "131072");
//        atmosphereServlet.framework().addInitParameter("org.atmosphere.websocket.maxTextMessageSize", "131072");
//        atmosphereServlet.framework().addInitParameter("org.atmosphere.websocket.maxBinaryBufferSize", "131072");
        ServletRegistration.Dynamic dynamic = environment.servlets().addServlet("/chat/*", atmosphereServlet);
        dynamic.setAsyncSupported(true);
        dynamic.addMapping("/chat/*");
    }
}
