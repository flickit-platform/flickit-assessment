package org.flickit.assessment.kit.adapter.in.rest.kittag;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.KitTag;
import org.flickit.assessment.kit.application.port.in.kittag.GetKitTagListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetKitTagListRestController {

    private final GetKitTagListUseCase useCase;

    @GetMapping("/assessment-kit-tags")
    public ResponseEntity<PaginatedResponse<KitTag>> getKitTagList(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<KitTag> kitTagList = useCase.getKitTagList(toParam(page, size));
        return new ResponseEntity<>(kitTagList, HttpStatus.OK);
    }

    private GetKitTagListUseCase.Param toParam(int page, int size) {
        return new GetKitTagListUseCase.Param(page, size);
    }
}
