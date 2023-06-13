package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentcolor.CheckAssessmentColorExistencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_COLOR_ID_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;
    private final CheckAssessmentColorExistencePort checkColorExistencePort;

    @Override
    public UUID createAssessment(CreateAssessmentCommand command) {
        validateCommand(command);
        CreateAssessmentPort.Param param = toParam(command);
        return createAssessmentPort.persist(param);
    }

    private void validateCommand(CreateAssessmentCommand command) {
        checkColorIdExistence(command.getColorId());
    }

    private CreateAssessmentPort.Param toParam(CreateAssessmentCommand command) {
        String code = generateSlugCode(command.getTitle());
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastModificationTime = LocalDateTime.now();

        return new CreateAssessmentPort.Param(
            command.getTitle(),
            command.getAssessmentKitId(),
            command.getColorId(),
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

    private void checkColorIdExistence(Long colorId) {
        if (colorId == null) {
            return;
        }
        boolean isColorIdExist = checkColorExistencePort.isColorIdExist(colorId);
        if (!isColorIdExist)
            throw new ResourceNotFoundException(CREATE_ASSESSMENT_COLOR_ID_NOT_FOUND_MESSAGE);
    }
}
