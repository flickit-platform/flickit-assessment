package org.flickit.assessment.core.application.port.out.evidence;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public interface CountEvidencesPort {

    int countQuestionsHavingEvidence(UUID assessmentId);

    int countUnresolvedComments(UUID assessmentId);

    Map<Long, Integer> countQuestionnairesQuestionsHavingEvidence(UUID assessmentId, long kitVersionId, ArrayList<Long> questionnaireIds);

    Map<Long, Integer> countQuestionnairesUnresolvedComments(UUID assessmentId, long kitVersionId, ArrayList<Long> questionnaireIds);
}
