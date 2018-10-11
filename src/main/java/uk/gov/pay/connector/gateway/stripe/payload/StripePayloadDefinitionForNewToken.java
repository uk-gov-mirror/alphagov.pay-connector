package uk.gov.pay.connector.gateway.stripe.payload;

import com.google.common.collect.ImmutableList;
import org.apache.http.NameValuePair;
import uk.gov.pay.connector.gateway.templates.PayloadDefinition;
import uk.gov.pay.connector.gateway.stripe.builder.StripeTokenRequestBuilder;

import static uk.gov.pay.connector.gateway.stripe.payload.StripePayloadDefinition.newParameterBuilder;

public class StripePayloadDefinitionForNewToken implements PayloadDefinition<StripeTokenRequestBuilder.StripeTokenData> {

    final static String CARD_NUMBER_KEY = "card[number]";
    final static String CARD_EXPIRE_MONTH_KEY = "card[exp_month]";
    final static String CARD_EXPIRE_YEAR_KEY = "card[exp_year]";
    final static String CARD_CVC_KEY = "card[cvc]";

    @Override
    public ImmutableList<NameValuePair> extract(StripeTokenRequestBuilder.StripeTokenData templateData) {

        // Keep this list in alphabetical order
        return newParameterBuilder()
                .add(CARD_CVC_KEY, templateData.getCvc())
                .add(CARD_EXPIRE_MONTH_KEY, templateData.getExpMonth().split("/")[0])
                .add(CARD_EXPIRE_YEAR_KEY, templateData.getExpYear().split("/")[1])
                .add(CARD_NUMBER_KEY, templateData.getCardNumber())
                .build();
    }

}
