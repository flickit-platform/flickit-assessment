package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.AnswerOptionDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.AnswerRangeDslModelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnswerRangeCreateKitPersisterTest {

    @InjectMocks
    private AnswerRangeCreateKitPersister persister;

    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Captor
    private ArgumentCaptor<List<CreateAnswerOptionPort.Param>> answerOptionsPersistCaptor;

    @Test
    public void testOrder() {
        assertEquals(5, persister.order());
    }

    @Test
    public void testPersist_WhenInputsAreValid_ThenSaveAnswerRangeAndItsAnswerOptions() {
        long kitVersionId = 1;
        AnswerRange answerRangeR1 = AnswerRangeMother.createReusableAnswerRangeWithTwoOptions(1);
        var dslOptionsR1 = answerRangeR1.getAnswerOptions().stream()
            .map(e -> AnswerOptionDslModelMother.answerOptionDslModel(e.getIndex(),
                e.getTitle(),
                e.getValue()))
            .toList();
        AnswerRangeDslModel dslRangeR1 = AnswerRangeDslModelMother.domainToDslModel(answerRangeR1,
            b-> b.answerOptions(dslOptionsR1));
        AnswerRange answerRangeR2 = AnswerRangeMother.createReusableAnswerRangeWithTwoOptions(2);
        var dslOptionsR2 = answerRangeR2.getAnswerOptions().stream()
            .map(e -> AnswerOptionDslModelMother.answerOptionDslModel(e.getIndex(),
                e.getTitle(),
                e.getValue()))
            .toList();
        AnswerRangeDslModel dslRangeR2 = AnswerRangeDslModelMother.domainToDslModel(answerRangeR2,
            b-> b.answerOptions(dslOptionsR2));

        List<AnswerRangeDslModel> dslRanges = List.of(dslRangeR1, dslRangeR2);
        CreateKitPersisterContext context = new CreateKitPersisterContext();
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .answerRanges(dslRanges)
            .build();


        var currentUserId = UUID.randomUUID();
        var answerRangeR1ParamNoId = new CreateAnswerRangePort.Param(kitVersionId,
            answerRangeR1.getTitle(),
            answerRangeR1.getCode(),
            answerRangeR1.isReusable(),
            currentUserId);

        var answerRangeR2ParamNoId = new CreateAnswerRangePort.Param(kitVersionId,
            answerRangeR2.getTitle(),
            answerRangeR2.getCode(),
            answerRangeR2.isReusable(),
            currentUserId);

        Map<String, Long> codeToId = new HashMap<>();
        codeToId.put(answerRangeR1.getCode(), answerRangeR1.getId());
        codeToId.put(answerRangeR2.getCode(), answerRangeR2.getId());
        when(createAnswerRangePort.persistAll(List.of(answerRangeR1ParamNoId, answerRangeR2ParamNoId))).thenReturn(codeToId);
        doNothing().when(createAnswerOptionPort).persistAll(anyList());

        persister.persist(context, dslModel, kitVersionId, currentUserId);

        verify(createAnswerOptionPort).persistAll(answerOptionsPersistCaptor.capture());
        List<AnswerOption> rangeR1answerOptions = answerRangeR1.getAnswerOptions();
        List<AnswerOption> rangeR2AnswerOptions = answerRangeR2.getAnswerOptions();
        List<AnswerOption> options = new ArrayList<>();
        options.addAll(rangeR1answerOptions);
        options.addAll(rangeR2AnswerOptions);
        List<CreateAnswerOptionPort.Param> portParams = answerOptionsPersistCaptor.getValue();
        assertEquals(options.size(), portParams.size());
        for (int i = 0; i < portParams.size(); i++) {
            assertEquals(options.get(i).getAnswerRangeId(), portParams.get(i).answerRangeId());
            assertEquals(options.get(i).getTitle(), portParams.get(i).title());
            assertEquals(options.get(i).getIndex(), portParams.get(i).index());
            assertEquals(options.get(i).getValue(), portParams.get(i).value());
        }
    }
}
