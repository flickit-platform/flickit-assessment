package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttributeDetailRestController {

    private final GetAttributeDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits/{kitId}/details/attributes/{attributeId}")
    public ResponseEntity<GetAttributeDetailResponseDto> getAttributeDetail(@PathVariable("kitId") Long kitId,
                                                                            @PathVariable("attributeId") Long attributeId) {
        var currentUserId = userContext.getUser().id();
        var response = useCase.getAttributeDetail(toParam(kitId, attributeId, currentUserId));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.OK);
    }

    private Param toParam(Long kitId, Long subjectId, UUID currentUserId) {
        return new Param(kitId, subjectId, currentUserId);
    }

    private GetAttributeDetailResponseDto toResponseDto(Result result) {
        return new GetAttributeDetailResponseDto(result.id(),
            result.index(),
            result.title(),
            result.questionCount(),
            result.weight(),
            result.description(),
            result.maturityLevels());
    }
}
