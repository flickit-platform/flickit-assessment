package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemListUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemListPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.GET_ADVICE_ITEM_LIST_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdviceItemListServiceTest {

    @InjectMocks
    private GetAdviceItemListService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAdviceItemListPort loadAdviceItemListPort;

    @Test
    void testGetAdviceItemList_whenUserDoesNotHaveAccess_thenThrowAccessDeniedException() {
        var param = createParam(GetAdviceItemListUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAdviceItems(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAdviceItemList_whenThereIsNotAssessmentResult_thenResourceNotFoundException() {
        var param = createParam(GetAdviceItemListUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAdviceItems(param));
        assertEquals(GET_ADVICE_ITEM_LIST_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetAdviceItemList_whenParametersAreValid_thenReturnsPaginatedAdviceItemList() {
        var param = createParam(GetAdviceItemListUseCase.Param.ParamBuilder::build);
        var assessmentResult = new AssessmentResult(UUID.randomUUID(), 123);
        var items = List.of(
            new AdviceItem(UUID.randomUUID(), "title1", UUID.randomUUID(), "description1",
                CostLevel.HIGH, PriorityLevel.LOW, ImpactLevel.MEDIUM, LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID()),
            new AdviceItem(UUID.randomUUID(), "title2", UUID.randomUUID(), "description2",
                CostLevel.MEDIUM, PriorityLevel.MEDIUM, ImpactLevel.HIGH, LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID()));

        var expectedResult = new PaginatedResponse<>(items, param.getPage(), param.getSize(), "desc", "lastModificationTime", 2);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAdviceItemListPort.loadAdviceItemList(assessmentResult.getId(), param.getPage(), param.getSize())).thenReturn(expectedResult);

        var result = service.getAdviceItems(param);
        assertThat(result.getItems())
            .zipSatisfy(expectedResult.getItems(), (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getTitle(), actual.title());
                assertEquals(expected.getDescription(), actual.description());
                assertEquals(expected.getCost().getTitle(), actual.cost());
                assertEquals(expected.getPriority().getTitle(), actual.priority());
                assertEquals(expected.getImpact().getTitle(), actual.impact());
            });
        assertEquals(param.getPage(), result.getPage());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(expectedResult.getItems().size(), result.getTotal());
    }

    private GetAdviceItemListUseCase.Param createParam(Consumer<GetAdviceItemListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAdviceItemListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAdviceItemListUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
