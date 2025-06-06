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

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.flickit.assessment.users.common.ErrorMessageKey.GET_TOP_SPACES_BASIC_SPACE_ASSESSMENTS_MAX;
import static org.flickit.assessment.users.common.MessageKey.SPACE_DRAFT_TITLE;

@Service
@Transactional
@RequiredArgsConstructor
public class GetTopSpacesService implements GetTopSpacesUseCase {

    private static final int NUMBER_OF_SPACES = 10;

    private final LoadSpaceListPort loadSpaceListPort;
    private final AppSpecProperties appSpecProperties;
    private final CreateSpacePort createSpacePort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Override
    public List<SpaceListItem> getSpaceList(Param param) {
        var portResult = loadSpaceListPort.loadSpaceList(param.getCurrentUserId());

        return topSpaceSelector(portResult, param.getCurrentUserId());
    }

    private List<SpaceListItem> topSpaceSelector(List<LoadSpaceListPort.SpaceWithAssessmentCount> spaces, UUID currentUserId) {
        if (spaces.isEmpty())
            return List.of(createNewSpace(getCurrentLanguage(), currentUserId));

        final int maxBasicAssessments = appSpecProperties.getSpace().getMaxBasicSpaceAssessments();
        var validItems = extractValidItems(spaces, maxBasicAssessments);

        if (spaces.size() == 1 && validItems.isEmpty())
            throw new UpgradeRequiredException(GET_TOP_SPACES_BASIC_SPACE_ASSESSMENTS_MAX);

        return Optional.of(validItems)
            .filter(items -> items.size() == 1)
            .map(items -> List.of(toSpaceListItem(items.getFirst())))
            .orElseGet(() -> getMultipleBasicsAndPremium(validItems));
    }

    private SpaceListItem createNewSpace(KitLanguage lang, UUID currentUserId) {
        var title = MessageBundle.message(SPACE_DRAFT_TITLE, lang);
        var newSpace = toSpace(title, currentUserId);
        var spaceId = createSpacePort.persist(newSpace);

        var spaceUserAccess = toSpaceUserAccess(spaceId, currentUserId);
        createSpaceUserAccessPort.persist(spaceUserAccess);
        return new SpaceListItem(spaceId, newSpace.getTitle(), toType(newSpace.getType()), Boolean.TRUE);
    }

    private static SpaceUserAccess toSpaceUserAccess(long spaceId, UUID currentUserId) {
        return new SpaceUserAccess(spaceId, currentUserId, currentUserId, LocalDateTime.now());
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

    private List<LoadSpaceListPort.SpaceWithAssessmentCount> extractValidItems(List<LoadSpaceListPort.SpaceWithAssessmentCount> items, int maxBasicAssessments) {
        return items.stream()
            .filter(item -> item.space().getType() == SpaceType.PREMIUM || basicSpaceHasCapacity(item, maxBasicAssessments))
            .limit(NUMBER_OF_SPACES)
            .toList();
    }

    private boolean basicSpaceHasCapacity(LoadSpaceListPort.SpaceWithAssessmentCount item, int maxBasicAssessments) {
        return item.space().getType() == SpaceType.BASIC && item.assessmentCount() < maxBasicAssessments;
    }

    private static List<SpaceListItem> getMultipleBasicsAndPremium(List<LoadSpaceListPort.SpaceWithAssessmentCount> validItems) {
        var firstSpaceId = validItems.stream().map(item -> item.space().getId()).findFirst().orElseThrow();

        return validItems.stream()
            .map(item -> {
                var space = item.space();
                boolean isDefault = space.getId().equals(firstSpaceId);
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

    private KitLanguage getCurrentLanguage() {
        return KitLanguage.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    private SpaceListItem toSpaceListItem(LoadSpaceListPort.SpaceWithAssessmentCount item) {
        return new SpaceListItem(
            item.space().getId(),
            item.space().getTitle(),
            new SpaceListItem.Type(item.space().getType().getCode(), item.space().getType().getTitle()),
            Boolean.TRUE
        );
    }

    private SpaceListItem.Type toType(SpaceType type) {
        return new SpaceListItem.Type(type.getCode(), type.getTitle());
    }
}
