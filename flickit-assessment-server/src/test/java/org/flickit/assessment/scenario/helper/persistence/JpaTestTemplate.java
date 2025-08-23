package org.flickit.assessment.scenario.helper.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Repository
@Transactional
public class JpaTestTemplate {

    protected EntityManager em;

    @PersistenceContext
    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void persist(Object... entities) {
        Stream.of(entities)
            .forEach(em::persist);
    }

    public <T> T load(Object id, Class<T> type) {
        return em.find(type, id);
    }

    public <T> List<T> search(Class<T> type, Specification<T> spec) {
        return em.createQuery(createQuery(type, spec)).getResultList();
    }

    public <T> T findSingle(Class<T> type, Specification<T> spec) {
        return em.createQuery(createQuery(type, spec)).getSingleResult();
    }

    public <T> T findTop(Class<T> type, Specification<T> spec) {
        CriteriaQuery<T> cq = createQuery(type, spec);
        TypedQuery<T> query = em.createQuery(cq);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    private <T> CriteriaQuery<T> createQuery(Class<T> type, Specification<T> spec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> r = cq.from(type);
        cq.select(r).where(spec.toPredicate(r, cq, cb));
        return cq;
    }

    public <T> boolean existById(Object id, Class<T> type) {
        return count(type) > 0;
    }

    public <T> int count(Class<T> type, Specification<T> spec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> r = cq.from(type);
        cq.select(cb.count(r)).where(spec.toPredicate(r, cq, cb));
        Long count = em.createQuery(cq).getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public <T> int count(Class<T> type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> r = cq.from(type);
        cq.select(cb.count(r));
        Long count = em.createQuery(cq).getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    public <T> int maxInt(Class<T> type, String propName, Specification<T> spec) {
        return (int) maxLong(type, propName, spec);
    }

    public <T> long maxLong(Class<T> type, String propName, Specification<T> spec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<T> r = cq.from(type);
        cq.select(cb.max(r.<Number>get(propName))).where(spec.toPredicate(r, cq, cb));
        Number max = em.createQuery(cq).getSingleResult();
        return max != null ? max.intValue() : 0;
    }

    public void truncate(Class<?>... entityTypes) {
        Stream.of(entityTypes)
            .map(this::extractTableName)
            .forEach(this::truncateTable);
    }

    private String extractTableName(Class<?> entityType) {
        Table annotation = entityType.getAnnotation(Table.class);
        return annotation.name();
    }

    private void truncateTable(String tableName) {
        em.createNativeQuery(String.format("TRUNCATE TABLE %s", tableName)).executeUpdate();
    }

}
