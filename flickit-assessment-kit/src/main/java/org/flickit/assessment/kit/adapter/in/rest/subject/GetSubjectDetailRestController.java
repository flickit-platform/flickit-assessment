package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSubjectDetailRestController {

    private final GetSubjectDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits/{kitId}/details/subjects/{subjectId}")
    public ResponseEntity<GetSubjectDetailResponseDto> getDslDownloadLink(@PathVariable("kitId") Long kitId,
                                                                          @PathVariable("subjectId") Long subjectId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getSubjectDetail(toParam(kitId, subjectId, currentUserId));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.OK);
    }

    private Param toParam(Long kitId, Long subjectId, UUID currentUserId) {
        return new Param(kitId, subjectId, currentUserId);
    }

    private GetSubjectDetailResponseDto toResponseDto(Result result) {
        return new GetSubjectDetailResponseDto(result.questionCount(), result.description(), result.attributes());
    }
}
