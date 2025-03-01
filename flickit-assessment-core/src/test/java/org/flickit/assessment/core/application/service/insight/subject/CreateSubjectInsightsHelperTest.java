package org.flickit.assessment.core.application.service.insight.subject;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightParam;
import org.flickit.assessment.core.application.service.insight.subject.CreateSubjectInsightsHelper.SubjectInsightParam.SubjectInsightParamBuilder;
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
import java.util.Optional;
import java.util.function.Consumer;

import static org.flickit.assessment.core.common.ErrorMessageKey.SUBJECT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSubjectInsightsHelperTest {

    @InjectMocks
    private CreateSubjectInsightsHelper helper;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private LoadSubjectValuePort loadSubjectValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    private final AssessmentResult assessmentResult = validResult();
    private final SubjectValue subjectValue = SubjectValueMother.createSubjectValue();
    private final Subject subject = subjectValue.getSubject();
    private final List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
    private final SubjectInsightsParam subjectInsightsParam =
        createSubjectInsightsParam(SubjectInsightsParamBuilder::build);
    private final SubjectInsightParam subjectInsightParam =
        createSubjectInsightParam(SubjectInsightParamBuilder::build);

    @Test
    void testCreateSubjectInsight_whenSubjectIdDoesNotExist_thenThrowResourceNotFoundException() {
        when(loadSubjectPort.loadByIdAndKitVersionId(subjectInsightParam.subjectId(), assessmentResult.getKitVersionId()))
            .thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> helper.createSubjectInsight(subjectInsightParam));
        assertEquals(SUBJECT_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(loadSubjectValuePort, loadMaturityLevelsPort);
    }

    @Test
    void testCreateSubjectInsight_whenSubjectIdIsValid_thenReturnSubjectInsight() {
        when(loadSubjectPort.loadByIdAndKitVersionId(subjectInsightParam.subjectId(), assessmentResult.getKitVersionId()))
            .thenReturn(Optional.of(subject));
        when(loadSubjectValuePort.load(assessmentResult.getId(), subjectInsightParam.subjectId()))
            .thenReturn(subjectValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.createSubjectInsight(subjectInsightParam);

        assertNotNull(result);
        String defaultInsight = createSubjectDefaultInsight(subjectValue, subjectInsightsParam.locale());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(subjectValue.getSubject().getId(), result.getSubjectId());
        assertEquals(defaultInsight, result.getInsight());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }

    @Test
    void testCreateSubjectInsight_whenLocaleIsPersian_thenReturnSubjectInsightInPersian() {
        when(loadSubjectPort.loadByIdAndKitVersionId(subjectInsightParam.subjectId(), assessmentResult.getKitVersionId()))
            .thenReturn(Optional.of(subject));
        var paramWithPersianLocale = createSubjectInsightParam(b -> b.locale(Locale.of(KitLanguage.FA.getCode())));
        when(loadSubjectValuePort.load(assessmentResult.getId(), paramWithPersianLocale.subjectId()))
            .thenReturn(subjectValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.createSubjectInsight(paramWithPersianLocale);

        assertNotNull(result);
        String defaultInsight = createSubjectDefaultInsight(subjectValue, paramWithPersianLocale.locale());
        assertEquals(assessmentResult.getId(), result.getAssessmentResultId());
        assertEquals(subjectValue.getSubject().getId(), result.getSubjectId());
        assertEquals(defaultInsight, result.getInsight());
        assertNotNull(result.getInsightTime());
        assertNotNull(result.getLastModificationTime());
        assertNull(result.getInsightBy());
        assertFalse(result.isApproved());
    }

    @Test
    void testCreateSubjectInsights_whenSubjectValueDoesNotExist_thenReturnEmptyList() {
        when(loadSubjectValuePort.loadAll(assessmentResult.getId(), subjectInsightsParam.subjectIds()))
            .thenReturn(List.of());
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);

        var result = helper.createSubjectInsights(subjectInsightsParam);

        assertTrue(result.isEmpty());

        verifyNoInteractions(loadSubjectPort);
    }

    @Test
    void testCreateSubjectInsights_whenSubjectIdIsValid_thenReturnSubjectInsight() {
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

        verifyNoInteractions(loadSubjectPort);
    }

    @Test
    void testCreateSubjectInsights_whenLocaleIsPersian_thenReturnSubjectInsightInPersian() {
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

        verifyNoInteractions(loadSubjectPort);
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

    private SubjectInsightParam createSubjectInsightParam(Consumer<SubjectInsightParamBuilder> changer) {
        var paramBuilder = subjectInsightParamBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private SubjectInsightParamBuilder subjectInsightParamBuilder() {
        return SubjectInsightParam.builder()
            .assessmentResult(assessmentResult)
            .subjectId(subjectValue.getSubject().getId())
            .locale(Locale.ENGLISH);
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
