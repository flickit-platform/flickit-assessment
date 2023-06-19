package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.domain.AssessmentColor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;

    @Override
    public UUID createAssessment(CreateAssessmentCommand command) {
        CreateAssessmentPort.Param param = toParam(command);
        return createAssessmentPort.persist(param);
    }

    private CreateAssessmentPort.Param toParam(CreateAssessmentCommand command) {
        String code = generateSlugCode(command.getTitle());
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastModificationTime = LocalDateTime.now();

        return new CreateAssessmentPort.Param(
            command.getTitle(),
            command.getAssessmentKitId(),
            getValidColorId(command.getColorId()),
            command.getSpaceId(),
            code,
            creationTime,
            lastModificationTime
        );
    }

    private String generateSlugCode(String title) {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\\s+", "-");
    }

    private int getValidColorId(Integer colorId) {
        if (colorId == null || !AssessmentColor.isValidId(colorId))
            return AssessmentColor.getDefault().getId();
        return colorId;
    }
}
