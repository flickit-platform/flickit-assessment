package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase.Result;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.users.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_NO_SPACE_AVAILABLE;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_SPACE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTopSpacesServiceTest {

    @InjectMocks
    private GetTopSpacesService service;

    @Mock
    private LoadSpaceListPort loadSpaceListPort;

    @Spy
    private final AppSpecProperties appSpecProperties = appSpecProperties();

    private final GetTopSpacesUseCase.Param param = createParam(GetTopSpacesUseCase.Param.ParamBuilder::build);
    private final int maxBasicAssessments = 1;

    private final Space premiumSpace = SpaceMother.premiumSpace(param.getCurrentUserId());
    private final Space basicSpace = SpaceMother.basicSpace(param.getCurrentUserId());

    @Test
    void testGetTopSpaces_whenSpaceDoesNotExist_thenThrowResourceNotFoundException() {
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getSpaceList(param));
        assertEquals(GET_TOP_SPACES_SPACE_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(appSpecProperties);
    }

    @Test
    void testGetTopSpaces_whenOneBasicSpaceWithCapacityExists_thenReturnBasicSpaceId() {
        var resultItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(resultItem));

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(1, items.size());
        var returnedItem = items.getFirst();
        assertEquals(basicSpace.getId(), returnedItem.id());
        assertEquals(basicSpace.getTitle(), returnedItem.title());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertTrue(returnedItem.isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenOnlyOneBasicSpaceExistsAndIsFull_thenThrowException() {
        var fullBasicSpace = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(fullBasicSpace));

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.getSpaceList(param));
        assertEquals(GET_TOP_SPACES_NO_SPACE_AVAILABLE, throwable.getMessage());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenOnlyOnePremiumSpaceExists_thenReturnIt() {
        var premiumItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(premiumItem));

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(1, items.size());
        assertEquals(premiumSpace.getId(), items.getFirst().id());
        assertTrue(items.getFirst().isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenBasicSpaceIsFullAndPremiumSpaceExists_thenReturnPremiumSpace() {
        var basicSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);
        var premiumSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 10);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(List.of(basicSpaceItem, premiumSpaceItem));

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(1, items.size());
        var returnedItem = items.getFirst();
        assertEquals(premiumSpace.getId(), returnedItem.id());
        assertEquals(SpaceType.PREMIUM.getCode(), returnedItem.type().code());
        assertEquals(SpaceType.PREMIUM.getTitle(), returnedItem.type().title());
        assertEquals(premiumSpace.getTitle(), returnedItem.title());
        assertTrue(returnedItem.isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenOnePremiumAndOneBasicSpaceWithCapacityExist_thenReturnBothAndPremiumSpaceIsDefault() {
        var basicSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);
        var premiumSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);
        var portResult = List.of(basicSpaceItem, premiumSpaceItem);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(portResult);

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(2, items.size());
        var defaultItem = items.stream()
            .filter(Result.SpaceListItem::isDefault)
            .findFirst()
            .orElseThrow();
        assertEquals(premiumSpace.getId(), defaultItem.id());
        assertEquals(SpaceType.PREMIUM.getCode(), defaultItem.type().code());
        assertEquals(SpaceType.PREMIUM.getTitle(), defaultItem.type().title());
        assertTrue(defaultItem.isDefault());
        assertThat(items)
            .zipSatisfy(portResult, (expected, actual) -> {
                assertEquals(expected.id(), actual.space().getId());
                assertEquals(expected.title(), actual.space().getTitle());
                assertEquals(expected.type().code(), actual.space().getType().getCode());
                assertEquals(expected.type().title(), actual.space().getType().getTitle());
            });
        assertThat(items).filteredOn(Result.SpaceListItem::isDefault).hasSize(1);

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenTwoBasicSpacesOneFullOneWithCapacityExist_thenReturnSpaceWithCapacity() {
        var basicSpaceWithCapacity = SpaceMother.basicSpace(param.getCurrentUserId());
        var basicWithCapacity = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpaceWithCapacity, 0);
        var fullBasicSpace = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(List.of(fullBasicSpace, basicWithCapacity));

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(1, items.size());
        var returnedItem = items.getFirst();
        assertEquals(basicSpaceWithCapacity.getId(), returnedItem.id());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertTrue(returnedItem.isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenMultipleSpacesWithCapacityExist_thenReturnAllAndOneDefault() {
        var limit = 10;
        var basicSpaces = IntStream.range(0, 6)
            .mapToObj(i -> new LoadSpaceListPort.SpaceWithAssessmentCount(
                SpaceMother.basicSpace(param.getCurrentUserId()), 0))
            .toList();
        var premiumSpaces = IntStream.range(0, 5)
            .mapToObj(i -> new LoadSpaceListPort.SpaceWithAssessmentCount(
                SpaceMother.premiumSpace(param.getCurrentUserId()), 0))
            .toList();
        var portResult = Stream.concat(basicSpaces.stream(), premiumSpaces.stream()).toList();

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(portResult);

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(limit, items.size());
        assertThat(items)
            .zipSatisfy(portResult.stream().limit(limit).toList(), (expected, actual) -> {
                assertEquals(expected.id(), actual.space().getId());
                assertEquals(expected.title(), actual.space().getTitle());
                assertEquals(expected.type().code(), actual.space().getType().getCode());
                assertEquals(expected.type().title(), actual.space().getType().getTitle());
            });
        assertThat(items).filteredOn(Result.SpaceListItem::isDefault).hasSize(1);
        assertThat(items).filteredOn(i -> i.type().code().equals(SpaceType.BASIC.getCode())).hasSize(6);
        assertThat(items).filteredOn(i -> i.type().code().equals(SpaceType.PREMIUM.getCode())).hasSize(4);
        var defaultItem = items.stream()
            .filter(Result.SpaceListItem::isDefault)
            .findFirst()
            .orElseThrow();
        assertEquals(SpaceType.PREMIUM.getCode(), defaultItem.type().code());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenMultiplePremiumSpacesExist_thenReturnAllAndOneDefault() {
        var premiumSpaceItem1 = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);
        var anotherPremiumSpace = SpaceMother.premiumSpace(param.getCurrentUserId());
        var premiumSpaceItem2 = new LoadSpaceListPort.SpaceWithAssessmentCount(anotherPremiumSpace, 0);
        var otherPremiumSpace = SpaceMother.premiumSpace(param.getCurrentUserId());
        var premiumSpaceItem3 = new LoadSpaceListPort.SpaceWithAssessmentCount(otherPremiumSpace, 0);
        var portResult = List.of(premiumSpaceItem1, premiumSpaceItem2, premiumSpaceItem3);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(portResult);

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(3, items.size());
        assertThat(items).filteredOn(Result.SpaceListItem::isDefault).hasSize(1);
        assertTrue(items.getFirst().isDefault());
        assertThat(items)
            .zipSatisfy(portResult, (expected, actual) -> {
                assertEquals(expected.id(), actual.space().getId());
                assertEquals(expected.title(), actual.space().getTitle());
                assertEquals(expected.type().code(), actual.space().getType().getCode());
                assertEquals(expected.type().title(), actual.space().getType().getTitle());
            });

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetTopSpaces_whenMultipleBasicSpacesExist_thenReturnAllAndOneDefault() {
        var basicSpaceItem1 = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);
        var anotherBasicSpace = SpaceMother.basicSpace(param.getCurrentUserId());
        var basicSpaceItem2 = new LoadSpaceListPort.SpaceWithAssessmentCount(anotherBasicSpace, 0);
        var otherBasicSpace = SpaceMother.basicSpace(param.getCurrentUserId());
        var basicSpaceItem3 = new LoadSpaceListPort.SpaceWithAssessmentCount(otherBasicSpace, 0);
        var portResult = List.of(basicSpaceItem1, basicSpaceItem2, basicSpaceItem3);

        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(portResult);

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(3, items.size());
        assertThat(items).filteredOn(Result.SpaceListItem::isDefault).hasSize(1);
        assertTrue(items.getFirst().isDefault());
        assertThat(items)
            .zipSatisfy(portResult, (expected, actual) -> {
                assertEquals(expected.id(), actual.space().getId());
                assertEquals(expected.title(), actual.space().getTitle());
                assertEquals(expected.type().code(), actual.space().getType().getCode());
                assertEquals(expected.type().title(), actual.space().getType().getTitle());
            });

        verify(appSpecProperties, times(1)).getSpace();
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
