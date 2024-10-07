package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_SUBJECTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeUpdateKitPersister implements UpdateKitPersister {

    private final UpdateAttributePort updateAttributePort;
    private final CreateAttributePort createAttributePort;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
        Map<String, Long> subjectCodeToSubjectId = ctx.get(KEY_SUBJECTS);

        Map<String, AttributeDslModel> attrCodeToAttrDslModel = dslKit.getAttributes().stream()
            .collect(Collectors.toMap(AttributeDslModel::getCode, Function.identity()));

        Map<String, Attribute> savedAttrCodeToAttr = new HashMap<>();
        boolean isMajorUpdate = false;
        for (Subject subject : savedKit.getSubjects()) {
            String subjectCode = subject.getCode();
            for (Attribute savedAttribute : subject.getAttributes()) {
                savedAttrCodeToAttr.put(savedAttribute.getCode(), savedAttribute);
                AttributeDslModel dslAttribute = attrCodeToAttrDslModel.get(savedAttribute.getCode());

                if (!savedAttribute.getTitle().equals(dslAttribute.getTitle()) ||
                    !savedAttribute.getDescription().equals(dslAttribute.getDescription()) ||
                    !subjectCode.equals(dslAttribute.getSubjectCode()) ||
                    savedAttribute.getIndex() != dslAttribute.getIndex() ||
                    savedAttribute.getWeight() != dslAttribute.getWeight()
                ) {
                    Long newSubjectId = subjectCodeToSubjectId.get(dslAttribute.getSubjectCode());
                    updateAttribute(savedAttribute, newSubjectId, savedKit.getActiveVersionId(), dslAttribute, currentUserId);
                }

                if (!subjectCode.equals(dslAttribute.getSubjectCode()) ||
                    savedAttribute.getWeight() != dslAttribute.getWeight()) {
                    isMajorUpdate = true;
                }
            }
        }

        Set<String> addedAttributeCodes = attrCodeToAttrDslModel.keySet().stream()
            .filter(e -> !savedAttrCodeToAttr.containsKey(e))
            .collect(Collectors.toSet());

        if (!addedAttributeCodes.isEmpty())
            isMajorUpdate = true;

        Map<String, Long> savedNewAttrCodeToIdMap = new HashMap<>();
        addedAttributeCodes.forEach(code -> {
            Long subjectId = subjectCodeToSubjectId.get(attrCodeToAttrDslModel.get(code).getSubjectCode());
            Long persistedAttributeId = createAttribute(attrCodeToAttrDslModel.get(code), subjectId, savedKit.getActiveVersionId(), currentUserId);
            savedNewAttrCodeToIdMap.put(code, persistedAttributeId);
        });

        Map<String, Long> attrCodeToAttrId = savedKit.getSubjects().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(Attribute::getCode, Attribute::getId));
        attrCodeToAttrId.putAll(savedNewAttrCodeToIdMap);

        ctx.put(UpdateKitPersisterContext.KEY_ATTRIBUTES, attrCodeToAttrId);
        log.debug("Final attributes: {}", attrCodeToAttrId);

        return new UpdateKitPersisterResult(isMajorUpdate);
    }

    private void updateAttribute(Attribute savedAttribute,
                                 Long subjectId,
                                 long kitVersionId,
                                 AttributeDslModel dslAttribute,
                                 UUID updatedBy) {
        UpdateAttributePort.Param param = new UpdateAttributePort.Param(savedAttribute.getId(),
            kitVersionId,
            dslAttribute.getTitle(),
            dslAttribute.getIndex(),
            dslAttribute.getDescription(),
            dslAttribute.getWeight(),
            LocalDateTime.now(),
            updatedBy,
            subjectId);
        updateAttributePort.update(param);
        log.debug("Attribute[id={}, code={}] updated", savedAttribute.getId(), savedAttribute.getCode());
    }

    private long createAttribute(AttributeDslModel dslAttribute, long subjectId, long kitVersionId, UUID createdBy) {
        Attribute attribute = new Attribute(
            null,
            dslAttribute.getCode(),
            dslAttribute.getTitle(),
            dslAttribute.getIndex(),
            dslAttribute.getDescription(),
            dslAttribute.getWeight(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            createdBy,
            createdBy
        );
        long persistedAttributeId = createAttributePort.persist(attribute, subjectId, kitVersionId);
        log.debug("Attribute[id={}, code={}] created", persistedAttributeId, dslAttribute.getCode());
        return persistedAttributeId;
    }

}
