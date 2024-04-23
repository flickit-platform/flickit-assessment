package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttrLevelQuestionsInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttrLevelQuestionsInfoPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttrLevelQuestionsInfoService implements GetAttrLevelQuestionsInfoUseCase {

    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadAttrLevelQuestionsInfoPort loadAttrLevelQuestionsInfoPort;

    @Override
    public Result getAttrLevelQuestionsInfo(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        LoadAttrLevelQuestionsInfoPort.Result attrLevelQuestionsInfo =
            loadAttrLevelQuestionsInfoPort.loadAttrLevelQuestionsInfo(param.getAttributeId(), param.getMaturityLevelId());

        List<Result.Question> questions = attrLevelQuestionsInfo.questions().stream()
            .map(e -> {
                var answerOptions = e.answerOption().stream()
                    .map(x -> new Result.Question.AnswerOption(x.index(), x.title(), x.value()))
                    .toList();
                return new Result.Question(e.index(),
                    e.title(),
                    e.mayNotBeApplicable(),
                    e.weight(),
                    e.questionnaire(),
                    answerOptions);
            }).toList();

        return new Result(attrLevelQuestionsInfo.id(),
            attrLevelQuestionsInfo.title(),
            attrLevelQuestionsInfo.index(),
            attrLevelQuestionsInfo.questionsCount(),
            questions);
    }
}
