package org.flickit.assessment.core.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase.Result.SpaceListItem;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceListPort.SpaceWithAssessmentCount;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentMoveTargetsService implements GetAssessmentMoveTargetsUseCase {

    private static final int SPACES_LIMIT = 10;

    private final LoadSpacePort loadSpacePort;
    private final LoadSpaceListPort loadSpaceListPort;
    private final AppSpecProperties appSpecProperties;
    private final LoadAssessmentUsersPort loadAssessmentUsersPort;

    @Override
    public Result getTargetSpaces(Param param) {
        var space = loadSpacePort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND));
        var ownedSpaces = loadSpaceListPort.loadByOwnerId(param.getCurrentUserId());

        var targetSpaces = extractAvailableSpaces(ownedSpaces, space.getId(), param);
        if (targetSpaces.isEmpty())
            return new Result(List.of());

        var selectedSpaceId = selectTargetSpace(targetSpaces);
        return toResult(targetSpaces, selectedSpaceId);
    }

    private List<Space> extractAvailableSpaces(List<SpaceWithAssessmentCount> items, long currentSpaceId, Param param) {
        final int maxBasicAssessments = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        return items.stream()
            .filter(item -> {
                boolean isEligibleBasicSpace = item.space().getType() == SpaceType.BASIC && !item.space().isDefault()
                        && item.assessmentCount() < maxBasicAssessments;
                boolean isEligibleDefaultSpace = item.space().isDefault()
                        && !loadAssessmentUsersPort.hasNonSpaceOwnerAccess(param.getAssessmentId());

                return (item.space().getId() != currentSpaceId)
                        && (item.space().getType() == SpaceType.PREMIUM || isEligibleDefaultSpace || isEligibleBasicSpace);
            })
                .map(SpaceWithAssessmentCount::space)
            .limit(SPACES_LIMIT)
            .toList();
    }

    private Result toResult(List<Space> availableSpaces, long selectedSpaceId) {
        var targets = availableSpaces.stream()
                .map(space -> {
                boolean selected = space.getId() == selectedSpaceId;
                    return toSpaceListItem(space, selected);
            })
            .toList();
        return new Result(targets);
    }

    private static Long selectTargetSpace(List<Space> availableSpaces) {
        return availableSpaces.stream()
                .filter(space -> space.getType() == SpaceType.PREMIUM)
                .findFirst()
                .orElseGet(availableSpaces::getFirst)
                .getId();
    }

    private static SpaceListItem toSpaceListItem(Space space, boolean selected) {
        return new SpaceListItem(
                space.getId(),
                space.getTitle(),
                SpaceListItem.Type.of(space.getType()),
                selected,
                space.isDefault());
    }
}
