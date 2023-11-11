package org.flickit.assessment.core.application.service.assessmentkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flickit.assessment.core.application.exception.NotValidKitContentException;
import org.flickit.assessment.core.application.port.in.assessmentkit.EditKitUseCase;
import org.flickit.assessment.kit.domain.AssessmentKit;
import org.flickit.assessment.kit.domain.Questionnaire;

import java.util.List;

import static org.flickit.assessment.core.common.ErrorMessageKey.EDIT_KIT_KIT_CONTENT_NOT_VALID;

public class EditKitService implements EditKitUseCase {

    public static AssessmentKit parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKit.class);
        } catch (JsonProcessingException e) {
            throw new NotValidKitContentException(EDIT_KIT_KIT_CONTENT_NOT_VALID);
        }
    }

    @Override
    public void edit(Param param) {
        AssessmentKit kitModel = parseJson(param.getContent());

        if (kitModel != null) {
            List<Questionnaire> questionnaires = kitModel.getQuestionnaires();
        }
    }

}
