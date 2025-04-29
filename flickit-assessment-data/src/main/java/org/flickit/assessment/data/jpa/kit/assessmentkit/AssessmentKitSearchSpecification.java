package org.flickit.assessment.data.jpa.kit.assessmentkit;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AssessmentKitSearchSpecification implements Specification<AssessmentKitJpaEntity> {

    private static final String JSON_EXTRACT_PATH_TEXT = "json_extract_path_text";

    private final String queryTerm;
    private final UUID userId;

    @Override
    public Predicate toPredicate(Root<AssessmentKitJpaEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> titlesMatch = toTitlePredicates(root, cb);
        Predicate published = cb.isTrue(root.get(AssessmentKitJpaEntity.Fields.published));
        Predicate hasAccess = toAccessCheckPredicate(root, query, cb);

        return cb.and(published, hasAccess, cb.or(titlesMatch.toArray(new Predicate[0])));
    }

    private List<Predicate> toTitlePredicates(Root<AssessmentKitJpaEntity> root, CriteriaBuilder cb) {
        Expression<String> titleExpression = cb.lower(root.get(AssessmentKitJpaEntity.Fields.title));
        Predicate titleMatch = cb.like(titleExpression, "%" + queryTerm.toLowerCase() + "%");

        List<Predicate> titlesMatch = new ArrayList<>();
        titlesMatch.add(titleMatch);

        for (KitLanguage language : KitLanguage.values()) {
            Expression<String> translatedTitleExpression = cb.lower(
                cb.function(JSON_EXTRACT_PATH_TEXT, String.class,
                    root.get(AssessmentKitJpaEntity.Fields.translations),
                    cb.literal(language.name()), cb.literal(AssessmentKitJpaEntity.Fields.title))
            );
            titlesMatch.add(cb.like(translatedTitleExpression, "%" + queryTerm.toLowerCase() + "%"));
        }
        return titlesMatch;
    }

    private Predicate toAccessCheckPredicate(Root<AssessmentKitJpaEntity> root,
                                             CriteriaQuery<?> query,
                                             CriteriaBuilder cb) {
        Predicate isPublic = cb.isFalse(root.get(AssessmentKitJpaEntity.Fields.isPrivate));
        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<KitUserAccessJpaEntity> subRoot = subQuery.from(KitUserAccessJpaEntity.class);
        subQuery.select(subRoot.get(KitUserAccessJpaEntity.Fields.kitId))
            .where(cb.equal(subRoot.get(KitUserAccessJpaEntity.Fields.userId), userId));

        Predicate hasAccess = cb.in(root.get(AssessmentKitJpaEntity.Fields.id)).value(subQuery);
        return cb.or(isPublic, cb.and(cb.isTrue(root.get(AssessmentKitJpaEntity.Fields.isPrivate)), hasAccess));
    }
}
