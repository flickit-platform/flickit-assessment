package org.flickit.assessment.core.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase.Result.SpaceListItem;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceListPort.SpaceWithAssessmentCount;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentMoveTargetsService implements GetAssessmentMoveTargetsUseCase {

    private static final int SPACES_LIMIT = 10;

    private final LoadSpacePort loadSpacePort;
    private final LoadSpaceListPort loadSpaceListPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    public Result getSpaceList(Param param) {
        var space = loadSpacePort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND));
        var loadedSpaces = loadSpaceListPort.loadSpaceList(param.getCurrentUserId());

        if (loadedSpaces.isEmpty())
            throw new InvalidStateException(GET_ASSESSMENT_MOVE_TARGETS_NO_SPACE_FOUND); // Can't happen

        final int maxBasicAssessments = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var spaces = Optional.of(extractSpacesWithCapacity(loadedSpaces, space.getId(), maxBasicAssessments))
            .filter(list -> !list.isEmpty())
            .map(list -> list.size() == 1
                ? List.of(toSpaceListItem(list.getFirst().space()))
                : getMultipleBasicsAndPremium(list))
            .orElseThrow(() -> new UpgradeRequiredException(GET_ASSESSMENT_MOVE_TARGETS_NO_SPACE_AVAILABLE));

        return new Result(spaces);
    }

    private List<SpaceWithAssessmentCount> extractSpacesWithCapacity(List<SpaceWithAssessmentCount> items, long currentSpaceId, int maxBasicAssessments) {
        return items.stream()
            .filter(item -> item.space().getId() != currentSpaceId
                && (item.space().getType() == SpaceType.PREMIUM
                || (item.space().getType() == SpaceType.BASIC && item.assessmentCount() < maxBasicAssessments)))
            .limit(SPACES_LIMIT)
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

    private static List<SpaceListItem> getMultipleBasicsAndPremium(List<SpaceWithAssessmentCount> availableSpaces) {
        var selectedSpaceId = selectTargetSpace(availableSpaces);

        return availableSpaces.stream()
            .map(item -> {
                var space = item.space();
                boolean selected = space.getId() == selectedSpaceId;
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

    private static Long selectTargetSpace(List<SpaceWithAssessmentCount> availableSpaces) {
        return availableSpaces.stream()
            .map(SpaceWithAssessmentCount::space)
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                spaces -> spaces.stream()
                    .filter(space -> space.getType().equals(SpaceType.PREMIUM))
                    .findFirst()
                    .or(() -> spaces.stream().findFirst())
                    .map(Space::getId)
                    .orElseThrow(() -> new UpgradeRequiredException(GET_ASSESSMENT_MOVE_TARGETS_NO_SPACE_AVAILABLE)) // can't happen
            ));
    }
}
