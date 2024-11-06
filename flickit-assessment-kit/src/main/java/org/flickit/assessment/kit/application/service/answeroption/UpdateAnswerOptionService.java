package org.flickit.assessment.kit.application.service.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.UpdateAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAnswerOptionService implements UpdateAnswerOptionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateAnswerOptionPort updateAnswerOptionPort;

    @Override
    public void updateAnswerOption(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        UpdateAnswerOptionPort.Param updatePortParam = toParam(param.getAnswerOptionId(),
                param.getKitVersionId(),
                param.getIndex(),
                param.getTitle(),
                param.getValue(),
                param.getCurrentUserId());
        updateAnswerOptionPort.update(updatePortParam);
    }

    private UpdateAnswerOptionPort.Param toParam(long answerOptionId,
                                                 long kitVersionId,
                                                 int index,
                                                 String title,
                                                 double value,
                                                 UUID currentUserId) {
        return new UpdateAnswerOptionPort.Param(answerOptionId,
                kitVersionId,
                index,
                title,
                value,
                LocalDateTime.now(),
                currentUserId);
    }
}
