package uk.gov.pay.connector.service.smartpay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import uk.gov.pay.connector.model.ChargeStatusRequest;
import uk.gov.pay.connector.model.domain.ChargeStatus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartpayNotification implements ChargeStatusRequest, Comparable<SmartpayNotification> {
    private final static Set<String> MANDATORY_FIELDS = ImmutableSet.of("eventCode", "eventDate", "pspReference");

    private final DateTime eventDate;
    private String eventCode;
    private String transactionId;
    private String success;

    private Optional<ChargeStatus> chargeStatus = Optional.empty();

    @JsonCreator
    public SmartpayNotification(@JsonProperty("NotificationRequestItem") Map<String, Object> notification){
        verify(notification, MANDATORY_FIELDS);

        this.eventCode = (String) notification.get("eventCode");
        this.transactionId = (String) notification.get("originalReference");
        if (this.transactionId == null) {
            this.transactionId = (String) notification.get("pspReference");
        }
        this.success = (String) notification.get("success");
        this.eventDate = DateTime.parse((String) notification.get("eventDate"));
    }

    private void verify(Map<String, Object> notification, Set<String> mandatoryFields) {
        Set<String> missingFields = Sets.difference(mandatoryFields, notification.keySet());
        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Missing fields: " + missingFields);
        }
    }

    @Override
    public Optional<ChargeStatus> getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(Optional<ChargeStatus> chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public String getEventCode() {
        return eventCode;
    }

    public Boolean isSuccessFull() {
        return "true".equals(success);
    }

    public DateTime getEventDate() {
        return eventDate;
    }

    @Override
    public int compareTo(SmartpayNotification other) {
        return this.eventDate.compareTo(other.eventDate);
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    public Pair<String, Boolean> getStatus() {
        return Pair.of(eventCode, isSuccessFull());
    }
}
