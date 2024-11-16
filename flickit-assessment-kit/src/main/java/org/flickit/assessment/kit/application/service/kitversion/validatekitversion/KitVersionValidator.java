package org.flickit.assessment.kit.application.service.kitversion.validatekitversion;

import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.KitVersion;

public interface KitVersionValidator {

    Notification validate(KitVersion kitVersion);
}
