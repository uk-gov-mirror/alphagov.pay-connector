package uk.gov.pay.connector.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Auth3dsDetails implements AuthorisationDetails {

    public enum Auth3dsResult {
        AUTHORISED, DECLINED, ERROR, CANCELED
    }

    private String paResponse;

    private Auth3dsResult auth3DsResult;

    private String md;

    @JsonProperty("pa_response")
    public void setPaResponse(String paResponse) {
            this.paResponse = paResponse;
        }

    public String getPaResponse() {
            return paResponse;
        }

    public Auth3dsResult getAuth3DsResult() {
        return auth3DsResult;
    }

    @JsonProperty("auth_3ds_result")
    public void setAuth3dsResult(String auth3dsResult) {
        this.auth3DsResult = auth3dsResult == null ? Auth3dsResult.ERROR : Auth3dsResult.valueOf(auth3dsResult);
    }

    @JsonProperty("md")
    public void setMd(String md) {
        this.md = md;
    }

    public String getMd() {
        return this.md;
    }

    @Override
    public String toString() {
        return "Auth3dsDetails{" +
                "paResponse='" + paResponse + '\'' +
                ", auth3DsResult=" + auth3DsResult +
                ", md='" + md + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Auth3dsDetails that = (Auth3dsDetails) o;

        return Objects.equals(paResponse, that.paResponse)
            && auth3DsResult != that.auth3DsResult
            && Objects.equals(md, that.md);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paResponse, auth3DsResult, md);
    }
}
