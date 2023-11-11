package org.flickit.assessment.core.application.service.assessmentkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.exception.NotValidKitContentException;
import org.flickit.assessment.core.application.port.in.assessmentkit.EditKitUseCase;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsByKitPort;
import org.flickit.assessment.core.application.port.out.qualityattribute.LoadAttributesByQuestionIdPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByKitIdPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.assessment.kit.domain.AssessmentKit;
import org.flickit.assessment.kit.domain.Attribute;
import org.flickit.assessment.kit.domain.Questionnaire;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_KIT_KIT_CONTENT_NOT_VALID;

@Service
@Transactional
@RequiredArgsConstructor
public class EditKitService implements EditKitUseCase {

    private final LoadQuestionnairesByKitIdPort loadQuestionnairesByKitIdPort;
    private final LoadAttributesByQuestionIdPort loadAttributesByQuestionIdPort;
    private final LoadQuestionsBySubjectPort loadQuestionsBySubjectPort;
    private final LoadSubjectByAssessmentKitIdPort loadSubjectByAssessmentKitIdPort;
    private final LoadMaturityLevelsByKitPort loadMaturityLevelsByKitPort;

    @Override
    public void edit(Param param) {
        List<Questionnaire> loadedQuestionnaires = loadQuestionnairesByKitIdPort.load(param.getKitId());
        List<Subject> loadedSubjects = loadSubjectByAssessmentKitIdPort.loadByAssessmentKitId(param.getKitId());
        List<Question> loadedQuestions = loadedSubjects.stream()
            .flatMap(subject -> loadQuestionsBySubjectPort.loadQuestionsBySubject(subject.getId()).stream())
            .toList();
        List<Attribute> loadedAttributes = loadedQuestions.stream()
            .flatMap(question -> loadAttributesByQuestionIdPort.load(question.getId()).stream())
            .toList();
        List<MaturityLevel> loadedMaturityLevels = loadMaturityLevelsByKitPort.loadByKitId(param.getKitId());

        AssessmentKit kitModel = parseJson(param.getContent());
        if (kitModel != null) {
            List<Questionnaire> questionnaires = kitModel.getQuestionnaires();
        }
    }

    private AssessmentKit parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKit.class);
        } catch (JsonProcessingException e) {
            throw new NotValidKitContentException(EDIT_KIT_KIT_CONTENT_NOT_VALID);
        }
    }

}
