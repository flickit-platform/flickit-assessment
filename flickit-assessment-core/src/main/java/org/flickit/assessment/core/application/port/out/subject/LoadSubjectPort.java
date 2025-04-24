package org.flickit.assessment.core.application.port.out.subject;

import jakarta.annotation.Nullable;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.Subject;

import java.util.Optional;

public interface LoadSubjectPort {

    Optional<Subject> load(long id, long kitVersionId, @Nullable KitLanguage language);
}
