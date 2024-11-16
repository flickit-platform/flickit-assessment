package org.flickit.assessment.kit.application.service.kitversion.validatekitversion;

import org.flickit.assessment.common.exception.api.Notification;

public interface KitVersionValidator {

    Notification validate(long kitVersionId);
}
