package uk.gov.pay.connector.exception;

import javax.ws.rs.WebApplicationException;

import static java.lang.String.format;
import static uk.gov.pay.connector.util.ResponseUtil.acceptedResponse;

public class OperationAlreadyInProgressRuntimeException extends WebApplicationException {
    public OperationAlreadyInProgressRuntimeException(String operationType, String chargeId) {
        super(acceptedResponse(format("%s for charge already in progress, %s", operationType, chargeId)));
    }
}
