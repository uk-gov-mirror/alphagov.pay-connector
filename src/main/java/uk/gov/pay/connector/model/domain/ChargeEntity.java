package uk.gov.pay.connector.model.domain;

import uk.gov.pay.connector.exception.InvalidStateTransitionException;
import uk.gov.pay.connector.model.api.ExternalChargeState;
import uk.gov.pay.connector.util.RandomIdGenerator;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static uk.gov.pay.connector.model.domain.ChargeStatus.CREATED;

@Entity
@Table(name = "charges")
@SequenceGenerator(name = "charges_charge_id_seq", sequenceName = "charges_charge_id_seq", allocationSize = 1)
@Access(AccessType.FIELD)
public class ChargeEntity extends AbstractEntity {

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "status")
    private String status;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Column(name = "return_url")
    private String returnUrl;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "gateway_account_id", updatable = false)
    private GatewayAccountEntity gatewayAccount;

    @OneToMany(mappedBy = "chargeEntity")
    private List<ChargeEventEntity> events = new ArrayList<>();

    @Column(name = "description")
    private String description;

    @Column(name = "reference")
    private String reference;

    @Column(name = "created_date")
    @Convert(converter = UTCDateTimeConverter.class)
    private ZonedDateTime createdDate;

    public ChargeEntity() {
        //for jpa
    }

    public ChargeEntity(Long amount, String returnUrl, String description, String reference, GatewayAccountEntity gatewayAccount, String email) {
        this(amount, CREATED, returnUrl, description, reference, gatewayAccount, email);
    }

    //for fixture
    ChargeEntity(Long amount, ChargeStatus status, String returnUrl, String description, String reference, GatewayAccountEntity gatewayAccount, String email) {
        this.amount = amount;
        this.status = status.getValue();
        this.returnUrl = returnUrl;
        this.description = description;
        this.reference = reference;
        this.gatewayAccount = gatewayAccount;
        this.createdDate = ZonedDateTime.now(ZoneId.of("UTC"));
        this.externalId = RandomIdGenerator.newId();
        this.email = email;
    }

    public Long getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public GatewayAccountEntity getGatewayAccount() {
        return gatewayAccount;
    }

    public String getDescription() {
        return description;
    }

    public String getReference() {
        return reference;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setStatus(ChargeStatus status) throws InvalidStateTransitionException {
        if (StateTransitions.transitionTo(ChargeStatus.fromString(this.status), status)) {
            this.status = status.getValue();
        } else {
            throw new InvalidStateTransitionException(this.status, status.getValue());
        }
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public void setGatewayAccount(GatewayAccountEntity gatewayAccount) {
        this.gatewayAccount = gatewayAccount;
    }

    public boolean isAssociatedTo(Long accountId) {
        return this.getGatewayAccount().getId().equals(accountId);
    }

    public List<ChargeEventEntity> getEvents() {
        return events;
    }

    public String getExternalId() {
        return externalId;
    }

    public boolean hasStatus(ChargeStatus... status) {
        return Arrays.stream(status).anyMatch(s -> equalsIgnoreCase(s.getValue(), getStatus()));
    }

    public boolean hasExternalStatus(ExternalChargeState... state) {
        return Arrays.stream(state).anyMatch(s -> ChargeStatus.fromString(getStatus()).toExternal().equals(s));
    }

    public String getEmail() {
        return email;
    }
}
