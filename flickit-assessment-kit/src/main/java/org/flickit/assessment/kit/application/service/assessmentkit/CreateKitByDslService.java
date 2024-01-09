package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadJsonKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.UpdateAssessmentKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkittag.CreateAssessmentKitTagKitPort;
import org.flickit.assessment.kit.application.port.out.minio.LoadJsonFilePort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CompositeCreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.flickit.assessment.kit.common.ErrorMessageKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;

import static org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateKitByDslService implements CreateKitByDslUseCase {

    public static final char SLASH = '/';
    private final LoadJsonKitDslPort loadJsonKitDslPort;
    private final LoadJsonFilePort loadJsonFilePort;
    private final CompositeCreateKitPersister persister; // TODO: implement
    private final CreateAssessmentKitPort createAssessmentKitPort;
    private final CreateAssessmentKitTagKitPort createAssessmentKitTagKitPort; // TODO: implement
    private final UpdateAssessmentKitDslPort updateAssessmentKitDslPort;

    @SneakyThrows
    @Override
    public Long create(CreateKitByDslUseCase.Param param) {
        var assessmentKitDsl = loadJsonKitDslPort.load(param.getKitJsonDslId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND));
        String dslFile = assessmentKitDsl.getDslFile();
        String dslFilePath = dslFile.substring(0, dslFile.lastIndexOf(SLASH));
        String versionId = dslFile.substring(dslFile.lastIndexOf(SLASH));

        String dslContent = loadJsonFilePort.load(dslFilePath, versionId);
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(dslContent);
        // TODO: validate dsl kit
        CreateKitPersisterContext context = persister.persist(dslKit);

        String code = param.getTitle().toLowerCase().replace(' ', '-');
        var createKitParam = new CreateAssessmentKitPort.Param(
            code,
            param.getTitle(),
            param.getSummary(),
            param.getAbout(),
            Boolean.FALSE,
            param.isPrivate(),
            param.getExpertGroupId(),
            context.get(KEY_SUBJECTS),
            context.get(KEY_MATURITY_LEVELS),
            context.get(KEY_QUESTIONNAIRES),
            param.getCurrentUserId()
        );
        Long kitId = createAssessmentKitPort.persist(createKitParam); // TODO: where is the inner entities?

        param.getTagIds().forEach(tagId ->
            createAssessmentKitTagKitPort.persist(new CreateAssessmentKitTagKitPort.Param(tagId, kitId)));

        updateAssessmentKitDslPort.update(new UpdateAssessmentKitDslPort.Param(assessmentKitDsl.getId(), kitId));

        return kitId;
    }

}
