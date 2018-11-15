package uk.gov.pay.connector.gateway.stripe.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Example response from Stripe:
{
  "error": {
    "message": "Invalid API Key provided: sk_test_************S8wL",
    "type": "invalid_request_error"
  }
}
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class StripeErrorResponse {

    @JsonProperty("error")
    private Error error;

    public Error getError() {
        return error;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Error {

        @JsonProperty("code")
        private String code;
        @JsonProperty("message")
        private String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}