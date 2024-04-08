package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetKitEditableInfoRestController {

    private final GetKitEditableInfoUseCase useCase;

    @GetMapping("/assessment-kits/get/{kitId}")
    public ResponseEntity<GetKitEditableInfoResponseDto> getKitEditableInfo(@PathVariable("itId") Long kitId) {
        var kitEditableInfo = useCase.getKitEditableInfo(toParam(kitId));
        return new ResponseEntity<>(toResponse(kitEditableInfo), HttpStatus.OK);
    }

    private GetKitEditableInfoUseCase.Param toParam(Long kitId) {
        return new GetKitEditableInfoUseCase.Param(kitId);
    }

    private GetKitEditableInfoResponseDto toResponse(GetKitEditableInfoUseCase.KitEditableInfo kitEditableInfo) {
        return new GetKitEditableInfoResponseDto(
            kitEditableInfo.id(),
            kitEditableInfo.title(),
            kitEditableInfo.summary(),
            kitEditableInfo.isActive(),
            kitEditableInfo.price(),
            kitEditableInfo.about(),
            kitEditableInfo.tags()
        );
    }
}
