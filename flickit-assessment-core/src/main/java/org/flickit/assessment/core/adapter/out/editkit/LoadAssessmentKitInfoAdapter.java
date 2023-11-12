package org.flickit.assessment.core.adapter.out.editkit;

import lombok.AllArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadAssessmentKitMaturityLevelModelsByKitPort;
import org.flickit.assessment.core.application.port.out.qualityattribute.LoadAssessmentKitAttributeModelsBySubjectPort;
import org.flickit.assessment.core.application.port.out.question.LoadAssessmentKitQuestionModelsByQuestionnairePort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadAssessmentKitQuestionnaireModelsByKitPort;
import org.flickit.assessment.core.application.port.out.subject.LoadAssessmentKitSubjectModelsByKitPort;
import org.flickit.assessment.kit.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class LoadAssessmentKitInfoAdapter implements LoadAssessmentKitInfoPort {

    private final LoadAssessmentKitQuestionnaireModelsByKitPort loadAssessmentKitQuestionnaireModelsByKitPort;
    private final LoadAssessmentKitAttributeModelsBySubjectPort loadAssessmentKitAttributeModelsBySubjectPort;
    private final LoadAssessmentKitQuestionModelsByQuestionnairePort loadAssessmentKitQuestionModelsByQuestionnairePort;
    private final LoadAssessmentKitSubjectModelsByKitPort loadAssessmentKitSubjectModelsByKitPort;
    private final LoadAssessmentKitMaturityLevelModelsByKitPort loadAssessmentKitMaturityLevelModelsByKitPort;

    @Override
    public AssessmentKit load(Long kitId) {
        List<Questionnaire> questionnaires = loadAssessmentKitQuestionnaireModelsByKitPort.load(kitId);
        List<Subject> subjects = loadAssessmentKitSubjectModelsByKitPort.load(kitId);
        List<Question> questions = questionnaires.stream()
            .flatMap(questionnaire -> loadAssessmentKitQuestionModelsByQuestionnairePort.load(questionnaire.getId()).stream())
            .toList();
        List<Attribute> attributes = subjects.stream()
            .flatMap(subject -> loadAssessmentKitAttributeModelsBySubjectPort.load(subject.getId()).stream())
            .toList();
        List<Level> levels = loadAssessmentKitMaturityLevelModelsByKitPort.load(kitId);

        return new AssessmentKit(
            questionnaires,
            attributes,
            questions,
            subjects,
            levels,
            Boolean.FALSE
        );
    }
}
