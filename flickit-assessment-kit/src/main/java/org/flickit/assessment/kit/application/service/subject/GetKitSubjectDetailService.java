package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.subject.GetKitSubjectDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.CheckSubjectExistencePort;
import org.flickit.assessment.kit.application.port.out.subject.CountSubjectQuestionsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectDetailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitSubjectDetailService implements GetKitSubjectDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final CheckSubjectExistencePort checkSubjectExistencePort;
    private final LoadSubjectDetailPort loadSubjectDetailPort;
    private final CountSubjectQuestionsPort countSubjectQuestionsPort;

    @Override
    public Result getKitSubjectDetail(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!checkSubjectExistencePort.exist(param.getKitId(), param.getSubjectId()))
            throw new ResourceNotFoundException(GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND);

        var subject = loadSubjectDetailPort.loadById(param.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND));
        var attributes = subject.getAttributes().stream().map(this::toAttribute).toList();
        var questionsCount = countSubjectQuestionsPort.countBySubjectId(param.getSubjectId());
        return new Result(questionsCount, subject.getDescription(), attributes);
    }

    private Attribute toAttribute(org.flickit.assessment.kit.application.domain.Attribute attribute) {
        return new Attribute(attribute.getId(), attribute.getIndex(), attribute.getTitle());
    }
}
