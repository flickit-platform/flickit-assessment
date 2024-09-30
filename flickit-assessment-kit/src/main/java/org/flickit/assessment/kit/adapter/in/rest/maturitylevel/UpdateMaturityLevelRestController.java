package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateMaturityLevelRestController {

    private final UpdateMaturityLevelUseCase useCase;
    private final UserContext userContext;

    @PutMapping("assessment-kits/{kitId}/maturity-levels/{maturityLevelId}")
    public ResponseEntity<Void> updateMaturityLevel(@PathVariable Long kitId, @PathVariable Long maturityLevelId, @RequestBody UpdateMaturityLevelRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.updateMaturityLevel(toParam(kitId, maturityLevelId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitId, Long maturityLevelId, UpdateMaturityLevelRequestDto requestDto, UUID currentUserId) {
        return new Param(maturityLevelId, kitId, requestDto.title(), requestDto.index(), requestDto.description(), requestDto.value(), currentUserId);
    }
}
