package org.flickit.assessment.kit.application.service.assessmentkit;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitListStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort.Result;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.flickit.assessment.kit.application.port.out.kittag.LoadKitTagListPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitListService implements GetKitListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadPublishedKitListPort loadPublishedKitListPort;
    private final CountKitListStatsPort countKitStatsPort;
    private final LoadKitTagListPort loadKitTagListPort;
    private final LoadKitLanguagesPort loadKitLanguagesPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<KitListItem> getKitList(Param param) {
        var kitLanguages = resolveKitLanguages(param.getLangs());
        var kitsPage = getPaginatedKits(param, kitLanguages);

        var ids = kitsPage.getItems().stream()
            .map((Result t) -> t.kit().getId()).toList();

        var idToStatsMap = countKitStatsPort.countKitsStats(ids).stream()
            .collect(Collectors.toMap(CountKitListStatsPort.Result::id, Function.identity()));

        var idToKitTagsMap = loadKitTagListPort.loadByKitIds(ids).stream()
            .collect(Collectors.groupingBy(LoadKitTagListPort.Result::kitId));

        var idToKitLanguagesMap = loadKitLanguagesPort.loadByKitIds(ids);

        var items = kitsPage.getItems().stream()
            .map(item -> toAssessmentKit(item,
                idToStatsMap.get(item.kit().getId()),
                idToKitTagsMap.get(item.kit().getId()),
                idToKitLanguagesMap.get(item.kit().getId())))
            .sorted(Comparator.comparing(KitListItem::isPrivate).reversed()
                .thenComparing(KitListItem::title, String.CASE_INSENSITIVE_ORDER))
            .toList();

        return new PaginatedResponse<>(
            items,
            kitsPage.getPage(),
            kitsPage.getSize(),
            kitsPage.getSort(),
            kitsPage.getOrder(),
            kitsPage.getTotal()
        );
    }

    private PaginatedResponse<Result> getPaginatedKits(Param param, Set<KitLanguage> kitLanguages) {
        PaginatedResponse<Result> kitsPage;
        if (param.getIsPrivate() == null) {
            kitsPage = loadPublishedKitListPort.loadPrivateAndPublicKits(param.getCurrentUserId(),
                kitLanguages,
                param.getPage(),
                param.getSize());
        } else if (!param.getIsPrivate())
            kitsPage = loadPublishedKitListPort.loadPublicKits(kitLanguages, param.getPage(), param.getSize());
        else
            kitsPage = loadPublishedKitListPort.loadPrivateKits(param.getCurrentUserId(),
                kitLanguages,
                param.getPage(),
                param.getSize());
        return kitsPage;
    }

    @Nullable
    private Set<KitLanguage> resolveKitLanguages(Collection<String> languages) {
        if (isNotEmpty(languages))
            return languages.stream()
                .map(KitLanguage::valueOf)
                .collect(toSet());
        return null;
    }

    private KitListItem toAssessmentKit(Result item,
                                        CountKitListStatsPort.Result stats,
                                        List<LoadKitTagListPort.Result> kitTags,
                                        List<KitLanguage> kitLanguages) {
        return new KitListItem(
            item.kit().getId(),
            item.kit().getTitle(),
            item.kit().getSummary(),
            item.kit().isPrivate(),
            stats.likes(),
            stats.assessmentsCount(),
            toExpertGroup(item.expertGroup()),
            kitTags.stream()
                .flatMap(result -> result.kitTags().stream())
                .toList(),
            kitLanguages.stream()
                .map(KitLanguage::getTitle)
                .toList());
    }

    private KitListItem.ExpertGroup toExpertGroup(ExpertGroup expertGroup) {
        return new KitListItem.ExpertGroup(expertGroup.getId(),
            expertGroup.getTitle(),
            createFileDownloadLinkPort.createDownloadLink(expertGroup.getPicture(), EXPIRY_DURATION));
    }
}
