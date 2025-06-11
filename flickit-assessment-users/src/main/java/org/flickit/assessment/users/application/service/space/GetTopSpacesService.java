package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceStatus;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase.Result.SpaceListItem;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_NO_SPACE_AVAILABLE;
import static org.flickit.assessment.users.common.MessageKey.SPACE_DRAFT_TITLE;

@Service
@Transactional
@RequiredArgsConstructor
public class GetTopSpacesService implements GetTopSpacesUseCase {

    private static final int TOP_SPACES_LIMIT = 10;

    private final LoadSpaceListPort loadSpaceListPort;
    private final AppSpecProperties appSpecProperties;
    private final CreateSpacePort createSpacePort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Override
    public Result getSpaceList(Param param) {
        var loadedSpaces = loadSpaceListPort.loadSpaceList(param.getCurrentUserId());
        var lang = KitLanguage.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());

        if (loadedSpaces.isEmpty())
            return new Result(List.of(createNewSpace(lang, param.getCurrentUserId())));

        final int maxBasicAssessments = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var availableSpaces = extractSpacesWithCapacity(loadedSpaces, maxBasicAssessments);

        if (availableSpaces.isEmpty())
            throw new UpgradeRequiredException(GET_TOP_SPACES_NO_SPACE_AVAILABLE);

        var spaces = Optional.of(availableSpaces)
            .filter(items -> items.size() == 1)
            .map(items -> List.of(toSpaceListItem(items.getFirst())))
            .orElseGet(() -> getMultipleBasicsAndPremium(availableSpaces));
        return new Result(spaces);
    }

    private SpaceListItem createNewSpace(KitLanguage lang, UUID currentUserId) {
        var title = MessageBundle.message(SPACE_DRAFT_TITLE, lang);
        var newSpace = toSpace(title, currentUserId);
        var spaceId = createSpacePort.persist(newSpace);

        var spaceUserAccess = new SpaceUserAccess(spaceId, currentUserId, currentUserId, LocalDateTime.now());
        createSpaceUserAccessPort.persist(spaceUserAccess);

        return new SpaceListItem(spaceId,
            newSpace.getTitle(),
            SpaceListItem.Type.of(newSpace.getType()),
            Boolean.TRUE);
    }

    private static Space toSpace(String title, UUID currentUserId) {
        return new Space(null,
            generateSlugCode(title),
            title,
            SpaceType.BASIC,
            currentUserId,
            SpaceStatus.ACTIVE,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            currentUserId,
            currentUserId
        );
    }

    private List<LoadSpaceListPort.SpaceWithAssessmentCount> extractSpacesWithCapacity(List<LoadSpaceListPort.SpaceWithAssessmentCount> items, int maxBasicAssessments) {
        return items.stream()
            .filter(item -> item.space().getType() == SpaceType.PREMIUM
                || (item.space().getType() == SpaceType.BASIC && item.assessmentCount() < maxBasicAssessments))
            .limit(TOP_SPACES_LIMIT)
            .toList();
    }

    private static List<SpaceListItem> getMultipleBasicsAndPremium(List<LoadSpaceListPort.SpaceWithAssessmentCount> availableSpaces) {
        var selectedSpaceId = availableSpaces.stream()
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

        return availableSpaces.stream()
            .map(item -> {
                var space = item.space();
                boolean isDefault = space.getId().equals(selectedSpaceId);
                return new SpaceListItem(
                    space.getId(),
                    space.getTitle(),
                    new SpaceListItem.Type(
                        space.getType().getCode(),
                        space.getType().getTitle()
                    ),
                    isDefault
                );
            })
            .toList();
    }

    private SpaceListItem toSpaceListItem(LoadSpaceListPort.SpaceWithAssessmentCount item) {
        return new SpaceListItem(
            item.space().getId(),
            item.space().getTitle(),
            SpaceListItem.Type.of(item.space().getType()),
            Boolean.TRUE);
    }
}
