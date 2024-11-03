package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

public interface LoadQuestionMayNotBeApplicablePort {

    /**
     * @throws ResourceNotFoundException if the question with given ID is not found.
     */

    boolean loadMayNotBeApplicableById(Long id, long kitVersionId);
}
