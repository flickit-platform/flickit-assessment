package org.flickit.assessment.core.application.service.insight.subject;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.Param;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSubjectInsightsHelperTest {

    @InjectMocks
    private CreateSubjectInsightsHelper helper;

    @Mock
    private LoadSubjectValuePort loadSubjectValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    private final AssessmentResult assessmentResult = validResult();
    private final SubjectValue subjectValue = SubjectValueMother.createSubjectValue();
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final Param param = createParam(Param.ParamBuilder::build);

    @Test
    void testInitSubjectInsights_WhenSubjectIdsIsEmpty_ThenReturnEmptyList() {
        var paramWithEmptySubjectIds = createParam(b -> b.subjectIds(List.of()));
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), paramWithEmptySubjectIds.subjectIds()))
            .thenReturn(List.of());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.initSubjectInsights(paramWithEmptySubjectIds);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitSubjectInsights_WhenSubjectIdDoesNotExist_ThenReturnEmptyList() {
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), param.subjectIds()))
            .thenReturn(List.of());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.initSubjectInsights(param);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInitSubjectInsights_WhenSubjectIdIsValid_ThenReturnSubjectInsights() {
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), param.subjectIds()))
            .thenReturn(List.of(subjectValue));
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.initSubjectInsights(param);
        assertFalse(result.isEmpty());
        String defaultInsight = createSubjectDefaultInsight(subjectValue, param.locale());
        SubjectInsight subjectInsight = result.getFirst();

        assertEquals(assessmentResult.getId(), subjectInsight.getAssessmentResultId());
        assertEquals(subjectValue.getSubject().getId(), subjectInsight.getSubjectId());
        assertEquals(defaultInsight, subjectInsight.getInsight());
        assertNotNull(subjectInsight.getInsightTime());
        assertNotNull(subjectInsight.getLastModificationTime());
        assertNull(subjectInsight.getInsightBy());
        assertFalse(subjectInsight.isApproved());
    }

    @Test
    void testInitSubjectInsights_WhenLocaleIsPersian_ThenReturnSubjectInsightsInPersian() {
        var paramWithPersianLocale = createParam(b -> b.locale(Locale.of(KitLanguage.FA.getCode())));
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), paramWithPersianLocale.subjectIds()))
            .thenReturn(List.of(subjectValue));
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.initSubjectInsights(paramWithPersianLocale);
        assertFalse(result.isEmpty());
        String defaultInsight = createSubjectDefaultInsight(subjectValue, paramWithPersianLocale.locale());
        SubjectInsight subjectInsight = result.getFirst();

        assertEquals(assessmentResult.getId(), subjectInsight.getAssessmentResultId());
        assertEquals(subjectValue.getSubject().getId(), subjectInsight.getSubjectId());
        assertEquals(defaultInsight, subjectInsight.getInsight());
        assertNotNull(subjectInsight.getInsightTime());
        assertNotNull(subjectInsight.getLastModificationTime());
        assertNull(subjectInsight.getInsightBy());
        assertFalse(subjectInsight.isApproved());
    }

    private String createSubjectDefaultInsight(SubjectValue subjectValue, Locale locale) {
        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            locale,
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            (int) Math.ceil(subjectValue.getConfidenceValue()),
            subjectValue.getSubject().getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            maturityLevels.size(),
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getSubject().getAttributes().size(),
            subjectValue.getSubject().getTitle());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentResult(assessmentResult)
            .subjectIds(List.of(subjectValue.getSubject().getId()))
            .locale(Locale.ENGLISH);
    }
}
