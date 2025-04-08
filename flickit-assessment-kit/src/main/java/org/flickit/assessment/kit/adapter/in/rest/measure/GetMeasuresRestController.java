package org.flickit.assessment.kit.adapter.in.rest.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.measure.GetMeasuresUseCase;
import org.flickit.assessment.kit.application.port.in.measure.GetMeasuresUseCase.MeasureListItem;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetMeasuresRestController {

    private final GetMeasuresUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/measures")
    public ResponseEntity<PaginatedResponse<GetMeasuresResponseDto>> getMeasures(@PathVariable("kitVersionId") Long kitVersionId,
                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "20") int size) {
        UUID currentUserId = userContext.getUser().id();
        var measures = useCase.getMeasures(toParam(kitVersionId, currentUserId, page, size));
        var response = toResponse(measures);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private static GetMeasuresUseCase.Param toParam(Long kitVersionId, UUID currentUserId, int page, int size) {
        return new GetMeasuresUseCase.Param(kitVersionId, currentUserId, page, size);
    }

    @NotNull
    private static PaginatedResponse<GetMeasuresResponseDto> toResponse(PaginatedResponse<MeasureListItem> measures) {
        List<GetMeasuresResponseDto> items = measures.getItems().stream()
            .map(e -> new GetMeasuresResponseDto(e.measure().getId(),
                e.measure().getTitle(),
                e.measure().getIndex(),
                e.measure().getDescription(),
                e.questionsCount()))
            .toList();

        return new PaginatedResponse<>(items,
            measures.getPage(),
            measures.getSize(),
            measures.getSort(),
            measures.getOrder(),
            measures.getTotal());
    }
}
