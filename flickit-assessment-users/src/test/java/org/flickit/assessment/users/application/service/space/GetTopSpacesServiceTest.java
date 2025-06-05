package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceStatus;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.users.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_BASIC_SPACE_ASSESSMENTS_MAX;
import static org.flickit.assessment.users.common.MessageKey.SPACE_DRAFT_TITLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTopSpacesServiceTest {

    @InjectMocks
    private GetTopSpacesService service;

    @Mock
    private LoadSpaceListPort loadSpaceListPort;

    @Mock
    private CreateSpacePort createSpacePort;

    @Spy
    private final AppSpecProperties appSpecProperties = appSpecProperties();

    private final GetTopSpacesUseCase.Param param = createParam(GetTopSpacesUseCase.Param.ParamBuilder::build);
    private final int maxBasicAssessments = 1;

    private final Space premiumSpace = SpaceMother.premiumSpace(param.getCurrentUserId());
    private final Space basicSpace = SpaceMother.basicSpace(param.getCurrentUserId());

    @Test
    void testGetTopSpaces_whenNoSpacesExistAndLanguageIsEN_thenCreateNewSpace() {
        var expectedTitle = MessageBundle.message(SPACE_DRAFT_TITLE, Locale.ENGLISH);
        ArgumentCaptor<Space> spaceCaptor = ArgumentCaptor.forClass(Space.class);
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of());
        when(createSpacePort.persist(spaceCaptor.capture())).thenReturn(123L);

        var result = service.getSpaceList(param);

        assertEquals(1, result.size());
        assertEquals(123L, result.getFirst().id());

        var capturedSpace = spaceCaptor.getValue();
        assertEquals(expectedTitle, capturedSpace.getTitle());
        assertEquals(generateSlugCode(expectedTitle), capturedSpace.getCode());
        assertEquals(param.getCurrentUserId(), capturedSpace.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedSpace.getLastModifiedBy());
        assertEquals(SpaceStatus.ACTIVE, capturedSpace.getStatus());
        assertEquals(SpaceType.BASIC, capturedSpace.getType());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());
    }

    @Test
    void testGetTopSpaces_whenNoSpacesExistAndLanguageIsFa_thenCreateNewSpace() {
        var expectedTitle = MessageBundle.message(SPACE_DRAFT_TITLE, Locale.of("FA"));
        ArgumentCaptor<Space> spaceCaptor = ArgumentCaptor.forClass(Space.class);
        LocaleContextHolder.setLocale(Locale.of("FA"));

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of());
        when(createSpacePort.persist(spaceCaptor.capture())).thenReturn(123L);

        var result = service.getSpaceList(param);

        assertEquals(1, result.size());
        assertEquals(123L, result.getFirst().id());

        var capturedSpace = spaceCaptor.getValue();
        assertEquals(expectedTitle, capturedSpace.getTitle());
        assertEquals(generateSlugCode(expectedTitle), capturedSpace.getCode());
        assertEquals(param.getCurrentUserId(), capturedSpace.getCreatedBy());
        assertEquals(param.getCurrentUserId(), capturedSpace.getLastModifiedBy());
        assertEquals(SpaceStatus.ACTIVE, capturedSpace.getStatus());
        assertEquals(SpaceType.BASIC, capturedSpace.getType());
        assertNotNull(capturedSpace.getCreationTime());
        assertNotNull(capturedSpace.getLastModificationTime());
    }

    @Test
    void testGetTopSpaces_whenOneBasicSpaceWithCapacity_thenReturnBasicSpaceId() {
        var resultItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(resultItem));

        var result = service.getSpaceList(param);

        assertEquals(1, result.size());
        var returnedItem = result.getFirst();
        assertEquals(basicSpace.getId(), returnedItem.id());
        assertEquals(basicSpace.getTitle(), returnedItem.title());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertTrue(returnedItem.isDefault());

        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testGetTopSpaces_whenOnlyOneBasicSpaceAndItIsFull_thenThrowException() {
        var fullBasic = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(fullBasic));

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.getSpaceList(param));

        assertEquals(GET_TOP_SPACES_BASIC_SPACE_ASSESSMENTS_MAX, throwable.getMessage());

        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testGetTopSpaces_whenOnlyPremiumSpaceExists_thenReturnIt() {
        var premiumItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(premiumItem));

        var result = service.getSpaceList(param);

        assertEquals(1, result.size());
        assertEquals(premiumSpace.getId(), result.getFirst().id());
        assertTrue(result.getFirst().isDefault());
    }


    @Test
    void testGetTopSpaces_whenBasicSpaceIsFullAndPremiumSpaceExists_thenReturnPremiumSpace() {
        var basicSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);
        var premiumSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 10);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(List.of(basicSpaceItem, premiumSpaceItem));

        var result = service.getSpaceList(param);

        assertEquals(1, result.size());
        var returnedItem = result.getFirst();
        assertEquals(premiumSpace.getId(), returnedItem.id());
        assertEquals(SpaceType.PREMIUM.getCode(), returnedItem.type().code());
        assertEquals(SpaceType.PREMIUM.getTitle(), returnedItem.type().title());
        assertEquals(premiumSpace.getTitle(), returnedItem.title());
        assertTrue(returnedItem.isDefault());

        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testGetTopSpaces_whenBasicAndPremiumSpacesWithCapacity_thenReturnBasicSpace() {
        var basicSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);
        var premiumSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(List.of(basicSpaceItem, premiumSpaceItem));

        var result = service.getSpaceList(param);

        assertEquals(2, result.size());
        var returnedItem = result.getFirst();
        assertEquals(basicSpace.getId(), returnedItem.id());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertTrue(returnedItem.isDefault());

        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testGetTopSpaces_whenTwoBasicSpacesOneFullOneWithCapacity_thenReturnSpaceWithCapacity() {
        var fullBasicSpace = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);

        var anotherBasicSpace = SpaceMother.basicSpace(param.getCurrentUserId());
        var basicWithCapacity = new LoadSpaceListPort.SpaceWithAssessmentCount(anotherBasicSpace, 0);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(List.of(fullBasicSpace, basicWithCapacity));

        var result = service.getSpaceList(param);

        assertEquals(1, result.size());
        var returnedItem = result.getFirst();
        assertEquals(anotherBasicSpace.getId(), returnedItem.id());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertTrue(returnedItem.isDefault());

        assertTrue(result.stream().anyMatch(GetTopSpacesUseCase.SpaceListItem::isDefault));

        verifyNoInteractions(createSpacePort);
    }

    @Test
    void testGetTopSpaces_whenMultipleSpacesWithCapacity_thenReturnAllAndNoneDefault() {
        var anotherBasicSpace = SpaceMother.basicSpace(param.getCurrentUserId());
        var basicSpaceItem1 = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);
        var basicSpaceItem2 = new LoadSpaceListPort.SpaceWithAssessmentCount(anotherBasicSpace, 0);

        var anotherPremiumSpace = SpaceMother.premiumSpace(param.getCurrentUserId());
        var premiumSpaceItem1 = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);
        var premiumSpaceItem2 = new LoadSpaceListPort.SpaceWithAssessmentCount(anotherPremiumSpace, 0);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(List.of(basicSpaceItem1, basicSpaceItem2, premiumSpaceItem1, premiumSpaceItem2));

        var result = service.getSpaceList(param);

        assertEquals(4, result.size());

        assertTrue(result.stream().anyMatch(GetTopSpacesUseCase.SpaceListItem::isDefault));

        verifyNoInteractions(createSpacePort);
    }


    private AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        properties.setSpace(new AppSpecProperties.Space());
        properties.getSpace().setMaxBasicSpaceAssessments(maxBasicAssessments);
        return properties;
    }

    private GetTopSpacesUseCase.Param createParam(Consumer<GetTopSpacesUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetTopSpacesUseCase.Param.ParamBuilder paramBuilder() {
        return GetTopSpacesUseCase.Param.builder()
            .currentUserId(UUID.randomUUID());
    }
}
