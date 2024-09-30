package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.CreateSubjectUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateSubjectRestController {

    private final CreateSubjectUseCase useCase;
    private final UserContext userContext;

    @PostMapping("kit-versions/{kitVersionId}/subjects")
    public ResponseEntity<CreateSubjectResponseDto> createSubject(@PathVariable("kitVersionId") Long kitVersionId,
                                                                  @RequestBody CreateSubjectRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        long subjectId = useCase.createSubject(toParam(kitVersionId, currentUserId, requestDto));
        return new ResponseEntity<>(new CreateSubjectResponseDto(subjectId), HttpStatus.CREATED);
    }

    private CreateSubjectUseCase.Param toParam(Long kitVersionId,
                                               UUID currentUserId,
                                               CreateSubjectRequestDto requestDto) {
        return new CreateSubjectUseCase.Param(kitVersionId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            requestDto.weight(),
            currentUserId);
    }
}
