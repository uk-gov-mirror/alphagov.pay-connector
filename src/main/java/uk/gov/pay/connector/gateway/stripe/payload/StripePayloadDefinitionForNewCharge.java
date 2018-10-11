package uk.gov.pay.connector.gateway.stripe.payload;

import com.google.common.collect.ImmutableList;
import org.apache.http.NameValuePair;
import uk.gov.pay.connector.gateway.templates.PayloadDefinition;
import uk.gov.pay.connector.gateway.stripe.builder.StripeOrderRequestBuilder;

import static uk.gov.pay.connector.gateway.stripe.payload.StripePayloadDefinition.newParameterBuilder;

public class StripePayloadDefinitionForNewCharge implements PayloadDefinition<StripeOrderRequestBuilder.StripeTemplateData> {

    final static String AMOUNT_KEY = "amount";
    final static String DESCRIPTION = "description";
    final static String CURRENCY_KEY = "currency";
    final static String CAPTURE_KEY = "capture";
    final static String SOURCE_KEY = "source";


    @Override
    public ImmutableList<NameValuePair> extract(StripeOrderRequestBuilder.StripeTemplateData templateData) {

        // Keep this list in alphabetical order
        return newParameterBuilder()
                .add(AMOUNT_KEY, templateData.getAmount())
//                .add(CAPTURE_KEY, "false")
                .add(CURRENCY_KEY, "GBP")
                .add(DESCRIPTION, templateData.getDescription())
                .add(SOURCE_KEY, templateData.getSource())
                .build();
    }

}
