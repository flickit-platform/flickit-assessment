package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_ANSWER_RANGES;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerRangeCreateKitPersister implements CreateKitPersister {

    private final CreateAnswerRangePort createAnswerRangePort;
    private final CreateAnswerOptionPort createAnswerOptionPort;

    @Override
    public int order() {
        return 5;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx,
                        AssessmentKitDslModel dslKit,
                        Long kitVersionId,
                        UUID currentUserId) {
        List<AnswerRangeDslModel> answerRangeModels = dslKit.getAnswerRanges();
        var params = answerRangeModels.stream()
            .map(e -> new CreateAnswerRangePort.Param(kitVersionId,
                e.getTitle(),
                e.getCode(),
                true,
                currentUserId))
            .toList();

        Map<String, Long> codeToId = createAnswerRangePort.persistAll(params);
        ctx.put(KEY_ANSWER_RANGES, codeToId);
        log.debug("Final Answer Ranges: {}", codeToId);

        var codeToAnswerOptionModels = answerRangeModels.stream()
            .collect(Collectors.toMap(AnswerRangeDslModel::getCode, AnswerRangeDslModel::getAnswerOptions));

        var answerOptionParams = codeToId.entrySet().stream()
            .flatMap(entry -> codeToAnswerOptionModels.get(entry.getKey()).stream()
                .map(x -> new CreateAnswerOptionPort.Param(
                    x.getCaption(),
                    x.getIndex(),
                    entry.getValue(),
                    x.getValue(),
                    kitVersionId,
                    currentUserId)))
            .toList();


        createAnswerOptionPort.persistAll(answerOptionParams);
    }
}
