package org.flickit.assessment.kit.application.service.assessmentkit.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslJsonPathPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UpdateKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateKitTagRelationPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMemberIdsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.flickit.assessment.kit.application.port.out.minio.LoadKitDSLJsonFilePort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.application.domain.AssessmentKit.generateSlugCode;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateKitByDslService implements CreateKitByDslUseCase {

    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadDslJsonPathPort loadDslJsonPathPort;
    private final LoadKitDSLJsonFilePort loadKitDSLJsonFilePort;
    private final CreateAssessmentKitPort createAssessmentKitPort;
    private final CompositeCreateKitPersister persister;
    private final CreateKitTagRelationPort createKitTagRelationPort;
    private final UpdateKitDslPort updateKitDslPort;
    private final LoadExpertGroupMemberIdsPort loadExpertGroupMemberIdsPort;
    private final GrantUserAccessToKitPort grantUserAccessToKitPort;

    @Override
    public Long create(CreateKitByDslUseCase.Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());

        String dslJsonPath = loadDslJsonPathPort.loadJsonPath(param.getKitDslId());

        String dslContent = loadKitDSLJsonFilePort.loadDslJson(dslJsonPath);
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);

        String code = generateSlugCode(param.getTitle());
        var createKitParam = new CreateAssessmentKitPort.Param(
            code,
            param.getTitle(),
            param.getSummary(),
            param.getAbout(),
            Boolean.FALSE,
            param.getIsPrivate(),
            param.getExpertGroupId(),
            KitVersionStatus.ACTIVE,
            param.getCurrentUserId()
        );
        var kitCreationResult = createAssessmentKitPort.persist(createKitParam);

        persister.persist(dslKit, kitCreationResult.kitVersionId(), param.getCurrentUserId());

        createKitTagRelationPort.persist(param.getTagIds(), kitCreationResult.kitId());

        updateKitDslPort.update(param.getKitDslId(), kitCreationResult.kitId(), param.getCurrentUserId(), LocalDateTime.now());

        List<UUID> expertGroupMemberIds = loadExpertGroupMemberIdsPort.loadMemberIds(param.getExpertGroupId())
            .stream()
            .map(LoadExpertGroupMemberIdsPort.Result::userId)
            .toList();
        grantUserAccessToKitPort.grantUsersAccess(kitCreationResult.kitId(), expertGroupMemberIds);

        return kitCreationResult.kitId();
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

}
