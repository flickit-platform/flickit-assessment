package org.flickit.assessment.core.application.service.attribute;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAttributesServiceTest {

    @InjectMocks
    private GetAssessmentAttributesService getAssessmentAttributesService;

    @Test
    void testGetAssessmentAttributesService_whenCurrentUserDoesNotHaveAccess_thenThrowsAccessDeniedException() {
        // TODO: Write your test case
    }
}
