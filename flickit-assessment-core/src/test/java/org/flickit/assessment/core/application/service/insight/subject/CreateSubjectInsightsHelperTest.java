package org.flickit.assessment.core.application.service.insight.subject;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightsParam;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightsParam.SubjectInsightsParamBuilder;
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
    private final SubjectInsightsParam subjectInsightsParam =
        createSubjectInsightsParam(SubjectInsightsParamBuilder::build);

    @Test
    void testCreateSubjectInsights_WhenSubjectIdsIsEmpty_ThenReturnEmptyList() {
        var paramWithEmptySubjectIds = createSubjectInsightsParam(b -> b.subjectIds(List.of()));
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), paramWithEmptySubjectIds.subjectIds()))
            .thenReturn(List.of());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.createSubjectInsights(paramWithEmptySubjectIds);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateSubjectInsights_WhenSubjectIdDoesNotExist_ThenReturnEmptyList() {
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), subjectInsightsParam.subjectIds()))
            .thenReturn(List.of());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.createSubjectInsights(subjectInsightsParam);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateSubjectInsights_WhenSubjectIdIsValid_ThenReturnSubjectInsight() {
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), subjectInsightsParam.subjectIds()))
            .thenReturn(List.of(subjectValue));
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.createSubjectInsights(subjectInsightsParam);
        assertFalse(result.isEmpty());
        String defaultInsight = createSubjectDefaultInsight(subjectValue, subjectInsightsParam.locale());
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
    void testCreateSubjectInsights_WhenLocaleIsPersian_ThenReturnSubjectInsightInPersian() {
        var paramWithPersianLocale = createSubjectInsightsParam(b -> b.locale(Locale.of(KitLanguage.FA.getCode())));
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), paramWithPersianLocale.subjectIds()))
            .thenReturn(List.of(subjectValue));
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.createSubjectInsights(paramWithPersianLocale);
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

    private SubjectInsightsParam createSubjectInsightsParam(Consumer<SubjectInsightsParamBuilder> changer) {
        var paramBuilder = subjectInsightsParamBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private SubjectInsightsParamBuilder subjectInsightsParamBuilder() {
        return SubjectInsightsParam.builder()
            .assessmentResult(assessmentResult)
            .subjectIds(List.of(subjectValue.getSubject().getId()))
            .locale(Locale.ENGLISH);
    }
}
