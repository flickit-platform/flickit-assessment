package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase.Result.SpaceListItem;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_NO_SPACE_AVAILABLE;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_NO_SPACE_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class GetTopSpacesService implements GetTopSpacesUseCase {

    private static final int TOP_SPACES_LIMIT = 10;

    private final LoadSpaceListPort loadSpaceListPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    public Result getSpaceList(Param param) {
        var loadedSpaces = loadSpaceListPort.loadSpaceList(param.getCurrentUserId());

        if (loadedSpaces.isEmpty())
            throw new InvalidStateException(GET_TOP_SPACES_NO_SPACE_FOUND); // Can't happen

        final int maxBasicAssessments = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var spaces = Optional.ofNullable(extractSpacesWithCapacity(loadedSpaces, maxBasicAssessments))
            .filter(list -> !list.isEmpty())
            .map(list -> list.size() == 1
                ? List.of(toSpaceListItem(list.getFirst().space()))
                : getMultipleBasicsAndPremium(list))
            .orElseThrow(() -> new UpgradeRequiredException(GET_TOP_SPACES_NO_SPACE_AVAILABLE));

        return new Result(spaces);
    }

    private List<LoadSpaceListPort.SpaceWithAssessmentCount> extractSpacesWithCapacity(List<LoadSpaceListPort.SpaceWithAssessmentCount> items, int maxBasicAssessments) {
        return items.stream()
            .filter(item -> item.space().getType() == SpaceType.PREMIUM
                || (item.space().getType() == SpaceType.BASIC && item.assessmentCount() < maxBasicAssessments))
            .limit(TOP_SPACES_LIMIT)
            .toList();
    }

    private SpaceListItem toSpaceListItem(Space space) {
        return new SpaceListItem(
            space.getId(),
            space.getTitle(),
            SpaceListItem.Type.of(space.getType()),
            true,
            space.isDefault());
    }

    private static List<SpaceListItem> getMultipleBasicsAndPremium(List<LoadSpaceListPort.SpaceWithAssessmentCount> availableSpaces) {
        var selectedSpaceId = selectTargetSpace(availableSpaces);

        return availableSpaces.stream()
            .map(item -> {
                var space = item.space();
                boolean selected = space.getId().equals(selectedSpaceId);
                return new SpaceListItem(
                    space.getId(),
                    space.getTitle(),
                    new SpaceListItem.Type(
                        space.getType().getCode(),
                        space.getType().getTitle()
                    ),
                    selected,
                    space.isDefault()
                );
            })
            .toList();
    }

    private static Long selectTargetSpace(List<LoadSpaceListPort.SpaceWithAssessmentCount> availableSpaces) {
        return availableSpaces.stream()
            .map(LoadSpaceListPort.SpaceWithAssessmentCount::space)
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                spaces -> spaces.stream()
                    .filter(space -> space.getType().equals(SpaceType.PREMIUM))
                    .findFirst()
                    .or(() -> spaces.stream().findFirst())
                    .map(Space::getId)
                    .orElseThrow(() -> new UpgradeRequiredException(GET_TOP_SPACES_NO_SPACE_AVAILABLE)) // can't happen
            ));
    }
}
