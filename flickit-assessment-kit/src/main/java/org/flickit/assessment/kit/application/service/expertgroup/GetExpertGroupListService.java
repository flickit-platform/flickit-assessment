package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupListService implements GetExpertGroupListUseCase {

    private final LoadExpertGroupListPort loadExpertGroupListPort;

    @Override
    public PaginatedResponse<ExpertGroupListItemFinal> getExpertGroupList(Param param) {

        var pageResult = loadExpertGroupListPort.loadExpertGroupList(toParam(param.getPage(), param.getSize(), param.getCurrentUserId()));
        return PaginatedResponseUtil.mapPaginatedResponse(
            pageResult,
            expertGroupListItem -> {
                boolean isEditable = expertGroupListItem.ownerId().equals(param.getCurrentUserId());

                return new ExpertGroupListItemFinal(
                    expertGroupListItem.id(),
                    expertGroupListItem.title(),
                    expertGroupListItem.bio(),
                    expertGroupListItem.picture(),
                    expertGroupListItem.publishedKitsCount(),
                    expertGroupListItem.membersCount(),
                    expertGroupListItem.members().stream().limit(5).toList(),
                    isEditable
                );
            }
        );

    }

    private LoadExpertGroupListPort.Param toParam(int page, int size, UUID currentUserId) {
        return new LoadExpertGroupListPort.Param(page, size, currentUserId);
    }
}

@NoArgsConstructor
class PaginatedResponseUtil {

    public static <T, R> PaginatedResponse<R> mapPaginatedResponse(
        PaginatedResponse<T> input,
        Function<T, R> mapper
    ) {
        List<R> mappedItems = input.getItems().stream()
            .map(mapper)
            .toList();

        return new PaginatedResponse<>(
            mappedItems,
            input.getPage(),
            input.getSize(),
            input.getSort(),
            input.getOrder(),
            input.getTotal()
        );
    }
}
