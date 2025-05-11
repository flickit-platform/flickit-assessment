package org.flickit.assessment.kit.application.service.assessmentkit;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.flickit.assessment.kit.application.port.out.kitlike.CheckKitLikeExistencePort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

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
    private CheckKitLikeExistencePort checkKitLikeExistencePort;

    @Mock
    private LoadKitLanguagesPort loadKitLanguagesPort;

    private GetPublishedKitUseCase.Param param = createParam(GetPublishedKitUseCase.Param.ParamBuilder::build);
    private final Subject subject = SubjectMother.subjectWithAttributes("subject", List.of(AttributeMother.attributeWithTitle("attribute")));
    private final CountKitStatsPort.Result counts = new CountKitStatsPort.Result(1, 1, 115, 1, 3, 1);

    @Test
    void testGetPublishedKit_whenKitIsNotPublished_thenThrowsResourceNotFoundException() {
        when(loadAssessmentKitPort.loadTranslated(param.getKitId()))
            .thenReturn(AssessmentKitMother.notPublishedKit());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getPublishedKit(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(checkKitUserAccessPort,
            countKitStatsPort,
            loadSubjectsPort,
            loadKitLanguagesPort);
    }

    @Test
    void testGetPublishedKit_whenKitIsPrivateAndCurrentUserIdIsNull_thenThrowsAccessDeniedException() {
        param = createParam(b -> b.currentUserId(null));

        when(loadAssessmentKitPort.loadTranslated(param.getKitId()))
            .thenReturn(AssessmentKitMother.privateKit());

        var exception = assertThrows(AccessDeniedException.class, () -> service.getPublishedKit(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(checkKitUserAccessPort,
            countKitStatsPort,
            loadSubjectsPort,
            loadKitLanguagesPort);
    }

    @Test
    void testGetPublishedKit_whenKitIsPrivateAndUserHasNotAccess_thenThrowsAccessDeniedException() {
        when(loadAssessmentKitPort.loadTranslated(param.getKitId()))
            .thenReturn(AssessmentKitMother.privateKit());
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId()))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getPublishedKit(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(countKitStatsPort,
            loadSubjectsPort,
            loadKitLanguagesPort);
    }

    @Test
    void testGetPublishedKit_whenKitIsPrivateAndUserHasAccess_thenReturnValidResult() {
        var kit = AssessmentKitMother.privateKit();
        var languages = List.of(KitLanguage.EN, KitLanguage.FA);

        when(loadAssessmentKitPort.loadTranslated(param.getKitId())).thenReturn(kit);
        when(checkKitUserAccessPort.hasAccess(param.getKitId(), param.getCurrentUserId())).thenReturn(true);
        when(countKitStatsPort.countKitStats(param.getKitId())).thenReturn(counts);
        when(loadSubjectsPort.loadAllTranslated(kit.getActiveVersionId())).thenReturn(List.of(subject));
        when(checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())).thenReturn(false);
        when(loadKitLanguagesPort.loadByKitId(param.getKitId())).thenReturn(languages);

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

        assertEquals(1, result.subjects().size());
        assertEquals(subject.getId(), result.subjects().getFirst().id());

        Assertions.assertThat(result.languages())
            .zipSatisfy(languages, (actual, expected) -> {
                assertEquals(expected.getCode(), actual.code());
                assertEquals(expected.getTitle(), actual.title());
            });
    }

    @Test
    void testGetPublishedKit_whenKitIsPublishedAndPublic_thenReturnValidResult() {
        var kit = AssessmentKitMother.simpleKit();

        when(loadAssessmentKitPort.loadTranslated(param.getKitId())).thenReturn(kit);
        when(countKitStatsPort.countKitStats(param.getKitId())).thenReturn(counts);
        when(loadSubjectsPort.loadAllTranslated(kit.getActiveVersionId())).thenReturn(List.of(subject));
        when(checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())).thenReturn(true);

        var result = service.getPublishedKit(param);

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

        assertEquals(1, result.subjects().size());
        assertEquals(subject.getId(), result.subjects().getFirst().id());

        verifyNoInteractions(checkKitUserAccessPort);
    }

    @Test
    void testGetPublishedKit_whenKitIsPublishedAndPublicAndCurrentUserIdIsNull_thenReturnValidResult() {
        param = createParam(b -> b.currentUserId(null));
        var kit = AssessmentKitMother.simpleKit();

        when(loadAssessmentKitPort.loadTranslated(param.getKitId())).thenReturn(kit);
        when(countKitStatsPort.countKitStats(param.getKitId())).thenReturn(counts);
        when(loadSubjectsPort.loadAllTranslated(kit.getActiveVersionId())).thenReturn(List.of(subject));
        when(checkKitLikeExistencePort.exist(param.getKitId(), param.getCurrentUserId())).thenReturn(true);

        var result = service.getPublishedKit(param);

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

        assertEquals(1, result.subjects().size());
        assertEquals(subject.getId(), result.subjects().getFirst().id());

        verifyNoInteractions(checkKitUserAccessPort);
    }

    private GetPublishedKitUseCase.Param createParam(Consumer<GetPublishedKitUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetPublishedKitUseCase.Param.ParamBuilder paramBuilder() {
        return GetPublishedKitUseCase.Param.builder()
            .kitId(0L)
            .currentUserId(UUID.randomUUID());
    }
}
