package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentUniqueConstraintPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentcolor.CheckAssessmentColorExistencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.application.service.exception.UniqueConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand.PROP_TITLE;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_COLOR_ID_NOT_FOUND_MESSAGE;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_ASSESSMENT_TITLE_SPACE_ID_UNIQUE_CONSTRAINT_VIOLATION_MESSAGE;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final CreateAssessmentPort createAssessmentPort;
    private final CheckAssessmentColorExistencePort checkColorExistencePort;
    private final CheckAssessmentUniqueConstraintPort checkUniqueConstraintPort;

    @Override
    public UUID createAssessment(CreateAssessmentCommand command) {
        validateCommand(command);
        CreateAssessmentPort.Param param = toParam(command);
        validateParam(param);
        return createAssessmentPort.persist(param);
    }

    private void validateCommand(CreateAssessmentCommand command) {
        checkTitleAndSpaceIdUniqueConstraint(command.getTitle(), command.getSpaceId());
        checkColorIdExistence(command.getColorId());
    }

    private void validateParam(CreateAssessmentPort.Param param) {
        checkCodeAndSpaceIdUniqueConstraint(param.code(), param.spaceId());
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

    private void checkTitleAndSpaceIdUniqueConstraint(String title, Long spaceId) {
        boolean alreadyExist = checkUniqueConstraintPort.checkTitleAndSpaceIdUniqueConstraint(title, spaceId);
        if (alreadyExist) {
            throw new UniqueConstraintViolationException(PROP_TITLE, CREATE_ASSESSMENT_TITLE_SPACE_ID_UNIQUE_CONSTRAINT_VIOLATION_MESSAGE);
        }
    }

    private void checkCodeAndSpaceIdUniqueConstraint(String code, Long spaceId) {
        boolean alreadyExist = checkUniqueConstraintPort.checkCodeAndSpaceIdUniqueConstraint(code, spaceId);
        if (alreadyExist) {
            throw new UniqueConstraintViolationException(PROP_TITLE, CREATE_ASSESSMENT_TITLE_SPACE_ID_UNIQUE_CONSTRAINT_VIOLATION_MESSAGE);
        }
    }

}
