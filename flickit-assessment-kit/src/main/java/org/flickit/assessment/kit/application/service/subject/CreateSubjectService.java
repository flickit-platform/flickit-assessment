package org.flickit.assessment.kit.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.subject.CreateSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class CreateSubjectService implements CreateSubjectUseCase {

    private final CreateSubjectPort createSubjectPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public long createSubject(Param param) {
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String code = Subject.generateSlugCode(param.getTitle());

        return createSubjectPort.persist(new CreateSubjectPort.Param(code,
            param.getTitle(),
            param.getIndex(),
            param.getWeight(),
            param.getDescription(),
            param.getKitVersionId(),
            param.getCurrentUserId()));
    }
}
