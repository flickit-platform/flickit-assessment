package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitLanguagesUseCase;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetKitLanguagesService implements GetKitLanguagesUseCase {

    @Override
    public Result getKitLanguages() {
        List<Result.KitLanguage> languages = Arrays.stream(KitLanguage.values())
            .map(e -> new Result.KitLanguage(e.getCode(), e.getTitle()))
            .toList();
        return new Result(languages);
    }
}
