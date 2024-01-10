package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.CreateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_SUBJECTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeCreateKitPersister implements CreateKitPersister {

    private final CreateAttributePort createAttributePort; // TODO

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId) {
        List<AttributeDslModel> dslAttributes = dslKit.getAttributes();

        Map<String, Subject> savedSubjectCodesMap = ctx.get(KEY_SUBJECTS);

        Map<String, List<AttributeDslModel>> subjectCodeToAttributeMap = dslAttributes.stream()
            .collect(groupingBy(AttributeDslModel::getSubjectCode));

        for (Subject subject : savedSubjectCodesMap.values()) {
            String subjectCode = subject.getCode();
            List<Attribute> subjectAttributes = subjectCodeToAttributeMap.get(subjectCode).stream().map(AttributeCreateKitPersister::toAttribute).toList();
            subject.setAttributes(subjectAttributes);
        }

        Map<String, Attribute> attrCodeToAttr = savedSubjectCodesMap.values().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .collect(toMap(Attribute::getCode, a -> a));
        ctx.put(UpdateKitPersisterContext.KEY_ATTRIBUTES, attrCodeToAttr);
        log.debug("Final attributes: {}", attrCodeToAttr);
    }

    private static Attribute toAttribute(AttributeDslModel a) {
        return new Attribute(null, a.getCode(), a.getTitle(), a.getIndex(), a.getDescription(), a.getWeight(), LocalDateTime.now(), LocalDateTime.now());
    }
}
