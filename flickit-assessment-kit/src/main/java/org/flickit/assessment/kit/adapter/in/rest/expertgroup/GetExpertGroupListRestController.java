package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class GetExpertGroupListRestController {

    private final GetExpertGroupListUseCase useCase;

    @GetMapping("/expert-groups")
    public ResponseEntity<PaginatedResponse<GetExpertGroupListUseCase.ExpertGroupListItem>> getExpertGroupList(@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page) {
        var expertGroupList = useCase.getExpertGroupList(toParam(size, page));
        return new ResponseEntity<>(expertGroupList, HttpStatus.OK);
    }

    private GetExpertGroupListUseCase.Param toParam(int size, int page) {
        return new GetExpertGroupListUseCase.Param(size, page);
    }
}
