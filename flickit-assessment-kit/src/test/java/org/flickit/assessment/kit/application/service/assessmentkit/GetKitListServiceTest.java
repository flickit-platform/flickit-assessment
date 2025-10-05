package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitListStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitListServiceTest {

    @InjectMocks
    private GetKitListService service;

    @Mock
    private LoadPublishedKitListPort loadPublishedKitListPort;

    @Mock
    private CountKitListStatsPort countKitStatsPort;

    @Mock
    private LoadKitLanguagesPort loadKitLanguagesPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static final ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
    private static final String EXPERT_GROUP_PICTURE_URL = "https://picureLink";

    @Test
    void testGetKitList_whenPublicKitsAreWantedAndResultIsFreeKitAndUserDoesNotHaveAccess_thenReturnPublicKits() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentKit = simpleKitWithPrice(0);
        var kitId = assessmentKit.getId();
        var kitIds = List.of(kitId);

        var expectedKitsPage = getExpectedKitsPage(assessmentKit, true);

        when(loadPublishedKitListPort.loadPublicKits(param.getCurrentUserId(), Set.of(KitLanguage.EN), param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        int kitLikes = 3, assessmentsCount = 15;
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, kitLikes, assessmentsCount)));
        when(loadKitLanguagesPort.loadByKitIds(kitIds)).thenReturn(
            Map.of(kitId, List.of(KitLanguage.EN)));
        when(createFileDownloadLinkPort.createDownloadLinkSafe(any(), any()))
            .thenReturn(EXPERT_GROUP_PICTURE_URL);

        var result = service.getKitList(param);
        assertPage(expectedKitsPage, result);
        var item = result.getItems().getFirst();
        assertKit(assessmentKit, item, kitLikes, assessmentsCount);
        assertEquals(KitLanguage.EN.getTitle(), item.languages().getFirst());
        assertExpertGroup(item);
        assertTrue(item.isFree());
        assertTrue(item.hasAccess());

        verify(loadPublishedKitListPort, never()).loadPrivateKits(any(), any(), anyInt(), anyInt());
    }

    @Test
    void testGetKitList_whenPublicKitsAreWantedAndResultIsPaidKitAndUserDoesNotHaveAccess_thenReturnPublicKits() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentKit = simpleKitWithPrice(1000);
        var kitId = assessmentKit.getId();
        var kitIds = List.of(kitId);

        var expectedKitsPage = getExpectedKitsPage(assessmentKit, false);

        when(loadPublishedKitListPort.loadPublicKits(param.getCurrentUserId(), Set.of(KitLanguage.EN), param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        int kitLikes = 3, assessmentsCount = 15;
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, kitLikes, assessmentsCount)));
        when(loadKitLanguagesPort.loadByKitIds(kitIds)).thenReturn(
            Map.of(kitId, List.of(KitLanguage.EN)));
        when(createFileDownloadLinkPort.createDownloadLinkSafe(any(), any()))
            .thenReturn(EXPERT_GROUP_PICTURE_URL);

        var result = service.getKitList(param);
        assertPage(expectedKitsPage, result);
        var item = result.getItems().getFirst();
        assertKit(assessmentKit, item, kitLikes, assessmentsCount);
        assertExpertGroup(item);
        assertFalse(item.isFree());
        assertFalse(item.hasAccess());

        verify(loadPublishedKitListPort, never()).loadPrivateKits(any(), any(), anyInt(), anyInt());
    }

    @Test
    void testGetKitList_whenPrivateKitsWithoutAccessAreWanted_thenReturnPrivateKits() {
        var param = createParam(p -> p
            .isPrivate(true)
            .langs(null));
        var assessmentKit = privateKit();
        var kitId = assessmentKit.getId();
        var kitIds = List.of(kitId);
        var expectedKitsPage = getExpectedKitsPage(assessmentKit, false);

        when(loadPublishedKitListPort.loadPrivateKits(param.getCurrentUserId(), null, param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        int kitLikes = 3, assessmentCount = 15;
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, kitLikes, assessmentCount)));
        when(loadKitLanguagesPort.loadByKitIds(kitIds)).thenReturn(
            Map.of(kitId, List.of(KitLanguage.EN)));
        when(createFileDownloadLinkPort.createDownloadLinkSafe(any(), any()))
            .thenReturn(EXPERT_GROUP_PICTURE_URL);

        var result = service.getKitList(param);
        assertPage(expectedKitsPage, result);
        var item = result.getItems().getFirst();
        assertKit(assessmentKit, item, kitLikes, assessmentCount);
        assertTrue(item.isPrivate());
        assertEquals(KitLanguage.EN.getTitle(), item.languages().getFirst());
        assertExpertGroup(item);
        assertTrue(item.isFree());
        assertFalse(item.hasAccess());

        verify(loadPublishedKitListPort, never()).loadPublicKits(isNull(), anyInt(), anyInt());
    }

    @Test
    void testGetKitList_whenAllPublishedKitsAreWanted_thenReturnAllKits() {
        var param = createParam(p -> p
            .langs(null)
            .isPrivate(null));
        var assessmentKit = simpleKit();
        var kitId = assessmentKit.getId();
        var kitIds = List.of(kitId);
        var expectedKitsPage = getExpectedKitsPage(assessmentKit, false);

        when(loadPublishedKitListPort.loadPrivateAndPublicKits(param.getCurrentUserId(), null, param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        int kitLikes = 3, assessmentsCount = 15;
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, kitLikes, assessmentsCount)));
        when(loadKitLanguagesPort.loadByKitIds(kitIds)).thenReturn(
            Map.of(kitId, List.of(KitLanguage.EN)));
        when(createFileDownloadLinkPort.createDownloadLinkSafe(any(), any()))
            .thenReturn(EXPERT_GROUP_PICTURE_URL);

        var result = service.getKitList(param);
        assertPage(expectedKitsPage, result);
        var item = result.getItems().getFirst();
        assertKit(assessmentKit, item, kitLikes, assessmentsCount);
        assertEquals(KitLanguage.EN.getTitle(), item.languages().getFirst());
        assertExpertGroup(item);
        assertTrue(item.isFree());
        assertTrue(item.hasAccess());

        verify(loadPublishedKitListPort, never()).loadPublicKits(isNull(), anyInt(), anyInt());
    }

    @Test
    void testGetKitList_whenNoKitIsFound_thenReturnEmptyResult() {
        var param = createParam(p -> p.isPrivate(true));

        var expectedKitsPage = new PaginatedResponse<LoadPublishedKitListPort.Result>(List.of(),
            0,
            10,
            AssessmentKitJpaEntity.Fields.title,
            Sort.Direction.ASC.name().toLowerCase(),
            0
        );

        when(loadPublishedKitListPort.loadPrivateKits(param.getCurrentUserId(), Set.of(KitLanguage.EN), param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        when(countKitStatsPort.countKitsStats(List.of())).thenReturn(List.of());
        when(loadKitLanguagesPort.loadByKitIds(List.of())).thenReturn(Map.of());

        var result = service.getKitList(param);
        assertPage(expectedKitsPage, result);

        verify(loadPublishedKitListPort, never()).loadPublicKits(any(), anyInt(), anyInt());
        verify(createFileDownloadLinkPort, never()).createDownloadLinkSafe(anyString(), any());
    }

    private static PaginatedResponse<LoadPublishedKitListPort.Result> getExpectedKitsPage(AssessmentKit assessmentKit, boolean hasKitAccess) {
        return new PaginatedResponse<>(
            List.of(new LoadPublishedKitListPort.Result(assessmentKit, expertGroup, hasKitAccess)),
            0,
            10,
            AssessmentKitJpaEntity.Fields.title,
            Sort.Direction.ASC.name().toLowerCase(),
            1
        );
    }

    private static void assertPage(PaginatedResponse<LoadPublishedKitListPort.Result> expectedKitsPage, PaginatedResponse<GetKitListUseCase.KitListItem> kitList) {
        assertEquals(expectedKitsPage.getPage(), kitList.getPage());
        assertEquals(expectedKitsPage.getSize(), kitList.getSize());
        assertEquals(expectedKitsPage.getSort(), kitList.getSort());
        assertEquals(expectedKitsPage.getOrder(), kitList.getOrder());
        assertEquals(expectedKitsPage.getTotal(), kitList.getTotal());
        assertEquals(expectedKitsPage.getItems().size(), kitList.getItems().size());
    }

    private static void assertKit(AssessmentKit assessmentKit, GetKitListUseCase.KitListItem item, int kitLike, int assessmentCount) {
        assertEquals(assessmentKit.getId(), item.id());
        assertEquals(assessmentKit.getTitle(), item.title());
        assertEquals(assessmentKit.getSummary(), item.summary());
        assertEquals(kitLike, item.likes());
        assertEquals(assessmentCount, item.assessmentsCount());
    }

    private static void assertExpertGroup(GetKitListUseCase.KitListItem item) {
        assertEquals(expertGroup.getId(), item.expertGroup().id());
        assertEquals(expertGroup.getTitle(), item.expertGroup().title());
        assertEquals(EXPERT_GROUP_PICTURE_URL, item.expertGroup().picture());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .isPrivate(false)
            .langs(Set.of(KitLanguage.EN.name()))
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
