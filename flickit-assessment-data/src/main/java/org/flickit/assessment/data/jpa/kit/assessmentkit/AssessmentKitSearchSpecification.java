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
        Predicate titleMatch = buildTitleSearchPredicate(root, cb);
        Predicate published = cb.isTrue(root.get(AssessmentKitJpaEntity.Fields.published));
        Predicate hasAccess = toAccessCheckPredicate(root, query, cb);

        return cb.and(published, hasAccess, titleMatch);
    }

    private Predicate buildTitleSearchPredicate(Root<AssessmentKitJpaEntity> root, CriteriaBuilder cb) {
        String searchPattern = "%" + queryTerm.toLowerCase() + "%";
        Expression<String> defaultTitle = cb.lower(root.get(AssessmentKitJpaEntity.Fields.title));
        Predicate titleMatch = cb.like(defaultTitle, searchPattern);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(titleMatch);

        for (KitLanguage language : KitLanguage.values()) {
            Expression<String> translatedTitle = cb.lower(
                cb.function(JSON_EXTRACT_PATH_TEXT, String.class,
                    root.get(AssessmentKitJpaEntity.Fields.translations),
                    cb.literal(language.name()), cb.literal(AssessmentKitJpaEntity.Fields.title))
            );
            predicates.add(cb.like(translatedTitle, searchPattern));
        }
        return cb.or(predicates.toArray(new Predicate[0]));
    }

    private Predicate toAccessCheckPredicate(Root<AssessmentKitJpaEntity> root,
                                             CriteriaQuery<?> query,
                                             CriteriaBuilder cb) {
        Predicate isPublic = cb.isFalse(root.get(AssessmentKitJpaEntity.Fields.isPrivate));

        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<KitUserAccessJpaEntity> subRoot = subQuery.from(KitUserAccessJpaEntity.class);
        subQuery.select(subRoot.get(KitUserAccessJpaEntity.Fields.kitId))
            .where(cb.equal(subRoot.get(KitUserAccessJpaEntity.Fields.userId), userId));

        Predicate isPrivate = cb.isTrue(root.get(AssessmentKitJpaEntity.Fields.isPrivate));
        Predicate userHasAccess = cb.in(root.get(AssessmentKitJpaEntity.Fields.id)).value(subQuery);
        Predicate privateAndAccessible = cb.and(isPrivate, userHasAccess);

        return cb.or(isPublic, privateAndAccessible);
    }
}
