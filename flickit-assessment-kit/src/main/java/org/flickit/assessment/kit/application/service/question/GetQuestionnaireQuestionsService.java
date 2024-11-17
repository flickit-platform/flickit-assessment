package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionnaireQuestionsUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionnaireQuestionsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionnaireQuestionsService implements GetQuestionnaireQuestionsUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadQuestionnaireQuestionsPort loadQuestionnaireQuestionsPort;

    @Override
    public PaginatedResponse<QuestionListItem> getQuestionnaireQuestions(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var response = loadQuestionnaireQuestionsPort.loadQuestionnaireQuestions(toParam(param));
        List<QuestionListItem> items = response.getItems().stream()
            .map(e -> new QuestionListItem(e.getId(),
                e.getTitle(),
                e.getIndex(),
                e.getHint(),
                e.getMayNotBeApplicable(),
                e.getAdvisable(),
                e.getAnswerRangeId()))
            .toList();

        return new PaginatedResponse<>(items,
            response.getPage(),
            response.getSize(),
            response.getSort(),
            response.getOrder(),
            response.getTotal());
    }

    private static LoadQuestionnaireQuestionsPort.Param toParam(Param param) {
        return new LoadQuestionnaireQuestionsPort.Param(param.getQuestionnaireId(),
            param.getKitVersionId(),
            param.getPage(),
            param.getSize());
    }
}
