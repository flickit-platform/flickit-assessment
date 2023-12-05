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

        boolean shouldInvalidated = false;

        Map<String, Long> subjectCodeToSubjectId = savedKit.getSubjects().stream()
            .collect(Collectors.toMap(Subject::getCode, Subject::getId));

        Map<String, AttributeDslModel> attrCodeToAttrDslModel = dslKit.getAttributes().stream()
            .collect(Collectors.toMap(AttributeDslModel::getCode, Function.identity()));

        for (Subject subject : savedKit.getSubjects()) {
            String subjectCode = subject.getCode();
            for (Attribute attribute : subject.getAttributes()) {

                AttributeDslModel attributeDslModel = attrCodeToAttrDslModel.get(attribute.getCode());


                if (!attribute.getTitle().equals(attributeDslModel.getTitle()) ||
                    !attribute.getDescription().equals(attributeDslModel.getDescription()) ||
                    !subjectCode.equals(attributeDslModel.getSubjectCode()) ||
                    attribute.getIndex() != attributeDslModel.getIndex() ||
                    attribute.getWeight() != attributeDslModel.getWeight()
                    ) {

                    updateAttributePort.update(toUpdatePram(attribute.getId(),
                        subjectCodeToSubjectId.get(attributeDslModel.getSubjectCode()),
                        attributeDslModel));
                    log.debug("Attribute[id = {}, code = {}] updated!", attribute.getId(), attribute.getCode());
                }

                if (!subjectCode.equals(attributeDslModel.getSubjectCode()) ||
                    attribute.getWeight() != attributeDslModel.getWeight()) {
                    shouldInvalidated = true;
                }
            }
        }


        Map<String, Long> attrCodeToAttrId = savedKit.getSubjects().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(Attribute::getCode, Attribute::getId));
        ctx.put(UpdateKitPersisterContext.KEY_ATTRIBUTES, attrCodeToAttrId);
        log.debug("Final attributes: {}", attrCodeToAttrId);

        return new UpdateKitPersisterResult(shouldInvalidated);
    }

    private UpdateAttributePort.Param toUpdatePram(long id, Long subjectId, AttributeDslModel attributeDslModel) {
        return new UpdateAttributePort.Param(id,
            attributeDslModel.getTitle(),
            attributeDslModel.getIndex(),
            attributeDslModel.getDescription(),
            attributeDslModel.getWeight(),
            LocalDateTime.now(),
            subjectId);
    }
}
