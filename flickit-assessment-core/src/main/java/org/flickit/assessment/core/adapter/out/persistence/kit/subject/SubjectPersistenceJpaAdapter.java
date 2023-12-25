package org.flickit.assessment.core.adapter.out.persistence.kit.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.qualityattribute.QualityAttributeMapper;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJoinAttributeView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;

@Component("coreSubjectPersistenceJpaAdapter")
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements LoadSubjectByAssessmentKitIdPort {

    private final SubjectJpaRepository repository;

    @Override
    public List<Subject> loadByAssessmentKitId(Long kitId) {
        var views = repository.loadByAssessmentKitId(kitId);

        Map<Long, List<SubjectJoinAttributeView>> collect = views.stream().collect(groupingBy(x -> x.getSubject().getId()));

        return collect.values().stream().map(result -> {
            SubjectJpaEntity subject = result.stream().findFirst().orElseThrow().getSubject();
            List<QualityAttribute> attributes = result.stream()
                .map(SubjectJoinAttributeView::getAttribute)
                .filter(Objects::nonNull)
                .map(QualityAttributeMapper::mapToDomainModel)
                .toList();

            return SubjectMapper.mapToDomainModel(subject, attributes);
        }).toList();
    }
}
