package uk.gov.pay.connector.gateway.stripe.builder;

import uk.gov.pay.connector.gateway.OrderRequestBuilder;
import uk.gov.pay.connector.gateway.model.OrderRequestType;
import uk.gov.pay.connector.gateway.templates.FormUrlEncodedStringBuilder;
import uk.gov.pay.connector.gateway.templates.PayloadBuilder;
import uk.gov.pay.connector.gateway.templates.PayloadDefinition;
import uk.gov.pay.connector.gateway.stripe.payload.StripePayloadDefinitionForNewToken;

import javax.ws.rs.core.MediaType;

import static uk.gov.pay.connector.gateway.stripe.StripePaymentProvider.STRIPE_APPLICATION_X_WWW_FORM_URLENCODED_CHARSET;

public class StripeTokenRequestBuilder extends OrderRequestBuilder {
    public static class StripeTokenData extends TemplateData{
        private String cardNumber;
        private String expMonth;
        private String expYear;
        private String cvc;

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpMonth() {
            return expMonth;
        }

        public void setExpMonth(String expMonth) {
            this.expMonth = expMonth;
        }

        public String getExpYear() {
            return expYear;
        }

        public void setExpYear(String expYear) {
            this.expYear = expYear;
        }

        public String getCvc() {
            return cvc;
        }

        public void setCvc(String cvc) {
            this.cvc = cvc;
        }
    }

    private static final PayloadBuilder TOKEN_TEMPLATE_BUILDER = createPayloadBuilderForNewToken();

    private StripeTokenData stripeTemplateData;

    private static PayloadBuilder createPayloadBuilderForNewToken() {
        PayloadDefinition payloadForToken = new StripePayloadDefinitionForNewToken();
        return new FormUrlEncodedStringBuilder(payloadForToken, STRIPE_APPLICATION_X_WWW_FORM_URLENCODED_CHARSET);
    }

    public static StripeTokenRequestBuilder aStripeTokenRequestBuilder() {
        return new StripeTokenRequestBuilder(new StripeTokenData(), TOKEN_TEMPLATE_BUILDER, OrderRequestType.TOKEN);
    }

    private StripeTokenRequestBuilder(StripeTokenData stripeTemplateData, PayloadBuilder payloadBuilder, OrderRequestType orderRequestType) {
        super(stripeTemplateData, payloadBuilder, orderRequestType);
        this.stripeTemplateData = stripeTemplateData;
    }

    public StripeTokenRequestBuilder withCardNumber(String cardNumber) {
        stripeTemplateData.setCardNumber(cardNumber);
        return this;
    }

    public StripeTokenRequestBuilder withExpMonth(String expMonth) {
        stripeTemplateData.setExpMonth(expMonth);
        return this;
    }
  public StripeTokenRequestBuilder withExpYear(String expYear) {
        stripeTemplateData.setExpYear(expYear);
        return this;
    }
  public StripeTokenRequestBuilder withCvc(String cvc) {
        stripeTemplateData.setCvc(cvc);
        return this;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.APPLICATION_FORM_URLENCODED_TYPE;
    }
}
