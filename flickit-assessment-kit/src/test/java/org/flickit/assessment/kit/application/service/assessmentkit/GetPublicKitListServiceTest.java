package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublicKitListUseCase;
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
import java.util.function.Consumer;

import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithPrice;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPublicKitListServiceTest {

    @InjectMocks
    private GetPublicKitListService service;

    @Mock
    private LoadPublishedKitListPort loadPublishedKitListPort;

    @Mock
    private CountKitListStatsPort countKitStatsPort;

    @Mock
    private LoadKitLanguagesPort loadKitLanguagesPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private final GetPublicKitListUseCase.Param param = createParam(GetPublicKitListUseCase.Param.ParamBuilder::build);
    private final ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
    private final String expertGroupPictureUrl = "https://picureLink";

    @Test
    void testGetPublicList_whenPublicKitsAreWantedAnkAndKitIsFree_thenReturnPublicKits() {
        AssessmentKit assessmentKit = kitWithPrice(0);
        long kitId = assessmentKit.getId();
        List<Long> kitIds = List.of(kitId);
        var expectedKitsPage = getExpectedKitsPage(assessmentKit);

        when(loadPublishedKitListPort.loadPublicKits(Set.of(KitLanguage.EN), param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        int likes = 3, assessmentsCount = 15;
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, likes, assessmentsCount)));
        when(loadKitLanguagesPort.loadByKitIds(kitIds)).thenReturn(
            Map.of(kitId, List.of(KitLanguage.EN)));
        when(createFileDownloadLinkPort.createDownloadLink(any(), any()))
            .thenReturn(expertGroupPictureUrl);

        var kitList = service.getPublicKitList(param);

        assertPage(expectedKitsPage, kitList);

        var item = kitList.getItems().getFirst();
        assertAssessmentKit(assessmentKit, item, likes, assessmentsCount);
        assertEquals(KitLanguage.EN.getTitle(), item.languages().getFirst());
        assertExpertGroup(item);
        assertTrue(item.isFree());
    }

    @Test
    void testGetPublicList_whenPublicKitsAreWantedAndKitIsPaid_thenReturnPublicKits() {
        AssessmentKit assessmentKit = kitWithPrice(100);
        long kitId = assessmentKit.getId();
        List<Long> kitIds = List.of(kitId);
        var expectedKitsPage = getExpectedKitsPage(assessmentKit);

        when(loadPublishedKitListPort.loadPublicKits(Set.of(KitLanguage.EN), param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        int likes = 2, assessmentsCount = 10;
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, likes, assessmentsCount)));
        when(loadKitLanguagesPort.loadByKitIds(kitIds)).thenReturn(
            Map.of(kitId, List.of(KitLanguage.EN)));
        when(createFileDownloadLinkPort.createDownloadLink(any(), any()))
            .thenReturn(expertGroupPictureUrl);

        var kitList = service.getPublicKitList(param);

        assertPage(expectedKitsPage, kitList);
        var item = kitList.getItems().getFirst();
        assertAssessmentKit(assessmentKit, item, likes, assessmentsCount);
        assertEquals(KitLanguage.EN.getTitle(), item.languages().getFirst());
        assertExpertGroup(item);
        assertFalse(item.isFree());
    }

    private PaginatedResponse<LoadPublishedKitListPort.Result> getExpectedKitsPage(AssessmentKit assessmentKit) {
        return new PaginatedResponse<>(
            List.of(new LoadPublishedKitListPort.Result(assessmentKit, expertGroup)),
            0,
            10,
            AssessmentKitJpaEntity.Fields.title,
            Sort.Direction.ASC.name().toLowerCase(),
            1
        );
    }

    private static void assertPage(PaginatedResponse<LoadPublishedKitListPort.Result> expectedKitsPage, PaginatedResponse<GetPublicKitListUseCase.KitListItem> kitList) {
        assertEquals(expectedKitsPage.getPage(), kitList.getPage());
        assertEquals(expectedKitsPage.getSize(), kitList.getSize());
        assertEquals(expectedKitsPage.getSort(), kitList.getSort());
        assertEquals(expectedKitsPage.getOrder(), kitList.getOrder());
        assertEquals(expectedKitsPage.getTotal(), kitList.getTotal());
        assertEquals(expectedKitsPage.getItems().size(), kitList.getItems().size());
    }

    private static void assertAssessmentKit(AssessmentKit assessmentKit, GetPublicKitListUseCase.KitListItem item, int likes, int assessmentsCount) {
        assertEquals(assessmentKit.getId(), item.id());
        assertEquals(assessmentKit.getTitle(), item.title());
        assertEquals(assessmentKit.getSummary(), item.summary());
        assertEquals(likes, item.likes());
        assertEquals(assessmentsCount, item.assessmentsCount());
    }

    private void assertExpertGroup(GetPublicKitListUseCase.KitListItem item) {
        assertEquals(expertGroup.getId(), item.expertGroup().id());
        assertEquals(expertGroup.getTitle(), item.expertGroup().title());
        assertEquals(expertGroupPictureUrl, item.expertGroup().picture());
    }

    private GetPublicKitListUseCase.Param createParam(Consumer<GetPublicKitListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetPublicKitListUseCase.Param.ParamBuilder paramBuilder() {
        return GetPublicKitListUseCase.Param.builder()
            .langs(Set.of(KitLanguage.EN.name()))
            .page(0)
            .size(10);
    }
}
