package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitFullInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitaccess.CheckKitAccessPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagsListPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPublishedKitServiceTest {

    @InjectMocks
    private GetPublishedKitService service;

    @Mock
    private LoadAssessmentKitFullInfoPort loadAssessmentKitFullInfoPort;

    @Mock
    private CheckKitAccessPort checkKitAccessPort;

    @Mock
    private CountKitStatsPort countKitStatsPort;

    @Mock
    private LoadKitTagsListPort loadKitTagsListPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetPublishedKit_WhenKitDoesNotExist_ThrowsException() {
        GetPublishedKitUseCase.Param param = new GetPublishedKitUseCase.Param(12L, UUID.randomUUID());

        when(loadAssessmentKitFullInfoPort.load(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getPublishedKit(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetPublishedKit_WhenKitIsNotPublished_ThrowsException() {
        GetPublishedKitUseCase.Param param = new GetPublishedKitUseCase.Param(12L, UUID.randomUUID());
        when(loadAssessmentKitFullInfoPort.load(param.getKitId()))
            .thenReturn(AssessmentKitMother.notPublishedKit());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getPublishedKit(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetPublishedKit_WhenKitIsPrivateAndUserHasNotAccess_ThrowsException() {
        GetPublishedKitUseCase.Param param = new GetPublishedKitUseCase.Param(12L, UUID.randomUUID());
        when(loadAssessmentKitFullInfoPort.load(param.getKitId()))
            .thenReturn(AssessmentKitMother.privateKit());

        when(checkKitAccessPort.checkHasAccess(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(false);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getPublishedKit(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void testGetPublishedKit_WhenKitIsPrivateAndUserHasAccess_ReturnValidResult() {
        var subject = SubjectMother.subjectWithAttributes("subject", List.of(AttributeMother.attributeWithTitle("attribute")));
        var questionnaire = QuestionnaireMother.questionnaireWithTitle("questionnaire");
        var maturityLevel = MaturityLevelMother.levelOne();
        var kit = AssessmentKitMother.completePrivateKit(List.of(subject), List.of(maturityLevel), List.of(questionnaire));
        var param = new GetPublishedKitUseCase.Param(kit.getId(), UUID.randomUUID());

        var counts = new CountKitStatsPort.Result(1, 1, 115,
            1, 3, 1);
        var tag = KitTagMother.createKitTag("security");
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var expertGroupPictureUrl = "https://expertGroupAvatarUrl";

        when(loadAssessmentKitFullInfoPort.load(param.getKitId())).thenReturn(kit);
        when(checkKitAccessPort.checkHasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        when(countKitStatsPort.countKitStats(param.getKitId())).thenReturn(counts);
        when(loadKitTagsListPort.load(param.getKitId())).thenReturn(List.of(tag));
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(createFileDownloadLinkPort.createDownloadLink(any(), any())).thenReturn(expertGroupPictureUrl);

        GetPublishedKitUseCase.Result result = service.getPublishedKit(param);

        assertEquals(kit.getId(), result.id());
        assertEquals(kit.getTitle(), result.title());
        assertEquals(kit.getSummary(), result.summary());
        assertEquals(kit.getAbout(), result.about());
        assertEquals(kit.isPublished(), result.published());
        assertEquals(kit.isPrivate(), result.isPrivate());
        assertEquals(kit.getCreationTime(), result.creationTime());
        assertEquals(kit.getLastModificationTime(), result.lastModificationTime());

        assertEquals(counts.likes(), result.likes());
        assertEquals(counts.assessmentCounts(), result.assessmentsCount());
        assertEquals(kit.getSubjects().size(), result.subjectsCount());
        assertEquals(counts.questionnairesCount(), result.questionnairesCount());

        assertEquals(kit.getSubjects().size(), result.subjects().size());
        assertEquals(subject.getId(), result.subjects().get(0).id());

        assertEquals(kit.getQuestionnaires().size(), result.questionnaires().size());
        assertEquals(questionnaire.getId(), result.questionnaires().get(0).id());

        assertEquals(kit.getMaturityLevels().size(), result.maturityLevels().size());
        assertEquals(maturityLevel.getId(), result.maturityLevels().get(0).id());

        assertEquals(1, result.tags().size());
        assertEquals(tag.getId(), result.tags().get(0).id());

        assertEquals(expertGroup.getId(), result.expertGroup().id());
        assertEquals(expertGroupPictureUrl, result.expertGroup().picture());
    }

    @Test
    void testGetPublishedKit_WhenKitIsPublishedAndPublic_ReturnValidResult() {
        var subject = SubjectMother.subjectWithAttributes("subject", List.of(AttributeMother.attributeWithTitle("attribute")));
        var questionnaire = QuestionnaireMother.questionnaireWithTitle("questionnaire");
        var maturityLevel = MaturityLevelMother.levelOne();
        var kit = AssessmentKitMother.completeKit(List.of(subject), List.of(maturityLevel), List.of(questionnaire));
        var param = new GetPublishedKitUseCase.Param(kit.getId(), UUID.randomUUID());

        var counts = new CountKitStatsPort.Result(1, 1, 115,
            1, 3, 1);
        var tag = KitTagMother.createKitTag("security");
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var expertGroupPictureUrl = "https://expertGroupAvatarUrl";

        when(loadAssessmentKitFullInfoPort.load(param.getKitId())).thenReturn(kit);
        when(countKitStatsPort.countKitStats(param.getKitId())).thenReturn(counts);
        when(loadKitTagsListPort.load(param.getKitId())).thenReturn(List.of(tag));
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(createFileDownloadLinkPort.createDownloadLink(any(), any())).thenReturn(expertGroupPictureUrl);

        GetPublishedKitUseCase.Result result = service.getPublishedKit(param);

        assertEquals(kit.getId(), result.id());
        assertEquals(kit.getTitle(), result.title());
        assertEquals(kit.getSummary(), result.summary());
        assertEquals(kit.getAbout(), result.about());
        assertEquals(kit.isPublished(), result.published());
        assertEquals(kit.isPrivate(), result.isPrivate());
        assertEquals(kit.getCreationTime(), result.creationTime());
        assertEquals(kit.getLastModificationTime(), result.lastModificationTime());

        assertEquals(counts.likes(), result.likes());
        assertEquals(counts.assessmentCounts(), result.assessmentsCount());
        assertEquals(kit.getSubjects().size(), result.subjectsCount());
        assertEquals(counts.questionnairesCount(), result.questionnairesCount());

        assertEquals(kit.getSubjects().size(), result.subjects().size());
        assertEquals(subject.getId(), result.subjects().get(0).id());

        assertEquals(kit.getQuestionnaires().size(), result.questionnaires().size());
        assertEquals(questionnaire.getId(), result.questionnaires().get(0).id());

        assertEquals(kit.getMaturityLevels().size(), result.maturityLevels().size());
        assertEquals(maturityLevel.getId(), result.maturityLevels().get(0).id());

        assertEquals(1, result.tags().size());
        assertEquals(tag.getId(), result.tags().get(0).id());

        assertEquals(expertGroup.getId(), result.expertGroup().id());
        assertEquals(expertGroupPictureUrl, result.expertGroup().picture());

        verifyNoInteractions(checkKitAccessPort);
    }
}
