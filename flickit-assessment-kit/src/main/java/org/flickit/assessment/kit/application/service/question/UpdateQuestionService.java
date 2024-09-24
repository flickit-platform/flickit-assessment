package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQuestionService implements UpdateQuestionUseCase {

    private final UpdateQuestionPort updateQuestionPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public void updateQuestion(Param param) {
        AssessmentKit kit = loadAssessmentKitPort.load(param.getKitId());
        Long kitVersionId = kit.getKitVersionId();
        long expertGroupId = kit.getExpertGroupId();
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateQuestionPort.update(new UpdateQuestionPort.Param(param.getQuestionId(),
            kitVersionId,
            param.getTitle(),
            param.getIndex(),
            param.getHint(),
            param.getMayNotBeApplicable(),
            param.getAdvisable(),
            LocalDateTime.now(),
            param.getCurrentUserId()));
    }
}
