package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetKitDslDownloadLinkRestController {

    private final GetKitDownloadLinkUseCase useCase;

    @GetMapping("/assessment-kits/{kitId}/dsl-download-link")
    public ResponseEntity<GetKitDslDownloadLinkResponseDto> getDslDownloadLink(@PathVariable("kitId") Long kitId) {
        var response = useCase.getKitLink(toParam(kitId));
        GetKitDslDownloadLinkResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetKitDownloadLinkUseCase.Param toParam(Long kitId) {
        return new GetKitDownloadLinkUseCase.Param(kitId);
    }

    private GetKitDslDownloadLinkResponseDto toResponseDto(String response) {
        return new GetKitDslDownloadLinkResponseDto(response);
    }
}
