package org.flickit.assessment.kit.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort.UpdateOrderParam.MeasureOrder;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort.UpdateOrderParam.QuestionnaireOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQuestionnaireOrdersService implements UpdateQuestionnaireOrdersUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final UpdateQuestionnairePort updateQuestionnairePort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final LoadMeasurePort loadMeasurePort;
    private final UpdateMeasurePort updateMeasurePort;

    @Override
    public void changeOrders(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());

        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateQuestionnairePort.updateOrders(toUpdatePortParam(param));

        var measureUpdatePortParam = createMeasureUpdateParam(param, kitVersion.getKit().getId());
        updateMeasurePort.updateOrders(measureUpdatePortParam);
    }

    private UpdateQuestionnairePort.UpdateOrderParam toUpdatePortParam(Param param) {
        var questionnaireOrders = param.getOrders().stream()
            .map(e -> new QuestionnaireOrder(e.getId(), e.getIndex()))
            .toList();
        return new UpdateQuestionnairePort.UpdateOrderParam(questionnaireOrders,
            param.getKitVersionId(),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }

    private UpdateMeasurePort.UpdateOrderParam createMeasureUpdateParam(Param param, Long kitId) {
        var questionnaireIdToCode = loadQuestionnairesPort.loadByKitId(kitId).stream()
            .collect(toMap(Questionnaire::getId, Questionnaire::getCode));
        var codeToMeasureId = loadMeasurePort.loadAllByKitVersionId(param.getKitVersionId()).stream()
            .collect(toMap(Measure::getCode, Measure::getId));
        return toMeasureUpdatePortParam(param, questionnaireIdToCode, codeToMeasureId);
    }

    private UpdateMeasurePort.UpdateOrderParam toMeasureUpdatePortParam(Param param,
                                                                        Map<Long, String> questionnaireIdToCode,
                                                                        Map<String, Long> codeToMeasureId) {
        var measureOrders = param.getOrders().stream()
            .map(e -> {
                var code = questionnaireIdToCode.get(e.getId());
                return new MeasureOrder(codeToMeasureId.get(code), e.getIndex());
            }).toList();
        return new UpdateMeasurePort.UpdateOrderParam(measureOrders,
            param.getKitVersionId(),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
