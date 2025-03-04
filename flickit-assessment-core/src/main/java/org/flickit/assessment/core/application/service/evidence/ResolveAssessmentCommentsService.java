package org.flickit.assessment.core.application.service.evidence;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.evidence.ResolveAssessmentCommentsUseCase;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ResolveAssessmentCommentsService implements ResolveAssessmentCommentsUseCase {

    @Override
    public void resolveAllComments(Param param) {

    }
}
