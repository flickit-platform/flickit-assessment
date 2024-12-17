package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDslUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DSL_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDslService implements GetKitDslUseCase {

    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAnswerRangesPort loadAnswerRangesPort;

    @Override
    public Result export(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(kit.getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Long activeVersionId = kit.getActiveVersionId();
        if (activeVersionId == null)
            throw new ValidationException(GET_KIT_DSL_NOT_ALLOWED);

        var questionnaires = loadQuestionnairesPort.loadDslModels(activeVersionId);
        var attributes = loadAttributesPort.loadDslModels(activeVersionId);
        var subjects = loadSubjectsPort.loadDslModels(activeVersionId);
        var maturityLevels = loadMaturityLevelsPort.loadDslModels(activeVersionId);
        var answerRanges = loadAnswerRangesPort.loadDslModels(activeVersionId);

        var assessmentKitDslModel = AssessmentKitDslModel.builder()
            .questionnaires(questionnaires)
            .attributes(attributes)
            .subjects(subjects)
            .answerRanges(answerRanges)
            .maturityLevels(maturityLevels)
            .build();

        return new Result(assessmentKitDslModel);
    }
}
