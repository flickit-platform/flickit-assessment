package org.flickit.assessment.core.application.service.assessmentkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.exception.NotValidKitContentException;
import org.flickit.assessment.core.application.port.in.assessmentkit.EditKitUseCase;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.domain.AssessmentKit;
import org.flickit.assessment.kit.domain.Questionnaire;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_KIT_KIT_CONTENT_NOT_VALID;

@Service
@Transactional
@RequiredArgsConstructor
public class EditKitService implements EditKitUseCase {

    private final LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;

    @Override
    public void edit(Param param) {
        AssessmentKit loadedKit = loadAssessmentKitInfoPort.load(param.getKitId());

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
