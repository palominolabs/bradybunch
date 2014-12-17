Show live views of your team in a Brady Bunch style picture window.  A Dropwizard based application that runs on Heroku.

# Development

Build the application with Maven:

    mvn package

Create a MySQL database & user corresponding to the information in
config-dev.yml, then initialize the DB with:

    java -jar target/bradybunch-1.0-SNAPSHOT.jar db migrate config-dev.yml

With the DB setup, you can run the development server:

    java -jar target/bradybunch-1.0-SNAPSHOT.jar server config-dev.yml

The server listens on 0.0.0.0:8080.

# Users

When I wrote this, I couldn't figure out how the Dropwizard CLI works and I
still don't know how to write a CLI command, so create users by putting
something like this into the PersonAuthenticator.authenticate() method:

    for (String name : Arrays
        .asList("jimmy", "johnny", "bobby", "sue")) {
        Person person = new Person();
        person.setName(WordUtils.capitalize(name));
        person.setEmail(name + "@palominolabs.com");
        person.setPassword("gh0stbusters");
        personDao.create(person);
    }

Re-`mvn package`, run, and the next time someone trys to login the app will create all
of those users.  Yay security!
