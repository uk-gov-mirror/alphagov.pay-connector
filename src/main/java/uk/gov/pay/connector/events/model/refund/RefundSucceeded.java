package uk.gov.pay.connector.events.model.refund;

import uk.gov.pay.connector.events.eventdetails.refund.RefundEventWithReferenceDetails;

import java.time.ZonedDateTime;

public class RefundSucceeded extends RefundEvent {

    public RefundSucceeded(String resourceExternalId, String parentResourceExternalId,
                           RefundEventWithReferenceDetails referenceDetails, ZonedDateTime timestamp) {
        super(resourceExternalId, parentResourceExternalId, referenceDetails, timestamp);
    }
}
