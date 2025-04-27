package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.SearchKitOptionsUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.SearchKitOptionsPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchKitOptionsService implements SearchKitOptionsUseCase {

    private final SearchKitOptionsPort port;
    private final LoadKitLanguagesPort loadKitLanguagesPort;

    @Override
    public PaginatedResponse<KitListItem> searchKitOptions(Param param) {
        var paginatedResponse = port.searchKitOptions(toParam(param.getQuery(),
            param.getPage(),
            param.getSize(),
            param.getCurrentUserId()));

        var kitIds = paginatedResponse.getItems().stream()
            .map(AssessmentKit::getId)
            .toList();

        var idToLanguagesMap = loadKitLanguagesPort.loadByKitIds(kitIds);

        var items = paginatedResponse.getItems().stream()
            .map(e -> new KitListItem(e.getId(),
                e.getTitle(),
                e.isPrivate(),
                KitListItem.Language.of(e.getLanguage()),
                idToLanguagesMap.get(e.getId()).stream()
                    .map(KitListItem.Language::of)
                    .toList()))
            .toList();

        return new PaginatedResponse<>(
            items,
            paginatedResponse.getPage(),
            paginatedResponse.getSize(),
            paginatedResponse.getSort(),
            paginatedResponse.getOrder(),
            paginatedResponse.getTotal());
    }

    private SearchKitOptionsPort.Param toParam(String query, int page, int size, UUID currentUserId) {
        return new SearchKitOptionsPort.Param(query, page, size, currentUserId);
    }
}
