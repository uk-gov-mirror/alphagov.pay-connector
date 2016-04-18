package uk.gov.pay.connector.service;

import uk.gov.pay.connector.exception.ConflictRuntimeException;
import uk.gov.pay.connector.model.GatewayResponse;
import uk.gov.pay.connector.model.domain.ChargeEntity;

import javax.persistence.OptimisticLockException;

import static java.lang.String.format;

interface TransactionalGatewayOperation {

    default GatewayResponse executeGatewayOperationFor(ChargeEntity chargeEntity) {
        ChargeEntity preOperationResponse;
        try {
            preOperationResponse = preOperation(chargeEntity);
        } catch (OptimisticLockException e) {
            throw new ConflictRuntimeException(format("Operation for charge conflicting, %s", chargeEntity.getExternalId()));
        }

        GatewayResponse operationResponse = operation(preOperationResponse);
        GatewayResponse postOperationResponse = postOperation(preOperationResponse, operationResponse);

        return postOperationResponse;
    }

    ChargeEntity preOperation(ChargeEntity chargeEntity);

    GatewayResponse operation(ChargeEntity chargeEntity);

    GatewayResponse postOperation(ChargeEntity chargeEntity, GatewayResponse operationResponse);
}
