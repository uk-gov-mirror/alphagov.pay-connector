package uk.gov.pay.connector.gateway.stripe;

import fj.data.Either;
import uk.gov.pay.connector.charge.model.domain.ChargeEntity;
import uk.gov.pay.connector.common.model.api.ExternalChargeRefundAvailability;
import uk.gov.pay.connector.gateway.BasePaymentProvider;
import uk.gov.pay.connector.gateway.GatewayClient;
import uk.gov.pay.connector.gateway.GatewayOperation;
import uk.gov.pay.connector.gateway.GatewayOrder;
import uk.gov.pay.connector.gateway.PaymentGatewayName;
import uk.gov.pay.connector.gateway.StatusMapper;
import uk.gov.pay.connector.gateway.model.request.Auth3dsResponseGatewayRequest;
import uk.gov.pay.connector.gateway.model.request.AuthorisationGatewayRequest;
import uk.gov.pay.connector.gateway.model.request.CancelGatewayRequest;
import uk.gov.pay.connector.gateway.model.request.CaptureGatewayRequest;
import uk.gov.pay.connector.gateway.model.request.RefundGatewayRequest;
import uk.gov.pay.connector.gateway.model.response.BaseResponse;
import uk.gov.pay.connector.gateway.model.response.GatewayResponse;
import uk.gov.pay.connector.gateway.util.ExternalRefundAvailabilityCalculator;
import uk.gov.pay.connector.gatewayaccount.model.GatewayAccountEntity;
import uk.gov.pay.connector.gateway.stripe.builder.StripeOrderRequestBuilder;
import uk.gov.pay.connector.gateway.stripe.builder.StripeTokenRequestBuilder;
import uk.gov.pay.connector.gateway.stripe.response.StripeChargeResponse;
import uk.gov.pay.connector.gateway.stripe.response.StripeTokenResponse;
import uk.gov.pay.connector.usernotification.model.Notification;
import uk.gov.pay.connector.usernotification.model.Notifications;

import javax.ws.rs.client.Invocation;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.UUID.randomUUID;

public class StripePaymentProvider extends BasePaymentProvider<BaseResponse, String> {

    public static final Charset STRIPE_APPLICATION_X_WWW_FORM_URLENCODED_CHARSET = Charset.forName("windows-1252");

    public static final String ROUTE_FOR_NEW_CHARGE = "charges";
    public static final String ROUTE_FOR_TOKEN = "tokens";

    public StripePaymentProvider(EnumMap<GatewayOperation, GatewayClient> clients, ExternalRefundAvailabilityCalculator externalRefundAvailabilityCalculator) {
        super(clients, externalRefundAvailabilityCalculator);
    }

    @Override
    public PaymentGatewayName getPaymentGatewayName() {
        return PaymentGatewayName.STRIPE;
    }

    @Override
    public Optional<String> generateTransactionId() {
        return Optional.of(randomUUID().toString());
    }

    @Override
    public GatewayResponse authorise(AuthorisationGatewayRequest request) {
        Optional<StripeTokenResponse> stripeTokenResponse = getStripeTokenForCharge(request);
        return sendReceive(ROUTE_FOR_NEW_CHARGE, request, buildAuthoriseOrderFor(stripeTokenResponse.get().getTransactionId()), (Class<? extends BaseResponse>) StripeChargeResponse.class, extractResponseIdentifier());
    }

    private Optional<StripeTokenResponse> getStripeTokenForCharge(AuthorisationGatewayRequest request) {
        GatewayResponse response = createStripeTokenForCharge(request);
        return (Optional<StripeTokenResponse>) response.getBaseResponse();
    }

    public GatewayResponse createStripeTokenForCharge(AuthorisationGatewayRequest request) {
        return sendReceive(ROUTE_FOR_TOKEN, request, buildTokenOrderFor(), StripeTokenResponse.class, extractResponseIdentifier());
    }

    @Override
    public GatewayResponse<BaseResponse> authorise3dsResponse(Auth3dsResponseGatewayRequest request) {
        return null;
    }

    @Override
    public GatewayResponse capture(CaptureGatewayRequest request) {
        return null;
    }

    @Override
    public GatewayResponse refund(RefundGatewayRequest request) {
        return null;
    }

    @Override
    public GatewayResponse cancel(CancelGatewayRequest request) {
        return null;
    }

    @Override
    public Boolean isNotificationEndpointSecured() {
        return this.isNotificationEndpointSecured;
    }

    @Override
    public String getNotificationDomain() {
        return this.notificationDomain;
    }

    @Override
    public boolean verifyNotification(Notification<String> notification, GatewayAccountEntity gatewayAccountEntity) {
        return false;
    }

    @Override
    public ExternalChargeRefundAvailability getExternalChargeRefundAvailability(ChargeEntity chargeEntity) {
        return externalRefundAvailabilityCalculator.calculate(chargeEntity);
    }

    @Override
    public Either<String, Notifications<String>> parseNotification(String payload) {
        return null;
    }

    @Override
    public StatusMapper<String> getStatusMapper() {
        return StripeStatusMapper.get();
    }

    private Function<AuthorisationGatewayRequest, GatewayOrder> buildAuthoriseOrderFor(String token) {
        return request -> {
            StripeOrderRequestBuilder stripeOrderRequestBuilder =
                    StripeOrderRequestBuilder.aStripeAuthoriseOrderRequestBuilder();
            return stripeOrderRequestBuilder
                    .withAmount(request.getAmount())
                    .withToken(token)
                    .withDescription(request.getDescription())
                    .withAuthorisationDetails(request.getAuthCardDetails())
                    .build();
        };
    }

    private Function<AuthorisationGatewayRequest, GatewayOrder> buildTokenOrderFor() {
        return request -> {
            StripeTokenRequestBuilder stripeTokenRequestBuilder =
                    StripeTokenRequestBuilder.aStripeTokenRequestBuilder();
            return stripeTokenRequestBuilder
                    .withCardNumber(request.getAuthCardDetails().getCardNo())
                    .withExpMonth(request.getAuthCardDetails().getEndDate())
                    .withExpYear(request.getAuthCardDetails().getEndDate())
                    .withCvc(request.getAuthCardDetails().getCvc())
                    .build();
        };
    }

    public static BiFunction<GatewayOrder, Invocation.Builder, Invocation.Builder> includeSessionIdentifier() {
        return (order, builder) -> builder;
    }

    private Function<GatewayClient.Response, Optional<String>> extractResponseIdentifier() {
        return response -> {
            Optional<String> emptyResponseIdentifierForStripe = Optional.empty();
            return emptyResponseIdentifierForStripe;
        };
    }
}
