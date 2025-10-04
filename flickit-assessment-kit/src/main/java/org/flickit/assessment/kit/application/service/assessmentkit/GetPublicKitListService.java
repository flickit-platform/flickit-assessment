package org.flickit.assessment.kit.application.service.assessmentkit;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublicKitListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitListStatsPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadPublishedKitListPort.Result;
import org.flickit.assessment.kit.application.port.out.kitlanguage.LoadKitLanguagesPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPublicKitListService implements GetPublicKitListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadPublishedKitListPort loadPublishedKitListPort;
    private final CountKitListStatsPort countKitStatsPort;
    private final LoadKitLanguagesPort loadKitLanguagesPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<KitListItem> getPublicKitList(Param param) {
        var kitLanguages = resolveKitLanguages(param.getLangs());
        var kitsPage = loadPublishedKitListPort.loadPublicKits(kitLanguages, param.getPage(), param.getSize());

        var ids = kitsPage.getItems().stream()
            .map((Result t) -> t.kit().getId()).toList();

        var idToStatsMap = countKitStatsPort.countKitsStats(ids).stream()
            .collect(Collectors.toMap(CountKitListStatsPort.Result::id, Function.identity()));

        var idToKitLanguagesMap = loadKitLanguagesPort.loadByKitIds(ids);

        var items = kitsPage.getItems().stream()
            .map(item -> toAssessmentKit(item,
                idToStatsMap.get(item.kit().getId()),
                idToKitLanguagesMap.get(item.kit().getId())))
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
                                        List<KitLanguage> kitLanguages) {
        return new KitListItem(
            item.kit().getId(),
            item.kit().getTitle(),
            item.kit().getSummary(),
            stats.likes(),
            stats.assessmentsCount(),
            toExpertGroup(item.expertGroup()),
            kitLanguages.stream()
                .map(KitLanguage::getTitle)
                .toList(),
            item.kit().getPrice() == 0);
    }

    private KitListItem.ExpertGroup toExpertGroup(ExpertGroup expertGroup) {
        return new KitListItem.ExpertGroup(expertGroup.getId(),
            expertGroup.getTitle(),
            createFileDownloadLinkPort.createDownloadLinkSafe(expertGroup.getPicture(), EXPIRY_DURATION));
    }
}
