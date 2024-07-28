package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectByWizardUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSubjectByWizardRestController {

    private final UpdateSubjectByWizardUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessment-kits/{kitId}/subjects/{subjectId}")
    public ResponseEntity<Void> updateSubjectByWizard(@PathVariable("kitId") Long kitId,
                                                      @PathVariable("subjectId") Long subjectId,
                                                      @RequestBody UpdateSubjectByWizardRequestDto updateSubjectByWizardRequestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateSubject(toParam(kitId, subjectId, currentUserId, updateSubjectByWizardRequestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateSubjectByWizardUseCase.Param toParam(Long kitId,
                                                       Long subjectId,
                                                       UUID currentUserId,
                                                       UpdateSubjectByWizardRequestDto updateSubjectByWizardRequestDto) {

        return new UpdateSubjectByWizardUseCase.Param(kitId,
            subjectId,
            updateSubjectByWizardRequestDto.index(),
            updateSubjectByWizardRequestDto.title(),
            updateSubjectByWizardRequestDto.description(),
            updateSubjectByWizardRequestDto.weight(),
            currentUserId);
    }
}
