package uk.gov.pay.connector.gateway.stripe.builder;

import uk.gov.pay.connector.gateway.OrderRequestBuilder;
import uk.gov.pay.connector.gateway.model.OrderRequestType;
import uk.gov.pay.connector.gateway.templates.FormUrlEncodedStringBuilder;
import uk.gov.pay.connector.gateway.templates.PayloadBuilder;
import uk.gov.pay.connector.gateway.templates.PayloadDefinition;
import uk.gov.pay.connector.gateway.stripe.payload.StripePayloadDefinitionForNewCharge;

import javax.ws.rs.core.MediaType;

import static uk.gov.pay.connector.gateway.stripe.StripePaymentProvider.STRIPE_APPLICATION_X_WWW_FORM_URLENCODED_CHARSET;

public class StripeOrderRequestBuilder extends OrderRequestBuilder {
    public static class StripeTemplateData extends TemplateData {
        private String operationType;
        private String amount;
        private String currency;
        private String source;
        private String description;
        private String frontendBaseUrl;

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
        }

        public String getFrontendBaseUrl() {
            return frontendBaseUrl;
        }

        public void setFrontendBaseUrl(String frontendBaseUrl) {
            this.frontendBaseUrl = frontendBaseUrl;
        }

        @Override
        public String getAmount() {
            return amount;
        }

        @Override
        public void setAmount(String amount) {
            this.amount = amount;
        }

        public void setFrontendUrl(String frontendUrl) {
            this.frontendBaseUrl = frontendUrl;
        }

        public String getFrontendUrl() {
            return frontendBaseUrl;
        }
    }

    private static final PayloadBuilder AUTHORISE_ORDER_TEMPLATE_BUILDER = createPayloadBuilderForNewOrder();

    private StripeTemplateData stripeTemplateData;

    private static PayloadBuilder createPayloadBuilderForNewOrder() {
        PayloadDefinition payloadDefinition = new StripePayloadDefinitionForNewCharge();
        return new FormUrlEncodedStringBuilder(payloadDefinition, STRIPE_APPLICATION_X_WWW_FORM_URLENCODED_CHARSET);
    }

    public static StripeOrderRequestBuilder aStripeAuthoriseOrderRequestBuilder() {
        return new StripeOrderRequestBuilder(new StripeTemplateData(), AUTHORISE_ORDER_TEMPLATE_BUILDER, OrderRequestType.AUTHORISE);
    }

    private StripeOrderRequestBuilder(StripeTemplateData stripeTemplateData, PayloadBuilder payloadBuilder, OrderRequestType orderRequestType) {
        super(stripeTemplateData, payloadBuilder, orderRequestType);
        this.stripeTemplateData = stripeTemplateData;
    }

    public StripeOrderRequestBuilder withAmount(String amount) {
        stripeTemplateData.setAmount(amount);
        return this;
    } 
    public StripeOrderRequestBuilder withToken(String token) {
        stripeTemplateData.setSource(token);
        return this;
    }

    public StripeOrderRequestBuilder withDescription(String description) {
        stripeTemplateData.setDescription(description);
        return this;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.APPLICATION_FORM_URLENCODED_TYPE;
    }
}
