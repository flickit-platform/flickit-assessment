package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase;
import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitSubjectDetailRestController {

    private final GetKitSubjectDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits/{kitId}/details/subjects/{subjectId}")
    public ResponseEntity<GetKitSubjectDetailResponseDto> getKitSubjectDetail(@PathVariable("kitId") Long kitId,
                                                                              @PathVariable("subjectId") Long subjectId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitSubjectDetail(toParam(kitId, subjectId, currentUserId));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.OK);
    }

    private Param toParam(Long kitId, Long subjectId, UUID currentUserId) {
        return new Param(kitId, subjectId, currentUserId);
    }

    private GetKitSubjectDetailResponseDto toResponseDto(Result result) {
        return new GetKitSubjectDetailResponseDto(result.questionsCount(), result.description(), result.attributes());
    }
}
