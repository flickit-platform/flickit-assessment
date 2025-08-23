package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.domain.KitMetadata;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateKitInfoRestController {

    private final UpdateKitInfoUseCase useCase;
    private final UserContext userContext;

    @PatchMapping("/assessment-kits/{kitId}")
    public ResponseEntity<Void> updateKitInfo(@PathVariable("kitId") Long kitId,
                                              @RequestBody UpdateKitInfoRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.updateKitInfo(toParam(kitId, request, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitId, UpdateKitInfoRequestDto request, UUID currentUserId) {
        return new Param(
            kitId,
            request.title(),
            request.summary(),
            request.lang(),
            request.published(),
            request.isPrivate(),
            request.price(),
            request.about(),
            request.tags(),
            request.translations(),
            request.removeTranslations(),
            toKitMetadata(request.metadata()),
            request.removeMetadata(),
            currentUserId
        );

    }

    private KitMetadata toKitMetadata(UpdateKitInfoRequestDto.MetadataDto metadataDto) {
        KitMetadata metadata = null;

        if (metadataDto != null) {
            var goal = metadataDto.goal() != null && !metadataDto.goal().isBlank() ? metadataDto.goal().strip() : null;
            var context = metadataDto.context() != null && !metadataDto.context().isBlank() ? metadataDto.context().strip() : null;
            metadata = new KitMetadata(goal, context);
        }
        return metadata;
    }
}
