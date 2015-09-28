package uk.gov.pay.connector.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ConnectorConfiguration extends Configuration {

    @Valid
    @NotNull
    private WorldpayConfig worldpayConfig = new WorldpayConfig();

    @Valid
    @NotNull
    private SmartpayConfig smartpayConfig = new SmartpayConfig();

    @Valid
    @NotNull
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @Valid
    @NotNull
    private LinksConfig links = new LinksConfig();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public LinksConfig getLinks() {
        return links;
    }

    @JsonProperty("worldpay")
    public WorldpayConfig getWorldpayConfig() {
        return worldpayConfig;
    }

    @JsonProperty("smartpay")
    public SmartpayConfig getSmartpayConfig() {
        return smartpayConfig;
    }
}
