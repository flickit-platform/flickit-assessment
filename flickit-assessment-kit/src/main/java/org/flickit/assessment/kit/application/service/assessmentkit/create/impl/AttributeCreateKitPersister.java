package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_ATTRIBUTES;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_SUBJECTS;

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
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId, UUID currentUserId) {
        List<AttributeDslModel> dslAttributes = dslKit.getAttributes();

        Map<String, Long> savedSubjectCodesMap = ctx.get(KEY_SUBJECTS);

        Map<String, Long> codeToPersistedAttributeIds = new HashMap<>();
        dslAttributes.forEach(a -> {
            Attribute attribute = createAttribute(a, savedSubjectCodesMap.get(a.getSubjectCode()), kitId, currentUserId);
            codeToPersistedAttributeIds.put(a.getCode(), attribute.getId());
        });

        ctx.put(KEY_ATTRIBUTES, codeToPersistedAttributeIds);
        log.debug("Final attributes: {}", codeToPersistedAttributeIds);
    }

    private Attribute createAttribute(AttributeDslModel dslAttribute, Long subjectId, Long kitId, UUID currentUserId) {
        Attribute attribute = new Attribute(
            null,
            dslAttribute.getCode(),
            dslAttribute.getTitle(),
            dslAttribute.getIndex(),
            dslAttribute.getDescription(),
            dslAttribute.getWeight(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        Long persistedAttributeId = createAttributePort.persist(attribute, subjectId, kitId, currentUserId);
        log.debug("Attribute[id={}, code={}] created.", persistedAttributeId, attribute.getCode());

        return new Attribute(
            persistedAttributeId,
            attribute.getCode(),
            attribute.getTitle(),
            attribute.getIndex(),
            attribute.getDescription(),
            attribute.getWeight(),
            attribute.getCreationTime(),
            attribute.getLastModificationTime()
        );
    }
}
