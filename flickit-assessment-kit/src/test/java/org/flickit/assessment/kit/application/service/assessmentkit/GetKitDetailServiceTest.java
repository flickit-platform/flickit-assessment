package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase.Result;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitDetailServiceTest {

    @InjectMocks
    private GetKitDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Test
    void testGetKitDetail_WhenKitExist_shouldReturnKitDetails() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var currentUserId = expertGroup.getOwnerId();
        var kitVersionId = 1L;
        GetKitDetailUseCase.Param param = new GetKitDetailUseCase.Param(12L, currentUserId);

        List<MaturityLevel> maturityLevels = List.of(
            MaturityLevelMother.levelOne(),
            MaturityLevelMother.levelTwo());
        List<Subject> subjects = List.of(SubjectMother.subjectWithTitle("subject1"));
        List<Questionnaire> questionnaires = List.of(QuestionnaireMother.questionnaireWithTitle("questionnaire1"));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadMaturityLevelsPort.loadByKitVersionId(kitVersionId)).thenReturn(maturityLevels);
        when(loadSubjectsPort.loadByKitVersionId(kitVersionId)).thenReturn(subjects);
        when(loadQuestionnairesPort.loadByKitId(param.getKitId())).thenReturn(questionnaires);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);

        Result result = service.getKitDetail(param);

        assertEquals(maturityLevels.size(), result.maturityLevels().size());
        assertEquals(maturityLevels.get(1).getCompetences().size(),
            result.maturityLevels().get(1).competences().size());
        assertEquals(subjects.size(), result.subjects().size());
        assertEquals(questionnaires.size(), result.questionnaires().size());
    }

    @Test
    void testGetKitDetail_WhenKitDoesNotExist_ThrowsException() {
        GetKitDetailUseCase.Param param = new GetKitDetailUseCase.Param(12L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getKitDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetKitDetail_WhenUserIsNotMember_ThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        GetKitDetailUseCase.Param param = new GetKitDetailUseCase.Param(12L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getKitDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
