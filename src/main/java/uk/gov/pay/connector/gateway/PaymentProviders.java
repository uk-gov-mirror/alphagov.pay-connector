package uk.gov.pay.connector.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.setup.Environment;
import uk.gov.pay.connector.app.ConnectorConfiguration;
import uk.gov.pay.connector.app.WorldpayConfig;
import uk.gov.pay.connector.gateway.epdq.EpdqPaymentProvider;
import uk.gov.pay.connector.gateway.epdq.EpdqSha512SignatureGenerator;
import uk.gov.pay.connector.gateway.model.response.BaseResponse;
import uk.gov.pay.connector.gateway.sandbox.SandboxPaymentProvider;
import uk.gov.pay.connector.gateway.smartpay.SmartpayPaymentProvider;
import uk.gov.pay.connector.gateway.util.DefaultExternalRefundAvailabilityCalculator;
import uk.gov.pay.connector.gateway.util.EpdqExternalRefundAvailabilityCalculator;
import uk.gov.pay.connector.gateway.util.ExternalRefundAvailabilityCalculator;
import uk.gov.pay.connector.gateway.worldpay.WorldpayPaymentProvider;
import uk.gov.pay.connector.gateway.stripe.StripePaymentProvider;

import javax.inject.Inject;
import javax.ws.rs.client.Invocation;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

import static jersey.repackaged.com.google.common.collect.Maps.newHashMap;
import static uk.gov.pay.connector.gateway.GatewayOperation.AUTHORISE;
import static uk.gov.pay.connector.gateway.PaymentGatewayName.EPDQ;
import static uk.gov.pay.connector.gateway.PaymentGatewayName.SANDBOX;
import static uk.gov.pay.connector.gateway.PaymentGatewayName.SMARTPAY;
import static uk.gov.pay.connector.gateway.PaymentGatewayName.STRIPE;
import static uk.gov.pay.connector.gateway.PaymentGatewayName.WORLDPAY;

/**
 * TODO: Currently, the usage of this class at runtime is a single instance instantiated by ConnectorApp.
 * - In this instance we are creating 3 instances for each provider which internally holds an instance of a GatewayClient
 * - Due to this all calls to a particular gateway goes via this single instance.
 * - We are currently not sure of the state in Dropwizard's Jersey Client wrapper and if so this may lead to multi-threading issues
 * - Potential refactoring after a performance test
 */
public class PaymentProviders<T extends BaseResponse> {

    private final Map<PaymentGatewayName, PaymentProvider> paymentProviders = newHashMap();
    private final GatewayClientFactory gatewayClientFactory;
    private final Environment environment;
    private final ConnectorConfiguration config;
    private final ExternalRefundAvailabilityCalculator defaultExternalRefundAvailabilityCalculator = new DefaultExternalRefundAvailabilityCalculator();
    private final ExternalRefundAvailabilityCalculator epdqExternalRefundAvailabilityCalculator = new EpdqExternalRefundAvailabilityCalculator();

    @Inject
    public PaymentProviders(ConnectorConfiguration config, GatewayClientFactory gatewayClientFactory, ObjectMapper objectMapper, Environment environment) {
        this.gatewayClientFactory = gatewayClientFactory;
        this.environment = environment;
        this.config = config;

        this.paymentProviders.put(WORLDPAY, createWorldpayProvider());
        this.paymentProviders.put(SMARTPAY, createSmartPayProvider(objectMapper));
        this.paymentProviders.put(SANDBOX, new SandboxPaymentProvider(defaultExternalRefundAvailabilityCalculator));
        this.paymentProviders.put(EPDQ, createEpdqProvider());
        this.paymentProviders.put(STRIPE, createStripeProvider());

    }

    private GatewayClient gatewayClientForOperation(PaymentGatewayName gateway,
                                                    GatewayOperation operation,
                                                    BiFunction<GatewayOrder, Invocation.Builder, Invocation.Builder> sessionIdentifier) {
        return gatewayClientFactory.createGatewayClient(
                gateway, operation, config.getGatewayConfigFor(gateway).getUrls(), sessionIdentifier, environment.metrics()
        );
    }

    private PaymentProvider createWorldpayProvider() {
        EnumMap<GatewayOperation, GatewayClient> gatewayClientEnumMap = GatewayOperationClientBuilder.builder()
                .authClient(gatewayClientForOperation(WORLDPAY, AUTHORISE, WorldpayPaymentProvider.includeSessionIdentifier()))
                .cancelClient(gatewayClientForOperation(WORLDPAY, GatewayOperation.CANCEL, WorldpayPaymentProvider.includeSessionIdentifier()))
                .captureClient(gatewayClientForOperation(WORLDPAY, GatewayOperation.CAPTURE, WorldpayPaymentProvider.includeSessionIdentifier()))
                .refundClient(gatewayClientForOperation(WORLDPAY, GatewayOperation.REFUND, WorldpayPaymentProvider.includeSessionIdentifier()))
                .build();

        WorldpayConfig worldpayConfig = config.getWorldpayConfig();

        return new WorldpayPaymentProvider(
                gatewayClientEnumMap, worldpayConfig.isSecureNotificationEnabled(), worldpayConfig.getNotificationDomain(), defaultExternalRefundAvailabilityCalculator);
    }

    private PaymentProvider createSmartPayProvider(ObjectMapper objectMapper) {
        EnumMap<GatewayOperation, GatewayClient> gatewayClients = GatewayOperationClientBuilder
                .builder()
                .authClient(gatewayClientForOperation(SMARTPAY, AUTHORISE, SmartpayPaymentProvider.includeSessionIdentifier()))
                .captureClient(gatewayClientForOperation(SMARTPAY, GatewayOperation.CAPTURE, SmartpayPaymentProvider.includeSessionIdentifier()))
                .cancelClient(gatewayClientForOperation(SMARTPAY, GatewayOperation.CANCEL, SmartpayPaymentProvider.includeSessionIdentifier()))
                .refundClient(gatewayClientForOperation(SMARTPAY, GatewayOperation.REFUND, SmartpayPaymentProvider.includeSessionIdentifier()))
                .build();

        return new SmartpayPaymentProvider(
                gatewayClients,
                objectMapper,
                defaultExternalRefundAvailabilityCalculator
        );
    }

    private PaymentProvider createEpdqProvider() {
        EnumMap<GatewayOperation, GatewayClient> gatewayClientEnumMap = GatewayOperationClientBuilder.builder()
                .authClient(gatewayClientForOperation(EPDQ, AUTHORISE, EpdqPaymentProvider.includeSessionIdentifier()))
                .cancelClient(gatewayClientForOperation(EPDQ, GatewayOperation.CANCEL, EpdqPaymentProvider.includeSessionIdentifier()))
                .captureClient(gatewayClientForOperation(EPDQ, GatewayOperation.CAPTURE, EpdqPaymentProvider.includeSessionIdentifier()))
                .refundClient(gatewayClientForOperation(EPDQ, GatewayOperation.REFUND, EpdqPaymentProvider.includeSessionIdentifier()))
                .build();

        return new EpdqPaymentProvider(gatewayClientEnumMap, new EpdqSha512SignatureGenerator(), epdqExternalRefundAvailabilityCalculator, config.getLinks().getFrontendUrl(), environment.metrics());
    }

    private PaymentProvider createStripeProvider() {
        EnumMap<GatewayOperation, GatewayClient> gatewayClientEnumMap = GatewayOperationClientBuilder.builder()
                .authClient(gatewayClientForOperation(STRIPE, AUTHORISE, StripePaymentProvider.includeSessionIdentifier()))
                .build();

        return new StripePaymentProvider(gatewayClientEnumMap, epdqExternalRefundAvailabilityCalculator);
    }

    public PaymentProvider<T, ?> byName(PaymentGatewayName gateway) {
        return paymentProviders.get(gateway);
    }
}
