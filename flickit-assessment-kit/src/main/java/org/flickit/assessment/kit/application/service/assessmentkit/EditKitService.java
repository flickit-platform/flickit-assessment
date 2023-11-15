package org.flickit.assessment.kit.application.service.assessmentkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.exception.NotValidKitContentException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_VALID;

@Service
@Transactional
@RequiredArgsConstructor
public class EditKitService implements UpdateKitByDslUseCase {

    private final LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;

    @Override
    public void update(Param param) {
        AssessmentKit loadedKit = loadAssessmentKitInfoPort.load(param.getKitId());
        AssessmentKit kitModel = parseJson(param.getDslContent());

        if (kitModel != null) {

        }
    }

    private AssessmentKit parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKit.class);
        } catch (JsonProcessingException e) {
            throw new NotValidKitContentException(UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_VALID
            );
        }
    }

}
