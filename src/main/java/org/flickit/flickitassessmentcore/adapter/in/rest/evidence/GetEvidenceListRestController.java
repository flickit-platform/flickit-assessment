package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
public class GetEvidenceListRestController {

    private final GetEvidenceListUseCase useCase;

    @GetMapping("/evidences")
    public ResponseEntity<PaginatedResponse<EvidenceListItem>> getEvidenceList(
        @RequestParam(value = "questionId", required = false) // validated in the use-case param
        Long questionId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        PaginatedResponse<EvidenceListItem> result = useCase.getEvidenceList(toParam(questionId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetEvidenceListUseCase.Param toParam(Long questionId, int size, int page) {
        return new GetEvidenceListUseCase.Param(questionId, size, page);
    }
}
