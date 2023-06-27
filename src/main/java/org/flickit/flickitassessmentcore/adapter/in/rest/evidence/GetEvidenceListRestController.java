package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL;

@RequiredArgsConstructor
@RestController
public class GetEvidenceListRestController {

    private final GetEvidenceListUseCase useCase;

    @GetMapping("/evidences")
    public ResponseEntity<GetEvidenceListResponseDto> getEvidenceList(
        @RequestParam("questionId")
        @NotNull(message = GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL)
        Long questionId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        GetEvidenceListUseCase.Result result = useCase.getEvidenceList(new GetEvidenceListUseCase.Param(questionId, size, page));
        return new ResponseEntity<>(new GetEvidenceListResponseDto(result.evidences()), HttpStatus.OK);
    }
}
