package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceStatus;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_BASIC_SPACE_ASSESSMENTS_MAX;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTopSpacesService implements GetTopSpacesUseCase {

    private static final int NUMBER_OF_SPACES = 10;

    private final LoadSpaceListPort loadSpaceListPort;
    private final AppSpecProperties appSpecProperties;
    private final CreateSpacePort createSpacePort;

    @Override
    public List<SpaceListItem> getSpaceList(Param param) {
        var portResult = loadSpaceListPort.loadSpaceList(param.getCurrentUserId());

        var spaces = topSpaceSelector(portResult, param.getCurrentUserId());

        return spaces;
    }

    private List<SpaceListItem> topSpaceSelector(List<LoadSpaceListPort.SpaceWithAssessmentCount> items, UUID currentUserId) {
        var validItems = items.stream()
                .filter(item -> (item.space().getType() == SpaceType.PREMIUM || basicSpaceHasCapacity(item)))
                .limit(NUMBER_OF_SPACES)
                .toList();

        if (items.isEmpty())
            return List.of(createNewSpace(KitLanguage.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase()), currentUserId));
        else if (validItems.size() == 1 && validItems.getFirst().space().getType() == SpaceType.BASIC && basicSpaceHasCapacity(validItems.getFirst()))
            return List.of(new SpaceListItem(validItems.getFirst().space().getId(), validItems.getFirst().space().getTitle(), validItems.getFirst().space().getType(), Boolean.TRUE));
        else if (items.size() == 1 && validItems.isEmpty())
            throw new UpgradeRequiredException(GET_TOP_SPACES_BASIC_SPACE_ASSESSMENTS_MAX);
        else if (validItems.size() == 1 && validItems.getFirst().space().getType() == SpaceType.PREMIUM)
            return List.of(new SpaceListItem(validItems.getFirst().space().getId(), validItems.getFirst().space().getTitle(), validItems.getFirst().space().getType(), Boolean.TRUE));
        else if (validItems.size() > 1
                && validItems.stream().anyMatch(this::spaceIsPremium)
                && validItems.stream().anyMatch(this::basicSpaceHasCapacity)) {
            return getMultipleBasicsAndPremium(validItems);
        }

        else return List.of();
    }

    private static List<SpaceListItem> getMultipleBasicsAndPremium(List<LoadSpaceListPort.SpaceWithAssessmentCount> validItems) {
        var spaces = validItems.stream()
                .map(item -> new SpaceListItem(item.space().getId(), item.space().getTitle(), item.space().getType(), Boolean.FALSE))
                .toList();

        var firstSpaceId = spaces.getFirst().id();

        return spaces.stream()
                .map(item -> item.id() == firstSpaceId
                        ? new SpaceListItem(item.id(), item.title(), item.type(), Boolean.TRUE)
                        : item)
                .toList();
    }

    private SpaceListItem createNewSpace(KitLanguage lang, UUID currentUserId) {
        var title = "title";
        var newSpace = new Space(null,
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
        var spaceId = createSpacePort.persist(newSpace);
        return new SpaceListItem(spaceId, newSpace.getTitle(), newSpace.getType(), Boolean.TRUE);
    }

    private boolean basicSpaceHasCapacity(LoadSpaceListPort.SpaceWithAssessmentCount item) {
        return item.space().getType() == SpaceType.BASIC
                && item.assessmentCount() < appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
    }

    private boolean spaceIsPremium(LoadSpaceListPort.SpaceWithAssessmentCount item) {
        return item.space().getType() == SpaceType.PREMIUM;
    }
}
