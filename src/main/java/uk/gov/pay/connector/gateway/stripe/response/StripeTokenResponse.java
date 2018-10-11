package uk.gov.pay.connector.gateway.stripe.response;


import uk.gov.pay.connector.gateway.model.GatewayParamsFor3ds;
import uk.gov.pay.connector.gateway.model.response.BaseAuthoriseResponse;

import java.util.Optional;

public class StripeTokenResponse implements BaseAuthoriseResponse {

    private String status;

    private String id;

    @Override
    public String getTransactionId() {
        return id;
    }

    @Override
    public AuthoriseStatus authoriseStatus() {
        return null;
    }

    @Override
    public Optional<? extends GatewayParamsFor3ds> getGatewayParamsFor3ds() {
        return Optional.empty();
    }

    @Override
    public String getErrorCode() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
