package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.CountSubjectQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitSubjectDetailService implements GetKitSubjectDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;
    private final LoadSubjectPort loadSubjectPort;
    private final CountSubjectQuestionsPort countSubjectQuestionsPort;

    @Override
    public Result getKitSubjectDetail(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        long kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());

        var subject = loadSubjectPort.load(param.getSubjectId(), kitVersionId);
        var attributes = subject.getAttributes().stream().map(this::toAttribute).toList();
        var questionsCount = countSubjectQuestionsPort.countBySubjectId(param.getSubjectId(), kitVersionId);
        return new Result(questionsCount, subject.getDescription(), attributes);
    }

    private Attribute toAttribute(org.flickit.assessment.kit.application.domain.Attribute attribute) {
        return new Attribute(attribute.getId(), attribute.getIndex(), attribute.getTitle());
    }
}
