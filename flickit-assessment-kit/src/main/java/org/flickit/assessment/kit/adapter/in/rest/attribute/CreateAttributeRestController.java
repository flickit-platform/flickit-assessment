package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.CreateAttributeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAttributeRestController {

    private final CreateAttributeUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/attributes")
    public ResponseEntity<CreateAttributeResponseDto> createAttribute(@PathVariable("kitVersionId") Long kitVersionId,
                                                                      @RequestBody CreateAttributeRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        long attributeId = useCase.createAttribute(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(new CreateAttributeResponseDto(attributeId), HttpStatus.CREATED);
    }

    private CreateAttributeUseCase.Param toParam(Long kitVersionId,
                                                 CreateAttributeRequestDto requestDto,
                                                 UUID currentUserId) {
        return new CreateAttributeUseCase.Param(kitVersionId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            requestDto.weight(),
            requestDto.subjectId(),
            currentUserId);
    }
}
