package org.flickit.assessment.kit.application.service.answerrange;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetAnswerRangeListServiceTest {

    @InjectMocks
    GetAnswerRangeListService service;


    void testGetAnswerRangeListService_CurrentUserDoesNotHaveAccess_ThrowsAccessDeniedException() {}
}
