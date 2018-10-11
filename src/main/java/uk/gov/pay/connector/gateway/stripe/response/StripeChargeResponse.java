package uk.gov.pay.connector.gateway.stripe.response;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import uk.gov.pay.connector.gateway.model.Auth3dsDetails;
import uk.gov.pay.connector.gateway.model.GatewayParamsFor3ds;
import uk.gov.pay.connector.gateway.model.response.BaseAuthoriseResponse;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class StripeChargeResponse implements BaseAuthoriseResponse {

    private static final String AUTHORISED = "5";
    private static final String REJECTED = "2";

    private static final Map<Auth3dsDetails.Auth3dsResult, String> statusTo3dsResultMapping = ImmutableMap.of(
            Auth3dsDetails.Auth3dsResult.AUTHORISED, AUTHORISED,
            Auth3dsDetails.Auth3dsResult.DECLINED, REJECTED,
            Auth3dsDetails.Auth3dsResult.ERROR, "ERROR");

    private String id;
    private String status;
    private String captured;
    private String failure_code;
    private String failure_message;

    @Override
    public String getTransactionId() {
        return id;
    }

    @Override
    public AuthoriseStatus authoriseStatus() {
        if (AUTHORISED.equals(status)) {
            return AuthoriseStatus.AUTHORISED;
        }

        return AuthoriseStatus.ERROR;
    }

    @Override
    public Optional<? extends GatewayParamsFor3ds> getGatewayParamsFor3ds() {
        return Optional.empty();
    }


    private boolean hasError() {
        return authoriseStatus() == AuthoriseStatus.ERROR;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getCaptured() {
        return captured;
    }

    public String getFailure_code() {
        return failure_code;
    }

    public String getFailure_message() {
        return failure_message;
    }

    @Override
    public String getErrorCode() {
        if (hasError())
            return failure_code;
        return null;
    }

    @Override
    public String getErrorMessage() {
        if (hasError())
            return failure_message;
        return null;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Stripe authorisation response (", ")");
        if (StringUtils.isNotBlank(id)) {
            joiner.add("ID: " + id);
        }
        if (StringUtils.isNotBlank(status)) {
            joiner.add("STATUS: " + status);
        }
        if (StringUtils.isNotBlank(captured)) {
            joiner.add("CAPTURED: " + captured);
        }
        if (StringUtils.isNotBlank(failure_code)) {
            joiner.add("FAILURE_CODE: " + failure_code);
        }
        return joiner.toString();
    }
}
