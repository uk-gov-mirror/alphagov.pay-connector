package uk.gov.pay.connector.it.contract;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.pay.connector.charge.model.domain.ChargeEntity;
import uk.gov.pay.connector.common.model.domain.Address;
import uk.gov.pay.connector.gateway.GatewayClient;
import uk.gov.pay.connector.gateway.GatewayOperation;
import uk.gov.pay.connector.gateway.GatewayOperationClientBuilder;
import uk.gov.pay.connector.gateway.PaymentProvider;
import uk.gov.pay.connector.gateway.model.AuthCardDetails;
import uk.gov.pay.connector.gateway.model.request.AuthorisationGatewayRequest;
import uk.gov.pay.connector.gateway.model.response.GatewayResponse;
import uk.gov.pay.connector.gateway.util.DefaultExternalRefundAvailabilityCalculator;
import uk.gov.pay.connector.gatewayaccount.model.GatewayAccountEntity;
import uk.gov.pay.connector.gateway.stripe.StripePaymentProvider;
import uk.gov.pay.connector.gateway.stripe.response.StripeChargeResponse;
import uk.gov.pay.connector.util.TestClientFactory;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.pay.connector.gatewayaccount.model.GatewayAccountEntity.Type.TEST;
import static uk.gov.pay.connector.model.domain.ChargeEntityFixture.aValidChargeEntity;
import static uk.gov.pay.connector.util.AuthUtils.buildAuthCardDetails;

@RunWith(MockitoJUnitRunner.class)
public class StripePaymentProviderTest {

    private String url = "https://api.stripe.com/v1";
    private ChargeEntity chargeEntity;
    private MetricRegistry mockMetricRegistry;
    private Histogram mockHistogram;
    private Counter mockCounter;

    @Test
    public void shouldAuthoriseSuccessfully() {
        setUpAndCheckThatStripeIsUp();
        PaymentProvider paymentProvider = getStripePaymentProvider();
        AuthorisationGatewayRequest request = buildAuthorisationRequest(chargeEntity);
        GatewayResponse<StripeChargeResponse> response = paymentProvider.authorise(request);
        assertThat(response.isSuccessful(), is(true));
    }

    private PaymentProvider getStripePaymentProvider() {
        Client client = TestClientFactory.createJerseyClient();
        GatewayClient gatewayClient = new GatewayClient(client, ImmutableMap.of(TEST.toString(), url),
                StripePaymentProvider.includeSessionIdentifier(), mockMetricRegistry);
        EnumMap<GatewayOperation, GatewayClient> gatewayClients = GatewayOperationClientBuilder.builder()
                .authClient(gatewayClient)
//                .captureClient(gatewayClient)
//                .cancelClient(gatewayClient)
//                .refundClient(gatewayClient)
                .build();
        return new StripePaymentProvider(gatewayClients, new DefaultExternalRefundAvailabilityCalculator());
    }

    private static AuthorisationGatewayRequest buildAuthorisationRequest(ChargeEntity chargeEntity) {
        return buildAuthorisationRequest(chargeEntity, "Mr. Payment");
    }

    private static AuthorisationGatewayRequest buildAuthorisationRequest(ChargeEntity chargeEntity, String cardholderName) {
        Address address = Address.anAddress();
        address.setLine1("41");
        address.setLine2("Scala Street");
        address.setCity("London");
        address.setCounty("London");
        address.setPostcode("EC2A 1AE");
        address.setCountry("GB");

        AuthCardDetails authCardDetails = aValidStripeCard();
        authCardDetails.setCardHolder(cardholderName);
        authCardDetails.setAddress(address);

        return new AuthorisationGatewayRequest(chargeEntity, authCardDetails);
    }

    private void setUpAndCheckThatStripeIsUp() {
        stripeSetupWithStatusCheck(false);
    }

    private void stripeSetupWithStatusCheck(boolean require3ds) {
        try {
            new URL(url).openConnection().connect();
            Map<String, String> validStripeCredentials = ImmutableMap.of(
                    "apiKey", "sk_test_5R92VZnYjN96ga3p5w07S8wL:"
            );
            GatewayAccountEntity validGatewayAccount = new GatewayAccountEntity();
            validGatewayAccount.setId(123L);
            validGatewayAccount.setGatewayName("stripe");
            validGatewayAccount.setCredentials(validStripeCredentials);
            validGatewayAccount.setType(TEST);
            validGatewayAccount.setRequires3ds(require3ds);

            chargeEntity = aValidChargeEntity()
                    .withGatewayAccountEntity(validGatewayAccount)
                    .withTransactionId(randomUUID().toString())
                    .build();

            mockMetricRegistry = mock(MetricRegistry.class);
            mockHistogram = mock(Histogram.class);
            mockCounter = mock(Counter.class);
            when(mockMetricRegistry.histogram(anyString())).thenReturn(mockHistogram);
            when(mockMetricRegistry.counter(anyString())).thenReturn(mockCounter);
        } catch (IOException ex) {
            Assume.assumeTrue(false);
        }
    }

    private static AuthCardDetails aValidStripeCard() {
        String validStripeCard = "4242424242424242";
        return buildAuthCardDetails(validStripeCard, "737", "08/28", "visa");
    }

}
