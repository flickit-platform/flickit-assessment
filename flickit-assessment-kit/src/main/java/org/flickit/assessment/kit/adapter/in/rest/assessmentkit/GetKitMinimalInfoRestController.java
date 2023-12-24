package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;


import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetKitMinimalInfoRestController {

    private final GetKitMinimalInfoUseCase useCase;

    @GetMapping("/assessment-kits/{kitId}/min-info")
    public ResponseEntity<GetKitMinimalInfoResponseDto> getKitMinimalInfo(@PathVariable("kitId") Long kitId) {
        var response = useCase.getKitMinimalInfo(toParam(kitId));
        GetKitMinimalInfoResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetKitMinimalInfoResponseDto toResponseDto(GetKitMinimalInfoUseCase.Result response) {
        return new GetKitMinimalInfoResponseDto(response.id(), response.title(), response.minimalExpertGroup());
    }

    private GetKitMinimalInfoUseCase.Param toParam(Long kitId) {
        return new GetKitMinimalInfoUseCase.Param(kitId);
    }
}
