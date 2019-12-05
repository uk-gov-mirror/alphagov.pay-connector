package uk.gov.pay.connector.charge.dao;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import uk.gov.pay.connector.charge.model.domain.ChargeEntity;
import uk.gov.pay.connector.charge.model.domain.ChargeStatus;
import uk.gov.pay.connector.charge.model.domain.ParityCheckStatus;
import uk.gov.pay.connector.common.dao.JpaDao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.gov.pay.connector.charge.model.domain.ChargeStatus.CAPTURE_APPROVED;
import static uk.gov.pay.connector.charge.model.domain.ChargeStatus.CAPTURE_APPROVED_RETRY;

@Transactional
public class ChargeDao extends JpaDao<ChargeEntity> {

    private static final String STATUS = "status";
    private static final String CREATED_DATE = "createdDate";

    @Inject
    public ChargeDao(final Provider<EntityManager> entityManager) {
        super(entityManager);
    }

    public Optional<ChargeEntity> findById(Long chargeId) {
        return super.findById(ChargeEntity.class, chargeId);
    }

    public Optional<ChargeEntity> findByExternalId(String externalId) {

        String query = "SELECT c FROM ChargeEntity c " +
                "WHERE c.externalId = :externalId";

        return entityManager.get()
                .createQuery(query, ChargeEntity.class)
                .setParameter("externalId", externalId)
                .getResultList().stream().findFirst();
    }

    public Optional<ChargeEntity> findByTokenId(String tokenId) {
        String query = "SELECT te.chargeEntity FROM TokenEntity te WHERE te.token=:tokenId AND te.used=false";

        return entityManager.get()
                .createQuery(query, ChargeEntity.class)
                .setParameter("tokenId", tokenId)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Optional<ChargeEntity> findByGatewayTransactionId(String gatewayTransactionId) {
        String query = "SELECT c from ChargeEntity c WHERE c.gatewayTransactionId=:gatewayTransactionId";

        return entityManager.get()
                .createQuery(query, ChargeEntity.class)
                .setParameter("gatewayTransactionId", gatewayTransactionId)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Optional<ChargeEntity> findByExternalIdAndGatewayAccount(String externalId, Long accountId) {

        String query = "SELECT c FROM ChargeEntity c " +
                "WHERE c.externalId = :externalId " +
                "AND c.gatewayAccount.id = :accountId";

        return entityManager.get()
                .createQuery(query, ChargeEntity.class)
                .setParameter("externalId", externalId)
                .setParameter("accountId", accountId)
                .getResultList().stream().findFirst();
    }

    public Optional<ChargeEntity> findByProviderAndTransactionId(String provider, String transactionId) {

        String query = "SELECT c FROM ChargeEntity c " +
                "WHERE c.gatewayTransactionId = :gatewayTransactionId " +
                "AND c.gatewayAccount.gatewayName = :provider";

        return entityManager.get()
                .createQuery(query, ChargeEntity.class)
                .setParameter("gatewayTransactionId", transactionId)
                .setParameter("provider", provider).getResultList().stream().findFirst();
    }

    public List<ChargeEntity> findBeforeDateWithStatusIn(ZonedDateTime date, List<ChargeStatus> statuses) {
        SearchParams params = new SearchParams()
                .withToDate(date)
                .withInternalStates(statuses);

        CriteriaBuilder cb = entityManager.get().getCriteriaBuilder();
        CriteriaQuery<ChargeEntity> cq = cb.createQuery(ChargeEntity.class);
        Root<ChargeEntity> charge = cq.from(ChargeEntity.class);

        List<Predicate> predicates = buildParamPredicates(params, cb, charge);
        cq.select(charge)
                .where(predicates.toArray(new Predicate[]{}))
                .orderBy(cb.desc(charge.get(CREATED_DATE)));
        Query query = entityManager.get().createQuery(cq);

        return query.getResultList();
    }

    private List<Predicate> buildParamPredicates(SearchParams params, CriteriaBuilder cb, Root<ChargeEntity> charge) {
        List<Predicate> predicates = new ArrayList<>();
        if (params.getInternalStates() != null && !params.getInternalStates().isEmpty())
            predicates.add(charge.get(STATUS).in(params.getInternalStates()));
        if (params.getToDate() != null)
            predicates.add(cb.lessThan(charge.get(CREATED_DATE), params.getToDate()));

        return predicates;
    }

    private static final String FIND_CAPTURE_CHARGES_WHERE_CLAUSE =
            "WHERE (c.status=:captureApprovedStatus OR c.status=:captureApprovedRetryStatus)" +
                    "AND NOT EXISTS (" +
                    "  SELECT ce FROM ChargeEventEntity ce WHERE " +
                    "    ce.chargeEntity = c AND " +
                    "    ce.status = :eventStatus AND " +
                    "    ce.updated >= :cutoffDate " +
                    ") ";

    public int countChargesForImmediateCapture(Duration notAttemptedWithin) {
        String query = "SELECT count(c) FROM ChargeEntity c " + FIND_CAPTURE_CHARGES_WHERE_CLAUSE;

        ZonedDateTime utcCutoffThreshold = ZonedDateTime.now()
                .minus(notAttemptedWithin)
                .withZoneSameInstant(ZoneId.of("UTC"));

        var count = (Number) entityManager.get()
                .createQuery(query)
                .setParameter("captureApprovedStatus", CAPTURE_APPROVED.getValue())
                .setParameter("captureApprovedRetryStatus", CAPTURE_APPROVED_RETRY.getValue())
                .setParameter("eventStatus", CAPTURE_APPROVED_RETRY)
                .setParameter("cutoffDate", utcCutoffThreshold)
                .getSingleResult();
        return count.intValue();
    }

    public int countCaptureRetriesForChargeExternalId(String externalId) {
        String query = "SELECT count(ce) FROM ChargeEventEntity ce WHERE " +
                "    ce.chargeEntity.externalId = :externalId AND " +
                "    (ce.status = :captureApprovedStatus OR ce.status = :captureApprovedRetryStatus)";

        return ((Number) entityManager.get()
                .createQuery(query)
                .setParameter("externalId", externalId)
                .setParameter("captureApprovedStatus", CAPTURE_APPROVED)
                .setParameter("captureApprovedRetryStatus", CAPTURE_APPROVED_RETRY)
                .getSingleResult()).intValue();
    }

    public List<ChargeEntity> findByIdAndLimit(Long id, int limit) {
        return entityManager.get()
                .createQuery("SELECT c FROM ChargeEntity c WHERE c.id > :id ORDER BY c.id", ChargeEntity.class)
                .setParameter("id", id)
                .setMaxResults(limit)
                .getResultList();
    }

    public Long findMaxId() {
        String query = "SELECT c.id FROM ChargeEntity c ORDER BY c.id DESC";

        return entityManager.get()
                .createQuery(query, Long.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<ChargeEntity> findByParityCheckStatus(ParityCheckStatus parityCheckStatus, int size, Long lastProcessedId) {
        return entityManager.get()
                .createQuery("SELECT c FROM ChargeEntity c WHERE c.id > :lastProcessedId AND c.parityCheckStatus = :parityCheckStatus ORDER BY c.id", ChargeEntity.class)
                .setParameter("parityCheckStatus", parityCheckStatus)
                .setParameter("lastProcessedId", lastProcessedId)
                .setMaxResults(size)
                .getResultList();
    }
}
