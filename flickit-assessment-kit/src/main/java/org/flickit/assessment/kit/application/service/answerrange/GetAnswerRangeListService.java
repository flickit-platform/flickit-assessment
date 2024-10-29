package org.flickit.assessment.kit.application.service.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class GetAnswerRangeListService implements GetAnswerRangeListUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAnswerRangePort loadAnswerRangePort;
    private final LoadAnswerOptionPort loadAnswerOptionPort;

    @Override
    public PaginatedResponse<AnswerRange> getAnswerRangeList(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var paginatedAnswerRanges = loadAnswerRangePort.loadByKitVersionId(param.getKitVersionId(), param.getPage(), param.getSize());

        List<AnswerRange> ranges = new ArrayList<>();
        if(!paginatedAnswerRanges.getItems().isEmpty()) {
            var answerRanges = paginatedAnswerRanges.getItems();
            var answerOptions = loadAnswerOptionPort.loadByKitVersionId(param.getKitVersionId());
            ranges = getAnswerRangesWithAnswerOptions(answerRanges, answerOptions);
        }

        return new PaginatedResponse<>(ranges,
            paginatedAnswerRanges.getPage(),
            paginatedAnswerRanges.getSize(),
            paginatedAnswerRanges.getSort(),
            paginatedAnswerRanges.getOrder(),
            paginatedAnswerRanges.getTotal());
    }

    private static List<AnswerRange> getAnswerRangesWithAnswerOptions(List<AnswerRange> answerRanges, List<AnswerOption> answerOptions) {
        Map<Long, List<AnswerOption>> answerRangeIdToOptions = answerOptions.stream()
            .collect(groupingBy(AnswerOption::getAnswerRangeId));

        return answerRanges.stream()
            .map(answerRange -> new AnswerRange(
                answerRange.getId(),
                answerRange.getTitle(),
                answerRangeIdToOptions.getOrDefault(answerRange.getId(), List.of()).stream()
                    .map(option -> new AnswerOption(
                        option.getId(),
                        option.getTitle(),
                        option.getIndex(),
                        null,
                        null
                    )).toList()
            )).toList();
    }
}
