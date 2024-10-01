package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase.SubjectOrderParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSubjectIndexRestController {

    private final UpdateSubjectIndexUseCase useCase;
    private final UserContext userContext;

    @PutMapping("kit-versions/{kitVersionId}/subjects/change-orders")
    public ResponseEntity<Void> updateSubjectIndex(@PathVariable("kitVersionId") Long kitVersionId,
                                                   @RequestBody UpdateSubjectIndexRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateSubjectIndex(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId,
                          UpdateSubjectIndexRequestDto requestDto,
                          UUID currentUserId) {
        var orders = requestDto.orders().stream()
            .map(s -> new SubjectOrderParam(s.id(), s.order()))
            .toList();
        return new Param(kitVersionId,
            orders,
            currentUserId);
    }
}
