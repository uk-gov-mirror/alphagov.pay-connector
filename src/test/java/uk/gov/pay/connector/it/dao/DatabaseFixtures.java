package uk.gov.pay.connector.it.dao;

import org.apache.commons.lang3.RandomUtils;
import uk.gov.pay.connector.model.domain.CardTypeEntity.Type;
import uk.gov.pay.connector.model.domain.ChargeStatus;
import uk.gov.pay.connector.model.domain.GatewayAccountEntity;
import uk.gov.pay.connector.model.domain.RefundStatus;
import uk.gov.pay.connector.util.DatabaseTestHelper;
import uk.gov.pay.connector.util.RandomIdGenerator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static uk.gov.pay.connector.model.domain.GatewayAccountEntity.Type.TEST;
import static uk.gov.pay.connector.model.domain.RefundStatus.CREATED;

public class DatabaseFixtures {

    private DatabaseTestHelper databaseTestHelper;

    public DatabaseFixtures(DatabaseTestHelper databaseTestHelper) {
        this.databaseTestHelper = databaseTestHelper;
    }

    public static DatabaseFixtures withDatabaseTestHelper(DatabaseTestHelper databaseTestHelper) {
        return new DatabaseFixtures(databaseTestHelper);
    }

    public TestAccount aTestAccount() {
        return new TestAccount();
    }

    public TestCharge aTestCharge() {
        return new TestCharge();
    }

    public TestChargeEvent aTestChargeEvent() {
        return new TestChargeEvent();
    }

    public TestRefundHistory aTestRefundHistory(TestRefund refund) {
        return new TestRefundHistory(refund);
    }

    public TestToken aTestToken() {
        return new TestToken();
    }

    public TestRefund aTestRefund() {
        return new TestRefund();
    }

    public TestCardDetails aTestCardDetails() {
        return new TestCardDetails();
    }

    public TestCardType aMastercardCreditCardType() {
        return new TestCardType().withLabel("MasterCard").withType(Type.CREDIT).withBrand("mastercard");
    }

    public TestCardType aMastercardDebitCardType() {
        return new TestCardType().withLabel("MasterCard").withType(Type.DEBIT).withBrand("mastercard");
    }

    public TestCardType aVisaCreditCardType() {
        return new TestCardType().withLabel("Visa").withType(Type.CREDIT).withBrand("visa");
    }

    public TestCardType aVisaDebitCardType() {
        return new TestCardType().withLabel("Visa").withType(Type.DEBIT).withBrand("visa");
    }

    public TestEmailNotification anEmailNotification() {
        return new TestEmailNotification();
    }

    public TestCardDetails validTestCardDetails() {
        return new TestCardDetails();
    }

    public TestCardType aMaestroDebitCardType() {
        return new TestCardType().withLabel("Maestro").withType(Type.DEBIT).withBrand("maestro").withRequires3ds(true);
    }

    public TestSuccessfulChargeEvent aSuccessfulChargeEvent() {
        return new TestSuccessfulChargeEvent();
    }

    public TestRefundedEvent aRefundedEvent() {
        return new TestRefundedEvent();
    }

    public class TestRefundHistory {

        private String externalId;
        private long id;
        private Long chargeId;
        private long amount;
        private ZonedDateTime createdDate;
        private String userExternalId;

        TestRefundHistory(TestRefund testRefund) {
            this.id = testRefund.getId();
            this.chargeId = testRefund.getTestCharge().getChargeId();
            this.externalId = testRefund.getExternalRefundId();
            this.amount = testRefund.getAmount();
            this.createdDate = testRefund.getCreatedDate();
            this.userExternalId = testRefund.getSubmittedByUserExternalId();
        }

        public TestRefundHistory insert(RefundStatus status, ZonedDateTime historyStartDate, ZonedDateTime historyEndDate) {
            databaseTestHelper.addRefundHistory(id, externalId, "", amount, status.toString(), chargeId, createdDate, historyStartDate, historyEndDate, null);
            return this;
        }

        public TestRefundHistory insert(RefundStatus status, String reference, ZonedDateTime historyStartDate, ZonedDateTime historyEndDate) {
            databaseTestHelper.addRefundHistory(id, externalId, reference, amount, status.toString(), chargeId, createdDate, historyStartDate, historyEndDate, null);
            return this;
        }

        public TestRefundHistory insert(RefundStatus status, String reference, ZonedDateTime historyStartDate) {
            databaseTestHelper.addRefundHistory(id, externalId, reference, amount, status.toString(), chargeId, createdDate, historyStartDate, null);
            return this;
        }

        public TestRefundHistory insert(RefundStatus status, ZonedDateTime historyStartDate, ZonedDateTime historyEndDate, String submittedByExternalId) {
            databaseTestHelper.addRefundHistory(id, externalId, "", amount, status.toString(), chargeId, createdDate, historyStartDate, historyEndDate, submittedByExternalId);
            return this;
        }

        public TestRefundHistory insert(RefundStatus status, String reference, ZonedDateTime historyStartDate, ZonedDateTime historyEndDate, String submittedByExternalId) {
            databaseTestHelper.addRefundHistory(id, externalId, reference, amount, status.toString(), chargeId, createdDate, historyStartDate, historyEndDate, submittedByExternalId);
            return this;
        }

        public TestRefundHistory insert(RefundStatus status, String reference, ZonedDateTime historyStartDate, String submittedByExternalId) {
            databaseTestHelper.addRefundHistory(id, externalId, reference, amount, status.toString(), chargeId, createdDate, historyStartDate, submittedByExternalId);
            return this;
        }
    }

    public class TestChargeEvent {
        private long chargeId;
        private ChargeStatus chargeStatus;
        private ZonedDateTime updated = ZonedDateTime.now();

        public TestChargeEvent withTestCharge(TestCharge testCharge) {
            this.chargeId = testCharge.getChargeId();
            return this;
        }

        public TestChargeEvent withChargeId(long chargeId) {
            this.chargeId = chargeId;
            return this;
        }

        public TestChargeEvent withChargeStatus(ChargeStatus chargeStatus) {
            this.chargeStatus = chargeStatus;
            return this;
        }

        public TestChargeEvent withDate(ZonedDateTime updated) {
            this.updated = updated;
            return this;
        }

        public TestChargeEvent insert() {
            databaseTestHelper.addEvent(chargeId, chargeStatus.getValue(), updated);
            return this;
        }

        public long getChargeId() {
            return chargeId;
        }

        public ChargeStatus getChargeStatus() {
            return chargeStatus;
        }

        public ZonedDateTime getUpdated() {
            return updated;
        }
    }

    public class TestAddress {
        private String line1 = "line1";
        private String line2 = "line2";
        private String postcode = "postcode";
        private String city = "city";
        private String county = "county";
        private String country = "country";

        public String getLine1() {
            return line1;
        }

        public String getLine2() {
            return line2;
        }

        public String getPostcode() {
            return postcode;
        }

        public String getCity() {
            return city;
        }

        public String getCounty() {
            return county;
        }

        public String getCountry() {
            return country;
        }
    }

    public class TestCardDetails {
        private String lastDigitsCardNumber = "1234";
        private String cardHolderName = "Mr. Pay McPayment";
        private String expiryDate = "02/17";
        private TestAddress billingAddress = new TestAddress();
        private Long chargeId;
        private String cardBrand = "visa";

        public TestCardDetails withLastDigitsOfCardNumber(String lastDigitsCardNumber) {
            this.lastDigitsCardNumber = lastDigitsCardNumber;
            return this;
        }

        public TestCardDetails withCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
            return this;
        }

        public TestCardDetails withExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public TestCardDetails withBillingAddress(TestAddress billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        public TestCardDetails withCardBrand(String cardBrand) {
            this.cardBrand = cardBrand;
            return this;
        }

        public TestCardDetails withChargeId(Long chargeId) {
            this.chargeId = chargeId;
            return this;
        }

        public String getLastDigitsCardNumber() {
            return lastDigitsCardNumber;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public TestAddress getBillingAddress() {
            return billingAddress;
        }

        public TestCardDetails update() {
            databaseTestHelper.updateChargeCardDetails(chargeId, cardBrand, lastDigitsCardNumber, cardHolderName, expiryDate, billingAddress.getLine1(), billingAddress.getLine2(), billingAddress.getPostcode(), billingAddress.getCity(), billingAddress.getCounty(), billingAddress.getCountry());
            return this;
        }

        public String getCardBrand() {
            return cardBrand;
        }
    }

    public class TestAccount {
        long accountId = RandomUtils.nextLong(1, 99999);
        private String paymentProvider = "sandbox";
        private Map<String, String> credentials = new HashMap<>();
        private String serviceName = "service_name";
        private String description = "a description";
        private String analyticsId = "an analytics id";
        private GatewayAccountEntity.Type type = TEST;
        private List<TestCardType> cardTypes = new ArrayList<>();


        public long getAccountId() {
            return accountId;
        }

        public String getPaymentProvider() {
            return paymentProvider;
        }

        public Map<String, String> getCredentials() {
            return credentials;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getDescription() {
            return description;
        }

        public String getAnalyticsId() {
            return analyticsId;
        }

        public TestAccount withAccountId(long accountId) {
            this.accountId = accountId;
            return this;
        }

        public TestAccount withCardTypes(List<TestCardType> cardTypes) {
            this.cardTypes = cardTypes;
            return this;
        }

        public TestAccount withPaymentProvider(String provider) {
            this.paymentProvider = provider;
            return this;
        }

        public TestAccount withCredentials(Map<String, String> credentials) {
            this.credentials = credentials;
            return this;
        }

        public TestAccount withServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public TestAccount withType(GatewayAccountEntity.Type type) {
            this.type = type;
            return this;
        }

        public TestAccount insert() {
            databaseTestHelper.addGatewayAccount(
                    String.valueOf(accountId),
                    paymentProvider,
                    credentials,
                    serviceName,
                    type,
                    description,
                    analyticsId);
            for (TestCardType cardType : cardTypes) {
                databaseTestHelper.addAcceptedCardType(this.getAccountId(), cardType.getId());
            }
            return this;
        }
    }
    
    public class TestSuccessfulChargeEvent {

        private long gatewayAccountId;
        private ZonedDateTime createdDate;
        private String externalChargeId;
        private long amount;

        public TestSuccessfulChargeEvent withAccountGatewayId(long gatewayAccountId) {
            this.gatewayAccountId = gatewayAccountId;
            return this;
        }

        public TestSuccessfulChargeEvent withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public TestSuccessfulChargeEvent withExternalChargeId(String externalChargeId) {
            this.externalChargeId = externalChargeId;
            return this;
        }

        public TestSuccessfulChargeEvent insert() {
            databaseTestHelper.addSuccessfulChargeEvent(gatewayAccountId, createdDate, externalChargeId, amount);
            return this;
        }

        public TestSuccessfulChargeEvent withAmount(long amount) {
            this.amount = amount;
            return this;
        }
    }

    public class TestRefundedEvent {

        private long gatewayAccountId;
        private ZonedDateTime createdDate;
        private String externalChargeId;
        private long amount;

        public TestRefundedEvent withAccountGatewayId(long gatewayAccountId) {
            this.gatewayAccountId = gatewayAccountId;
            return this;
        }

        public TestRefundedEvent withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public TestRefundedEvent withExternalChargeId(String externalChargeId) {
            this.externalChargeId = externalChargeId;
            return this;
        }

        public TestRefundedEvent insert() {
            databaseTestHelper.addRefundedEvent(gatewayAccountId, createdDate, externalChargeId, amount);
            return this;
        }

        public TestRefundedEvent withAmount(long amount) {
            this.amount = amount;
            return this;
        }
    }

    static final AtomicLong initialChargeId = new AtomicLong(1L);
    
    public class TestCharge {
        Long chargeId = initialChargeId.getAndIncrement();
        private String description = "Test description";
        String email = "alice.111@mail.fake";
        String externalChargeId = RandomIdGenerator.newId();
        long amount = 101L;
        ChargeStatus chargeStatus = ChargeStatus.CREATED;
        String returnUrl = "http://service.com/success-page";
        String transactionId;
        String reference = "Test reference";

        ZonedDateTime createdDate = ZonedDateTime.now(ZoneId.of("UTC"));

        TestAccount testAccount;
        TestCardDetails cardDetails;

        public TestCharge withTestAccount(TestAccount account) {
            this.testAccount = account;
            return this;
        }

        public TestCharge withChargeId(long chargeId) {
            this.chargeId = chargeId;
            return this;
        }

        public TestCharge withExternalChargeId(String externalChargeId) {
            this.externalChargeId = externalChargeId;
            return this;
        }

        public TestCharge withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public TestCharge withEmail(String email) {
            this.email = email;
            return this;
        }

        public TestCharge withChargeStatus(ChargeStatus chargeStatus) {
            this.chargeStatus = chargeStatus;
            return this;
        }

        public TestCharge withTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TestCharge withAmount(long amount) {
            this.amount = amount;
            return this;
        }

        public TestCharge withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public TestCharge withCardDetails(TestCardDetails testCardDetails) {
            cardDetails = testCardDetails;
            return this;
        }

        public TestCharge withDescription(String description) {
            this.description = description;
            return this;
        }

        public TestCharge insert() {
            if (testAccount == null)
                throw new IllegalStateException("Test Account must be provided.");

            databaseTestHelper.addCharge(chargeId, externalChargeId, String.valueOf(testAccount.getAccountId()), amount, chargeStatus, returnUrl, transactionId, reference, description, createdDate, email);

            if (cardDetails != null) {
                cardDetails.update();
            }
            return this;
        }

        public Long getChargeId() {
            return chargeId;
        }

        public String getExternalChargeId() {
            return externalChargeId;
        }

        public long getAmount() {
            return amount;
        }

        public ChargeStatus getChargeStatus() {
            return chargeStatus;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getReference() {
            return reference;
        }

        public String getEmail() {
            return email;
        }

        public ZonedDateTime getCreatedDate() {
            return createdDate;
        }

        public String getDescription() {
            return description;
        }

        public TestAccount getTestAccount() {
            return testAccount;
        }
    }

    public class TestToken {
        TestCharge testCharge;
        String secureRedirectToken = "3c9fee80-977a-4da5-a003-4872a8cf95b6";

        public TestToken withTestToken(TestCharge testCharge) {
            this.testCharge = testCharge;
            return this;
        }

        public TestToken insert() {
            if (testCharge == null)
                throw new IllegalStateException("Test Charge must be provided.");
            databaseTestHelper.addToken(testCharge.getChargeId(), secureRedirectToken);
            return this;
        }

        public String getSecureRedirectToken() {
            return secureRedirectToken;
        }
    }

    static final AtomicLong refundId = new AtomicLong(1L);
    
    public class TestRefund {
        Long id = refundId.getAndIncrement();
        String externalRefundId = RandomIdGenerator.newId();
        String reference;
        long amount = 101L;
        RefundStatus status = CREATED;
        ZonedDateTime createdDate = ZonedDateTime.now(ZoneId.of("UTC"));
        TestCharge testCharge;
        String submittedByUserExternalId;

        public TestRefund withTestCharge(TestCharge charge) {
            this.testCharge = charge;
            return this;
        }

        public TestRefund withAmount(long amount) {
            this.amount = amount;
            return this;
        }

        public TestRefund withType(RefundStatus status) {
            this.status = status;
            return this;
        }

        public TestRefund withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public TestRefund withSubmittedBy(String submittedBy) {
            this.submittedByUserExternalId = submittedBy;
            return this;
        }

        public TestRefund withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public TestRefund withRefundStatus(RefundStatus status) {
            this.status = status;
            return this;
        }

        public TestRefund insert() {
            if (testCharge == null)
                throw new IllegalStateException("Test charge must be provided.");
            databaseTestHelper.addRefund(id, externalRefundId, reference, amount, status.toString(), testCharge.getChargeId(), createdDate, submittedByUserExternalId);
            return this;
        }


        public long getId() {
            return id;
        }

        public String getExternalRefundId() {
            return externalRefundId;
        }

        public String getReference() {
            return reference;
        }

        public long getAmount() {
            return amount;
        }

        public RefundStatus getStatus() {
            return status;
        }

        public ZonedDateTime getCreatedDate() {
            return createdDate;
        }

        public TestCharge getTestCharge() {
            return testCharge;
        }

        public String getSubmittedByUserExternalId() {
            return submittedByUserExternalId;
        }
    }

    public class TestEmailNotification {

        TestAccount testAccount;
        String template = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

        public TestEmailNotification withTestAccount(TestAccount testAccount) {
            this.testAccount = testAccount;
            return this;
        }

        public TestEmailNotification insert() {
            if (testAccount == null)
                throw new IllegalStateException("Test Account must be provided.");
            databaseTestHelper.addToken(testAccount.getAccountId(), template);
            return this;
        }

        public TestAccount getTestAccount() {
            return testAccount;
        }
    }

    public class TestCardType {
        UUID id = UUID.randomUUID();
        String label = "Mastercard";
        Type type = Type.CREDIT;
        String brand = "mastercard-c";
        boolean requires3DS;

        public TestCardType withCardTypeId(UUID id) {
            this.id = id;
            return this;
        }

        public TestCardType withLabel(String label) {
            this.label = label;
            return this;
        }

        public TestCardType withType(Type type) {
            this.type = type;
            return this;
        }

        public TestCardType withBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public TestCardType withRequires3ds(boolean requires3DS) {
            this.requires3DS = requires3DS;
            return this;
        }

        public TestCardType insert() {
            databaseTestHelper.addCardType(id, label, type.toString(), brand, requires3DS);
            return this;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getBrand() {
            return brand;
        }

        public boolean getRequires3DS() {
            return requires3DS;
        }
    }
}
