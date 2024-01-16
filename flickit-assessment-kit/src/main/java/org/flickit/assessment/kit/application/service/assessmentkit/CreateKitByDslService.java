package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadJsonKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.UpdateKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateAssessmentKitTagKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.minio.LoadKitDSLJsonFilePort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CompositeCreateKitPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateKitByDslService implements CreateKitByDslUseCase {

    public static final char SLASH = '/';
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadJsonKitDslPort loadJsonKitDslPort;
    private final LoadKitDSLJsonFilePort loadKitDSLJsonFilePort;
    private final CreateAssessmentKitPort createAssessmentKitPort;
    private final CompositeCreateKitPersister persister;
    private final CreateAssessmentKitTagKitPort createAssessmentKitTagKitPort;
    private final UpdateKitDslPort updateKitDslPort;

    @SneakyThrows
    @Override
    public Long create(CreateKitByDslUseCase.Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());

        var assessmentKitDsl = loadJsonKitDslPort.load(param.getKitDslId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND));
        String dslJsonPath = assessmentKitDsl.getJsonPath();
        String dslFilePath = dslJsonPath.substring(0, dslJsonPath.lastIndexOf(SLASH));
        String versionId = dslJsonPath.substring(dslJsonPath.lastIndexOf(SLASH) + 1);

        String dslContent = loadKitDSLJsonFilePort.load(dslFilePath, versionId);
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        // TODO: validate dsl kit

        String code = param.getTitle().toLowerCase().replace(' ', '-');
        var createKitParam = new CreateAssessmentKitPort.Param(
            code,
            param.getTitle(),
            param.getSummary(),
            param.getAbout(),
            Boolean.FALSE,
            param.getIsPrivate(),
            param.getExpertGroupId(),
            param.getCurrentUserId()
        );
        Long kitId = createAssessmentKitPort.persist(createKitParam);

        persister.persist(dslKit, kitId, param.getCurrentUserId());

        createAssessmentKitTagKitPort.persist(param.getTagIds(), kitId);

        updateKitDslPort.update(new UpdateKitDslPort.Param(assessmentKitDsl.getId(), kitId));

        return kitId;
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

}
