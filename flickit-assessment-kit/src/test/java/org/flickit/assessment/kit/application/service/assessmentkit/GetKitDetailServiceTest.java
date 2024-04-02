package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase.Result;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitDetailServiceTest {

    @InjectMocks
    private GetKitDetailService service;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private LoadQuestionnairePort loadQuestionnairePort;

    @Test
    void testGetKitDetail_WhenKitExist_shouldReturnKitDetails() {
        long kitVersionId = 12;
        List<MaturityLevel> maturityLevels = List.of(
            MaturityLevelMother.levelOne(),
            MaturityLevelMother.levelTwo());
        List<Subject> subjects = List.of(SubjectMother.subjectWithTitle("subject1"));
        List<Questionnaire> questionnaires = List.of(QuestionnaireMother.questionnaireWithTitle("questionnaire1"));

        when(loadMaturityLevelsPort.loadByKitVersionId(kitVersionId)).thenReturn(maturityLevels);
        when(loadSubjectPort.loadByKitVersionId(kitVersionId)).thenReturn(subjects);
        when(loadQuestionnairePort.loadByKitVersionId(kitVersionId)).thenReturn(questionnaires);

        Result result = service.getKitDetail(new GetKitDetailUseCase.Param(kitVersionId));

        assertEquals(maturityLevels.size(), result.maturityLevels().size());
        assertEquals(maturityLevels.get(1).getCompetences().size(),
            result.maturityLevels().get(1).competences().size());
        assertEquals(subjects.size(), result.subjects().size());
        assertEquals(questionnaires.size(), result.questionnaires().size());
    }
}
