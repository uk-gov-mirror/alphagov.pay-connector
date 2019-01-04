package uk.gov.pay.connector.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.util.Duration;

public class CustomJerseyClientConfiguration extends Configuration {
    private Duration readTimeout;

    @JsonProperty
    public Duration getReadTimeout() {
        return this.readTimeout;
    }
}
