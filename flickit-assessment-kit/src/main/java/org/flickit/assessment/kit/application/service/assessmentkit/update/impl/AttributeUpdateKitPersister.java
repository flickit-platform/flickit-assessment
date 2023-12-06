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

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeUpdateKitPersister implements UpdateKitPersister {

    private final UpdateAttributePort updateAttributePort;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx, AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Map<String, Long> subjectCodeToSubjectId = savedKit.getSubjects().stream()
            .collect(Collectors.toMap(Subject::getCode, Subject::getId));

        Map<String, AttributeDslModel> attrCodeToAttrDslModel = dslKit.getAttributes().stream()
            .collect(Collectors.toMap(AttributeDslModel::getCode, Function.identity()));

        boolean shouldInvalidate = false;
        for (Subject subject : savedKit.getSubjects()) {
            String subjectCode = subject.getCode();
            for (Attribute savedAttribute : subject.getAttributes()) {
                AttributeDslModel dslAttribute = attrCodeToAttrDslModel.get(savedAttribute.getCode());

                if (!savedAttribute.getTitle().equals(dslAttribute.getTitle()) ||
                    !savedAttribute.getDescription().equals(dslAttribute.getDescription()) ||
                    !subjectCode.equals(dslAttribute.getSubjectCode()) ||
                    savedAttribute.getIndex() != dslAttribute.getIndex() ||
                    savedAttribute.getWeight() != dslAttribute.getWeight()
                    ) {
                    Long newSubjectId = subjectCodeToSubjectId.get(dslAttribute.getSubjectCode());
                    updateAttribute(savedAttribute, newSubjectId, dslAttribute);
                }

                if (!subjectCode.equals(dslAttribute.getSubjectCode()) ||
                    savedAttribute.getWeight() != dslAttribute.getWeight()) {
                    shouldInvalidate = true;
                }
            }
        }

        Map<String, Long> attrCodeToAttrId = savedKit.getSubjects().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(Attribute::getCode, Attribute::getId));
        ctx.put(UpdateKitPersisterContext.KEY_ATTRIBUTES, attrCodeToAttrId);
        log.debug("Final attributes: {}", attrCodeToAttrId);

        return new UpdateKitPersisterResult(shouldInvalidate);
    }

    private void updateAttribute(Attribute savedAttribute, Long subjectId, AttributeDslModel dslAttribute) {
        UpdateAttributePort.Param param = new UpdateAttributePort.Param(savedAttribute.getId(),
            dslAttribute.getTitle(),
            dslAttribute.getIndex(),
            dslAttribute.getDescription(),
            dslAttribute.getWeight(),
            LocalDateTime.now(),
            subjectId);
        updateAttributePort.update(param);
        log.debug("Attribute[id={}, code={}] updated", savedAttribute.getId(), savedAttribute.getCode());
    }
}
