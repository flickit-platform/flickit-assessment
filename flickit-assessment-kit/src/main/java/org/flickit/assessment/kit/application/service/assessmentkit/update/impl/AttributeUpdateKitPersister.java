package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class AttributeUpdateKitPersister implements UpdateKitPersister {

    private final UpdateAttributePort updateAttributePort;

    @Override
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx, AssessmentKit savedKit, AssessmentKitDslModel dslKit) {

        Map<String, Long> collect = savedKit.getSubjects().stream()
            .collect(Collectors.toMap(Subject::getCode, Subject::getId));

        Map<String, Attribute> codeToAttribute = savedKit.getSubjects().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(Attribute::getCode, Function.identity()));


        Map<String, Long> codeToSubjectId = new HashMap<>();

        savedKit.getSubjects().forEach(e -> {
            e.getAttributes().forEach(o -> codeToSubjectId.put(o.getCode(), e.getId()));
        });

        boolean shouldInvalidated = false;
        for (AttributeDslModel e : dslKit.getAttributes()) {
            String subjectCode = e.getSubjectCode();
            Long subjectId = collect.get(subjectCode);
            Long oldSubjectId = codeToSubjectId.get(e.getCode());
            if (!Objects.equals(e.getWeight(), codeToAttribute.get(e.getCode()).getWeight()) || !Objects.equals(subjectId, oldSubjectId))
                shouldInvalidated = true;

            updateAttributePort.update(toUpdatePram(codeToAttribute.get(e.getCode()).getId(), subjectId, e));

        }

        Map<String, Long> codeToAttributeId = savedKit.getSubjects().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(Attribute::getCode, Attribute::getId));

        ctx.put(UpdateKitPersisterContext.KEY_ATTRIBUTES, codeToAttributeId);

        return new UpdateKitPersisterResult(shouldInvalidated);
    }

    private UpdateAttributePort.Param toUpdatePram(long id, Long subjectId, AttributeDslModel e) {
        return new UpdateAttributePort.Param(id,
            e.getTitle(),
            e.getIndex(),
            e.getDescription(),
            e.getWeight(),
            LocalDateTime.now(),
            subjectId);
    }

    @Override
    public int order() {
        return 4;
    }

}
