package org.flickit.assessment.kit.application.service.subject;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.subject.CreateSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.GenerateHashCodeUtil.generateCode;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateSubjectService implements CreateSubjectUseCase {

    private final CreateSubjectPort createSubjectPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadKitVersionPort loadKitVersionPort;

    @Override
    public long createSubject(Param param) {
        KitVersion kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId());
        if (!ownerId.equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String code = generateCode(param.getTitle());

        return createSubjectPort.persist(new CreateSubjectPort.Param(code,
            param.getTitle(),
            param.getIndex(),
            param.getWeight(),
            param.getDescription(),
            param.getKitVersionId(),
            param.getCurrentUserId()));
    }
}
