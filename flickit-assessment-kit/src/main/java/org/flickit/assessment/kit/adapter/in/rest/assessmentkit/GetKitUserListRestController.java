package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetKitUserListRestController {

    private final GetKitUserListUseCase useCase;

    @GetMapping("assessment-kits/{kitId}/users")
    public ResponseEntity<PaginatedResponse<GetKitUserListUseCase.KitUserListItem>> getKitUserList(
        @PathVariable("kitId") Long kitId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size) {
        var userList = useCase.getKitUserList(toParam(kitId, page, size));
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    private GetKitUserListUseCase.Param toParam(Long kitId, int page, int size) {
        return new GetKitUserListUseCase.Param(kitId, page, size);
    }


}
