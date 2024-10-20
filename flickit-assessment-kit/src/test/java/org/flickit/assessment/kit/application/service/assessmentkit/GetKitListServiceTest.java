package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitListStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.KitTagMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private LoadKitTagListPort loadKitTagListPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetKitList_GettingPublicKitsValidParams_ValidResult() {
        var param = new Param(Boolean.FALSE, 0, 10, UUID.randomUUID());
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitId = assessmentKit.getId();
        var kitIds = List.of(kitId);
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var expertGroupPictureUrl = "https://picureLink";
        var expectedKitsPage = new PaginatedResponse<>(
            List.of(new LoadPublishedKitListPort.Result(assessmentKit, expertGroup)),
            0,
            10,
            AssessmentKitJpaEntity.Fields.TITLE,
            Sort.Direction.ASC.name().toLowerCase(),
            1
        );
        var sampleTag = KitTagMother.createKitTag("sample tag");

        when(loadPublishedKitListPort.loadPublicKits(param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, 3, 15)));
        when(loadKitTagListPort.loadByKitIds(kitIds)).thenReturn(
            List.of(new LoadKitTagListPort.Result(kitId, List.of(sampleTag))));
        when(createFileDownloadLinkPort.createDownloadLink(any(), any()))
            .thenReturn(expertGroupPictureUrl);

        var kitList = service.getKitList(param);

        assertEquals(expectedKitsPage.getPage(), kitList.getPage());
        assertEquals(expectedKitsPage.getSize(), kitList.getSize());
        assertEquals(expectedKitsPage.getSort(), kitList.getSort());
        assertEquals(expectedKitsPage.getOrder(), kitList.getOrder());
        assertEquals(expectedKitsPage.getTotal(), kitList.getTotal());
        assertEquals(expectedKitsPage.getItems().size(), kitList.getItems().size());

        var item = kitList.getItems().getFirst();
        assertEquals(assessmentKit.getId(), item.id());
        assertEquals(assessmentKit.getTitle(), item.title());
        assertEquals(assessmentKit.getSummary(), item.summary());
        assertEquals(assessmentKit.isPrivate(), item.isPrivate());
        assertEquals(3, item.likes());
        assertEquals(15, item.assessmentsCount());
        assertEquals(expertGroup.getId(), item.expertGroup().id());
        assertEquals(expertGroup.getTitle(), item.expertGroup().title());
        assertEquals(expertGroupPictureUrl, item.expertGroup().picture());

        verify(loadPublishedKitListPort, never()).loadPrivateKits(any(), anyInt(), anyInt());
    }

    @Test
    void testGetKitList_GettingPrivateKitsValidParams_ValidResult() {
        var param = new Param(Boolean.TRUE, 0, 10, UUID.randomUUID());
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitId = assessmentKit.getId();
        var kitIds = List.of(kitId);
        var expertGroup = ExpertGroupMother.createExpertGroup();
        var expertGroupPictureUrl = "https://picureLink";
        var expectedKitsPage = new PaginatedResponse<>(
            List.of(new LoadPublishedKitListPort.Result(assessmentKit, expertGroup)),
            0,
            10,
            AssessmentKitJpaEntity.Fields.TITLE,
            Sort.Direction.ASC.name().toLowerCase(),
            1
        );
        var sampleTag = KitTagMother.createKitTag("sample tag");

        when(loadPublishedKitListPort.loadPrivateKits(param.getCurrentUserId(), param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        when(countKitStatsPort.countKitsStats(kitIds))
            .thenReturn(List.of(new CountKitListStatsPort.Result(kitId, 3, 15)));
        when(loadKitTagListPort.loadByKitIds(kitIds)).thenReturn(
            List.of(new LoadKitTagListPort.Result(kitId, List.of(sampleTag))));
        when(createFileDownloadLinkPort.createDownloadLink(any(), any()))
            .thenReturn(expertGroupPictureUrl);

        var kitList = service.getKitList(param);

        assertEquals(expectedKitsPage.getPage(), kitList.getPage());
        assertEquals(expectedKitsPage.getSize(), kitList.getSize());
        assertEquals(expectedKitsPage.getSort(), kitList.getSort());
        assertEquals(expectedKitsPage.getOrder(), kitList.getOrder());
        assertEquals(expectedKitsPage.getTotal(), kitList.getTotal());
        assertEquals(expectedKitsPage.getItems().size(), kitList.getItems().size());

        var item = kitList.getItems().getFirst();
        assertEquals(assessmentKit.getId(), item.id());
        assertEquals(assessmentKit.getTitle(), item.title());
        assertEquals(assessmentKit.getSummary(), item.summary());
        assertEquals(assessmentKit.isPrivate(), item.isPrivate());
        assertEquals(3, item.likes());
        assertEquals(15, item.assessmentsCount());
        assertEquals(expertGroup.getId(), item.expertGroup().id());
        assertEquals(expertGroup.getTitle(), item.expertGroup().title());
        assertEquals(expertGroupPictureUrl, item.expertGroup().picture());

        verify(loadPublishedKitListPort, never()).loadPublicKits(anyInt(), anyInt());
    }

    @Test
    void testGetKitList_GettingPrivateKitsValidParams_EmptyResult() {
        var param = new Param(Boolean.TRUE, 0, 10, UUID.randomUUID());

        var expectedKitsPage = new PaginatedResponse<LoadPublishedKitListPort.Result>(List.of(),
            0,
            10,
            AssessmentKitJpaEntity.Fields.TITLE,
            Sort.Direction.ASC.name().toLowerCase(),
            0
        );

        when(loadPublishedKitListPort.loadPrivateKits(param.getCurrentUserId(), param.getPage(), param.getSize()))
            .thenReturn(expectedKitsPage);
        when(countKitStatsPort.countKitsStats(List.of())).thenReturn(List.of());
        when(loadKitTagListPort.loadByKitIds(List.of())).thenReturn(List.of());

        var kitList = service.getKitList(param);

        assertEquals(expectedKitsPage.getPage(), kitList.getPage());
        assertEquals(expectedKitsPage.getSize(), kitList.getSize());
        assertEquals(expectedKitsPage.getSort(), kitList.getSort());
        assertEquals(expectedKitsPage.getOrder(), kitList.getOrder());
        assertEquals(expectedKitsPage.getTotal(), kitList.getTotal());
        assertEquals(expectedKitsPage.getItems().size(), kitList.getItems().size());

        verify(loadPublishedKitListPort, never()).loadPublicKits(anyInt(), anyInt());
        verify(createFileDownloadLinkPort, never()).createDownloadLink(anyString(), any());
    }
}
