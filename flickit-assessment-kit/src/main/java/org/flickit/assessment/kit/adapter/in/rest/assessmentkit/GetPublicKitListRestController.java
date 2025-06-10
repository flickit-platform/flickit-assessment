package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublicKitListUseCase.KitListItem;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublicKitListUseCase.Param;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublicKitListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class GetPublicKitListRestController {

    private final GetPublicKitListUseCase useCase;

    @GetMapping("/public/assessment-kits")
    public ResponseEntity<PaginatedResponse<KitListItem>> getPublicKitList(
        @RequestParam(required = false) Set<String> langs,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "0") int page) {
        var response = useCase.getPublicKitList(toParam(langs, page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(Set<String> langs, int page, int size) {
        return new Param(langs, page, size);
    }
}
