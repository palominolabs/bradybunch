web: java $JAVA_OPTS -Ddw.server.connector.port=$PORT -jar target/bradybunch-1.0-SNAPSHOT.jar server config-heroku.yml
migratedb: java $JAVA_OPTS -jar target/bradybunch-1.0-SNAPSHOT.jar db migrate config-heroku.yml
