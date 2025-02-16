package org.flickit.assessment.advice.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessmentkit.LoadAssessmentKitLanguagePort;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.advice.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;

@Component("adviceAssessmentKitPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements LoadAssessmentKitLanguagePort {

    private final AssessmentKitJpaRepository repository;

    @Override
    public KitLanguage loadKitLanguage(long kitVersionId) {
        int languageId = repository.loadKitLanguageId(kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));
        return KitLanguage.valueOfById(languageId);
    }
}
