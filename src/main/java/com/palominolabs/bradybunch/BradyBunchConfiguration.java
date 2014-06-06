package com.palominolabs.bradybunch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palominolabs.bradybunch.core.Template;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BradyBunchConfiguration extends Configuration {
    @NotEmpty
    private String defaultName = "Stranger";

//    @Valid
//    @NotNull
//    @JsonProperty("database")
//    private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
