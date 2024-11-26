package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_ATTRIBUTES;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_SUBJECTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeCreateKitPersister implements CreateKitPersister {

    private final CreateAttributePort createAttributePort;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitVersionId, UUID currentUserId) {
        List<AttributeDslModel> dslAttributes = dslKit.getAttributes();

        Map<String, Long> savedSubjectCodesMap = ctx.get(KEY_SUBJECTS);

        Map<String, Long> codeToPersistedAttributeIds = new HashMap<>();
        dslAttributes.forEach(a -> {
            Long persistedAttributeId = createAttribute(a, savedSubjectCodesMap.get(a.getSubjectCode()), kitVersionId, currentUserId);
            codeToPersistedAttributeIds.put(a.getCode(), persistedAttributeId);
        });

        ctx.put(KEY_ATTRIBUTES, codeToPersistedAttributeIds);
        log.debug("Final attributes: {}", codeToPersistedAttributeIds);
    }

    private Long createAttribute(AttributeDslModel dslAttribute, Long subjectId, Long kitVersionId, UUID currentUserId) {
        Attribute attribute = new Attribute(
            null,
            dslAttribute.getCode(),
            dslAttribute.getTitle(),
            dslAttribute.getIndex(),
            dslAttribute.getDescription(),
            dslAttribute.getWeight(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            currentUserId,
            currentUserId
        );

        Long persistedAttributeId = createAttributePort.persist(attribute, subjectId, kitVersionId);
        log.debug("Attribute[id={}, code={}] created.", persistedAttributeId, attribute.getCode());

        return persistedAttributeId;
    }
}
