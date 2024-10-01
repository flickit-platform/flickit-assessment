package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.maturitylevel.CreateMaturityLevelUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateMaturityLevelRestController {

    private final CreateMaturityLevelUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/maturity-levels")
    public ResponseEntity<CreateMaturityLevelResponseDto> createMaturityLevel(@PathVariable("kitVersionId") Long kitVersionId,
                                                                              @RequestBody CreateMaturityLevelRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        long levelId = useCase.createMaturityLevel(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(new CreateMaturityLevelResponseDto(levelId), HttpStatus.CREATED);
    }

    private static CreateMaturityLevelUseCase.Param toParam(Long kitVersionId,
                                                            CreateMaturityLevelRequestDto requestDto,
                                                            UUID currentUserId) {
        return new CreateMaturityLevelUseCase.Param(kitVersionId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            requestDto.value(),
            currentUserId);
    }
}
