package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitlike.CheckKitLikeExistencePort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
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

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPublishedKitServiceTest {

    @InjectMocks
    private GetPublishedKitService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CheckKitUserAccessPort checkKitUserAccessPort;

    @Mock
    private CountKitStatsPort countKitStatsPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadKitTagListPort loadKitTagListPort;

    @Mock
    private CheckKitLikeExistencePort checkKitLikeExistencePort;

    @Test
    void testGetPublishedKit_WhenKitDoesNotExist_ThrowsException() {
        GetPublishedKitUseCase.Param param = new GetPublishedKitUseCase.Param(12L, UUID.randomUUID());

        when(loadAssessmentKitPort.load(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getPublishedKit(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(checkKitUserAccessPort,
            countKitStatsPort,
            loadSubjectsPort,
            loadQuestionnairesPort,
            loadMaturityLevelsPort,
            loadKitTagListPort);
    }

    @Test
    void testGetPublishedKit_WhenKitIsNotPublished_ThrowsException() {
        GetPublishedKitUseCase.Param param = new GetPublishedKitUseCase.Param(12L, UUID.randomUUID());
        when(loadAssessmentKitPort.load(param.getKitId()))
            .thenReturn(AssessmentKitMother.notPublishedKit());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getPublishedKit(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(checkKitUserAccessPort,
            countKitStatsPort,
            loadSubjectsPort,
            loadQuestionnairesPort,
            loadMaturityLevelsPort,
            loadKitTagListPort);
    }

    @Test
    void testGetPublishedKit_WhenKitIsPrivateAndUserHasNotAccess_ThrowsException() {
        GetPublishedKitUseCase.Param param = new GetPublishedKitUseCase.Param(12L, UUID.randomUUID());
        when(loadAssessmentKitPort.load(param.getKitId()))
            .thenReturn(AssessmentKitMother.privateKit());

        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(false);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getPublishedKit(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(countKitStatsPort,
            loadSubjectsPort,
            loadQuestionnairesPort,
            loadMaturityLevelsPort,
            loadKitTagListPort);
    }

    @Test
    void testGetPublishedKit_WhenKitIsPrivateAndUserHasAccess_ReturnValidResult() {
        var subject = SubjectMother.subjectWithAttributes("subject", List.of(AttributeMother.attributeWithTitle("attribute")));
        var questionnaire = QuestionnaireMother.questionnaireWithTitle("questionnaire");
        var maturityLevel = MaturityLevelMother.levelOne();
        var kit = AssessmentKitMother.privateKit();
        var param = new GetPublishedKitUseCase.Param(kit.getId(), UUID.randomUUID());

        var counts = new CountKitStatsPort.Result(1, 1, 115,
            1, 3, 1);
        var tag = KitTagMother.createKitTag("security");

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        when(countKitStatsPort.countKitStats(param.getKitId())).thenReturn(counts);
        when(loadSubjectsPort.loadByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(subject));
        when(loadQuestionnairesPort.loadByKitId(param.getKitId())).thenReturn(List.of(questionnaire));
        when(loadMaturityLevelsPort.loadAllByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(maturityLevel));
        when(loadKitTagListPort.loadByKitId(param.getKitId())).thenReturn(List.of(tag));
        when(checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())).thenReturn(false);

        GetPublishedKitUseCase.Result result = service.getPublishedKit(param);

        assertEquals(kit.getId(), result.id());
        assertEquals(kit.getTitle(), result.title());
        assertEquals(kit.getSummary(), result.summary());
        assertEquals(kit.getAbout(), result.about());
        assertEquals(kit.isPublished(), result.published());
        assertEquals(kit.isPrivate(), result.isPrivate());
        assertEquals(kit.getCreationTime(), result.creationTime());
        assertEquals(kit.getLastModificationTime(), result.lastModificationTime());
        assertEquals(kit.getExpertGroupId(), result.expertGroupId());

        assertEquals(counts.likes(), result.like().count());
        assertFalse(result.like().liked());
        assertEquals(counts.assessmentCounts(), result.assessmentsCount());
        assertEquals(1, result.subjectsCount());
        assertEquals(counts.questionnairesCount(), result.questionnairesCount());

        assertEquals(1, result.subjects().size());
        assertEquals(subject.getId(), result.subjects().getFirst().id());

        assertEquals(1, result.questionnaires().size());
        assertEquals(questionnaire.getId(), result.questionnaires().getFirst().id());

        assertEquals(1, result.maturityLevels().size());
        assertEquals(maturityLevel.getId(), result.maturityLevels().getFirst().id());

        assertEquals(1, result.tags().size());
        assertEquals(tag.getId(), result.tags().getFirst().id());
    }

    @Test
    void testGetPublishedKit_WhenKitIsPublishedAndPublic_ReturnValidResult() {
        var subject = SubjectMother.subjectWithAttributes("subject", List.of(AttributeMother.attributeWithTitle("attribute")));
        var questionnaire = QuestionnaireMother.questionnaireWithTitle("questionnaire");
        var maturityLevel = MaturityLevelMother.levelOne();
        var kit = AssessmentKitMother.simpleKit();
        var param = new GetPublishedKitUseCase.Param(kit.getId(), UUID.randomUUID());

        var counts = new CountKitStatsPort.Result(1, 1, 115,
            1, 3, 1);
        var tag = KitTagMother.createKitTag("security");

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(countKitStatsPort.countKitStats(param.getKitId())).thenReturn(counts);
        when(loadSubjectsPort.loadByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(subject));
        when(loadQuestionnairesPort.loadByKitId(param.getKitId())).thenReturn(List.of(questionnaire));
        when(loadMaturityLevelsPort.loadAllByKitVersionId(kit.getActiveVersionId())).thenReturn(List.of(maturityLevel));
        when(loadKitTagListPort.loadByKitId(param.getKitId())).thenReturn(List.of(tag));
        when(checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())).thenReturn(true);

        GetPublishedKitUseCase.Result result = service.getPublishedKit(param);

        assertEquals(kit.getId(), result.id());
        assertEquals(kit.getTitle(), result.title());
        assertEquals(kit.getSummary(), result.summary());
        assertEquals(kit.getAbout(), result.about());
        assertEquals(kit.isPublished(), result.published());
        assertEquals(kit.isPrivate(), result.isPrivate());
        assertEquals(kit.getCreationTime(), result.creationTime());
        assertEquals(kit.getLastModificationTime(), result.lastModificationTime());
        assertEquals(kit.getExpertGroupId(), result.expertGroupId());

        assertEquals(counts.likes(), result.like().count());
        assertTrue(result.like().liked());
        assertEquals(counts.assessmentCounts(), result.assessmentsCount());
        assertEquals(1, result.subjectsCount());
        assertEquals(counts.questionnairesCount(), result.questionnairesCount());

        assertEquals(1, result.subjects().size());
        assertEquals(subject.getId(), result.subjects().getFirst().id());

        assertEquals(1, result.questionnaires().size());
        assertEquals(questionnaire.getId(), result.questionnaires().getFirst().id());

        assertEquals(1, result.maturityLevels().size());
        assertEquals(maturityLevel.getId(), result.maturityLevels().getFirst().id());

        assertEquals(1, result.tags().size());
        assertEquals(tag.getId(), result.tags().getFirst().id());

        verifyNoInteractions(checkKitUserAccessPort);
    }
}
