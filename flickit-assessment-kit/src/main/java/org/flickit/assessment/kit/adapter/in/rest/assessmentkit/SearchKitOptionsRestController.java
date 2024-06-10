package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.SearchKitOptionsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SearchKitOptionsRestController {

    private final UserContext currentUser;
    private final SearchKitOptionsUseCase useCase;

    @GetMapping("/assessment-kits/options/search")
    public ResponseEntity<PaginatedResponse<SearchKitOptionsUseCase.KitListItem>> searchKitOptions(
        @RequestParam(value = "query", defaultValue = "", required = false) String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        UUID currentUserId = currentUser.getUser().id();
        var response = useCase.searchKitOptions(toParam(query, page, size, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private SearchKitOptionsUseCase.Param toParam(String query, int page, int size, UUID currentUserId) {
        return new SearchKitOptionsUseCase.Param(query, page, size, currentUserId);
    }
}
