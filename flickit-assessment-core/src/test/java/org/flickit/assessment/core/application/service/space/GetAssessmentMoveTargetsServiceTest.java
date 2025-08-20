package org.flickit.assessment.core.application.service.space;

import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase.Param;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MOVE_TARGETS_NO_SPACE_AVAILABLE;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentMoveTargetsServiceTest {

    @InjectMocks
    private GetAssessmentMoveTargetsService service;

    @Mock
    private LoadSpacePort loadSpacePort;

    @Mock
    private LoadSpaceListPort loadSpaceListPort;

    @Spy
    private final AppSpecProperties appSpecProperties = appSpecProperties();

    private final Param param = createParam(GetAssessmentMoveTargetsUseCase.Param.ParamBuilder::build);
    private final int maxBasicAssessments = 1;

    private final Space currentSpace = SpaceMother.createDefaultSpaceWithOwner(param.getCurrentUserId());
    private final Space premiumSpace = SpaceMother.createPremiumSpaceWithOwner(param.getCurrentUserId());
    private final Space basicSpace = SpaceMother.createBasicSpaceWithOwner(param.getCurrentUserId());

    @Test
    void testGetAssessmentMoveTargets_whenSpaceDoesNotExist_thenThrowResourceNotFoundException() {
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getSpaceList(param));
        assertEquals(GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(appSpecProperties,
            loadSpaceListPort);
    }

    @Test
    void testGetAssessmentMoveTargets_whenOneAndDefaultSpaceWithCapacityExists_thenReturnDefaultSpace() {
        Space defaultSpace = SpaceMother.createDefaultSpaceWithOwner(param.getCurrentUserId());
        var resultItem = new LoadSpaceListPort.SpaceWithAssessmentCount(defaultSpace, 0);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(resultItem));

        var result = service.getSpaceList(param);

        var items = result.items();
        assertEquals(1, items.size());
        var returnedItem = items.getFirst();
        assertEquals(defaultSpace.getId(), returnedItem.id());
        assertEquals(defaultSpace.getTitle(), returnedItem.title());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertTrue(returnedItem.selected());
        assertTrue(returnedItem.isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenOneBasicSpaceWithCapacityExists_thenReturnBasicSpace() {
        var resultItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(resultItem));

        var result = service.getSpaceList(param);

        var items = result.items();
        assertEquals(1, items.size());
        var returnedItem = items.getFirst();
        assertEquals(basicSpace.getId(), returnedItem.id());
        assertEquals(basicSpace.getTitle(), returnedItem.title());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertTrue(returnedItem.selected());
        assertFalse(returnedItem.isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenOnlyOneBasicSpaceExistsAndIsFull_thenThrowException() {
        var fullBasicSpace = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(fullBasicSpace));

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.getSpaceList(param));
        assertEquals(GET_ASSESSMENT_MOVE_TARGETS_NO_SPACE_AVAILABLE, throwable.getMessage());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenOnlyOnePremiumSpaceExists_thenReturnIt() {
        var premiumItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(List.of(premiumItem));

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(1, items.size());
        assertEquals(premiumSpace.getId(), items.getFirst().id());
        assertTrue(items.getFirst().selected());
        assertFalse(items.getFirst().isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenBasicSpaceIsFullAndPremiumSpaceExists_thenReturnPremiumSpace() {
        var basicSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);
        var premiumSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 10);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
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
        assertTrue(returnedItem.selected());
        assertFalse(returnedItem.isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenOnePremiumAndOneBasicSpaceWithCapacityExist_thenReturnBothAndPremiumSpaceIsSelected() {
        var basicSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);
        var premiumSpaceItem = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);
        var portResult = List.of(basicSpaceItem, premiumSpaceItem);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(portResult);

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(2, items.size());
        var selectedItem = items.stream()
            .filter(Result.SpaceListItem::selected)
            .findFirst()
            .orElseThrow();
        assertEquals(premiumSpace.getId(), selectedItem.id());
        assertEquals(SpaceType.PREMIUM.getCode(), selectedItem.type().code());
        assertEquals(SpaceType.PREMIUM.getTitle(), selectedItem.type().title());
        assertTrue(selectedItem.selected());
        assertFalse(selectedItem.isDefault());
        assertThat(items)
            .zipSatisfy(portResult, (expected, actual) -> {
                assertEquals(expected.id(), actual.space().getId());
                assertEquals(expected.title(), actual.space().getTitle());
                assertEquals(expected.type().code(), actual.space().getType().getCode());
                assertEquals(expected.type().title(), actual.space().getType().getTitle());
            });
        assertThat(items).filteredOn(Result.SpaceListItem::selected).hasSize(1);

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenTwoBasicSpacesOneFullOneWithCapacityExist_thenReturnSpaceWithCapacity() {
        var basicSpaceWithCapacity = SpaceMother.createBasicSpaceWithOwner(param.getCurrentUserId());
        var basicWithCapacity = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpaceWithCapacity, 0);
        var fullBasicSpace = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, maxBasicAssessments);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId()))
            .thenReturn(List.of(fullBasicSpace, basicWithCapacity));

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(1, items.size());
        var returnedItem = items.getFirst();
        assertEquals(basicSpaceWithCapacity.getId(), returnedItem.id());
        assertEquals(SpaceType.BASIC.getCode(), returnedItem.type().code());
        assertEquals(SpaceType.BASIC.getTitle(), returnedItem.type().title());
        assertTrue(returnedItem.selected());
        assertFalse(returnedItem.isDefault());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenMultipleSpacesWithCapacityExist_thenReturnAllAndOneSelected() {
        var limit = 10;
        var basicSpaces = IntStream.range(0, 6)
            .mapToObj(i -> new LoadSpaceListPort.SpaceWithAssessmentCount(
                SpaceMother.createBasicSpaceWithOwner(param.getCurrentUserId()), 0))
            .toList();
        var premiumSpaces = IntStream.range(0, 5)
            .mapToObj(i -> new LoadSpaceListPort.SpaceWithAssessmentCount(
                SpaceMother.createPremiumSpaceWithOwner(param.getCurrentUserId()), 0))
            .toList();
        var portResult = Stream.concat(basicSpaces.stream(), premiumSpaces.stream()).toList();

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
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
        assertThat(items).filteredOn(Result.SpaceListItem::selected).hasSize(1);
        assertThat(items).filteredOn(i -> i.type().code().equals(SpaceType.BASIC.getCode())).hasSize(6);
        assertThat(items).filteredOn(i -> i.type().code().equals(SpaceType.PREMIUM.getCode())).hasSize(4);
        var selectedItem = items.stream()
            .filter(Result.SpaceListItem::selected)
            .findFirst()
            .orElseThrow();
        assertEquals(SpaceType.PREMIUM.getCode(), selectedItem.type().code());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testGetAssessmentMoveTargets_whenMultiplePremiumSpacesExist_thenReturnAllAndOneSelected() {
        var premiumSpaceItem1 = new LoadSpaceListPort.SpaceWithAssessmentCount(premiumSpace, 0);
        var anotherPremiumSpace = SpaceMother.createPremiumSpaceWithOwner(param.getCurrentUserId());
        var premiumSpaceItem2 = new LoadSpaceListPort.SpaceWithAssessmentCount(anotherPremiumSpace, 0);
        var otherPremiumSpace = SpaceMother.createPremiumSpaceWithOwner(param.getCurrentUserId());
        var premiumSpaceItem3 = new LoadSpaceListPort.SpaceWithAssessmentCount(otherPremiumSpace, 0);
        var portResult = List.of(premiumSpaceItem1, premiumSpaceItem2, premiumSpaceItem3);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(portResult);

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(3, items.size());
        assertThat(items).filteredOn(Result.SpaceListItem::selected).hasSize(1);
        assertTrue(items.getFirst().selected());
        assertFalse(items.getFirst().isDefault());
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
    void testGetAssessmentMoveTargets_whenMultipleBasicSpacesExist_thenReturnAllAndOneSelected() {
        var basicSpaceItem1 = new LoadSpaceListPort.SpaceWithAssessmentCount(basicSpace, 0);
        var anotherBasicSpace = SpaceMother.createBasicSpaceWithOwner(param.getCurrentUserId());
        var basicSpaceItem2 = new LoadSpaceListPort.SpaceWithAssessmentCount(anotherBasicSpace, 0);
        var otherBasicSpace = SpaceMother.createBasicSpaceWithOwner(param.getCurrentUserId());
        var basicSpaceItem3 = new LoadSpaceListPort.SpaceWithAssessmentCount(otherBasicSpace, 0);
        var portResult = List.of(basicSpaceItem1, basicSpaceItem2, basicSpaceItem3);

        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpaceListPort.loadSpaceList(param.getCurrentUserId())).thenReturn(portResult);

        var result = service.getSpaceList(param);
        var items = result.items();
        assertEquals(3, items.size());
        assertThat(items).filteredOn(Result.SpaceListItem::selected).hasSize(1);
        assertTrue(items.getFirst().selected());
        assertFalse(items.getFirst().isDefault());
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

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentMoveTargetsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentMoveTargetsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

}
