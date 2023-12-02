package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class AttributeUpdateKitPersister implements UpdateKitPersister {


    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {

        Map<String, Long> collect = savedKit.getSubjects().stream().collect(Collectors.toMap(Subject::getCode, Subject::getId));
        Map<String, List<Attribute>> codeToAttributes = savedKit.getSubjects().stream().collect(Collectors.toMap(Subject::getCode, Subject::getAttributes));


        savedKit.getSubjects().stream().map(Subject::getAttributes).flatMap(Collection::stream).forEach(e -> {

        });

        dslKit.getAttributes().forEach(e -> {
            String subjectCode = e.getSubjectCode();
            Long subjectId = collect.get(subjectCode);
            codeToAttributes.get(subjectCode);

        });
        return new UpdateKitPersisterResult(false);
    }

}
