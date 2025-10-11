package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase.Result;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private LoadMeasurePort loadMeasurePort;

    @Test
    void testGetKitDetail_whenKitExist_thenShouldReturnKitDetails() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var currentUserId = expertGroup.getOwnerId();
        var kitVersionId = 1L;
        GetKitDetailUseCase.Param param = new GetKitDetailUseCase.Param(12L, currentUserId);

        List<MaturityLevel> maturityLevels = List.of(
            MaturityLevelMother.levelOneWithTranslations(),
            MaturityLevelMother.levelTwo());
        var attribute1 = AttributeMother.attributeWithTitle("attribute1");
        var attribute2 = AttributeMother.attributeWithTitle("attribute2");
        List<Attribute> attributes = List.of(attribute2, attribute1);
        List<Subject> subjects = List.of(SubjectMother.subjectWithAttributes("subject1", attributes));
        List<Questionnaire> questionnaires = List.of(QuestionnaireMother.questionnaireWithTitle("questionnaire1"));
        List<Measure> measures = List.of(MeasureMother.measureWithTitle("measure1"), MeasureMother.measureWithTitle("measure2"));

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadMaturityLevelsPort.loadAllByKitVersionId(kitVersionId)).thenReturn(maturityLevels);
        when(loadSubjectsPort.loadByKitVersionId(kitVersionId)).thenReturn(subjects);
        when(loadQuestionnairesPort.loadByKitId(param.getKitId())).thenReturn(questionnaires);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadMeasurePort.loadAll(kitVersionId)).thenReturn(measures);

        Result result = service.getKitDetail(param);

        assertEquals(maturityLevels.get(1).getCompetences().size(),
            result.maturityLevels().get(1).competences().size());
        assertThat(maturityLevels)
            .zipSatisfy(result.maturityLevels(), (actual, expected) -> {
                assertEquals(expected.id(), actual.getId());
                assertEquals(expected.title(), actual.getTitle());
                assertEquals(expected.description(), actual.getDescription());
                assertEquals(expected.competences().size(), actual.getCompetences().size());
                assertEquals(expected.translations(), actual.getTranslations());
            });
        assertEquals(subjects.size(), result.subjects().size());
        assertEquals(questionnaires.size(), result.questionnaires().size());
        var resultAttributes = result.subjects().getFirst().attributes();
        assertEquals(2, resultAttributes.size());
        assertEquals(attribute1.getId(), resultAttributes.getFirst().id());
        assertEquals(attribute2.getId(), resultAttributes.getLast().id());
        assertEquals(2, result.measures().size());
    }

    @Test
    void testGetKitDetail_whenKitDoesNotExist_thenThrowsException() {
        GetKitDetailUseCase.Param param = new GetKitDetailUseCase.Param(12L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getKitDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetKitDetail_whenUserIsNotMember_thenThrowsException() {
        var expertGroup = ExpertGroupMother.createExpertGroup();
        GetKitDetailUseCase.Param param = new GetKitDetailUseCase.Param(12L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getKitDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
