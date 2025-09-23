package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslFilePathPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslJsonPathPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.UpdateKitDslPort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.KitDslMapper.toJpaEntity;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class KitDslPersistenceJpaAdapter implements
    CreateKitDslPort,
    LoadDslJsonPathPort,
    UpdateKitDslPort,
    LoadDslFilePathPort {

    private final KitDslJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public Long create(String dslFilePath, String jsonFilePath, UUID createdBy) {
        var entity = toJpaEntity(dslFilePath, jsonFilePath, createdBy);
        entity.setId(sequenceGenerators.generateKitDslId());
        return repository.save(entity).getId();
    }

    @Override
    public AssessmentKitDslModel convert(MultipartFile excelFile) {
        ExcelToDslModelConverter converter = new ExcelToDslModelConverter();
        try (InputStream is = excelFile.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            var qualityAttributes = workbook.getSheet("QualityAttributes");

            var subjects = converter.convertSubjects(qualityAttributes);
            System.out.println(subjects);
            var attributes = converter.convertAttributes(qualityAttributes);
            System.out.println(attributes);
            var Questionnaires = converter.convertQuestionnaires(workbook.getSheet("Questionnaires"));
            System.out.println(Questionnaires);
            var questions = converter.convertQuestions(workbook.getSheet("Questions"));
            System.out.println(questions);
            var sheet2 = workbook.getSheet("sheet2");
            var sheet3 = workbook.getSheet("sheet3");
            var sheet4 = workbook.getSheet("sheet4");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public String loadJsonPath(Long kitDslId) {
        KitDslJpaEntity kitDslEntity = repository.findById(kitDslId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND));
        return kitDslEntity.getJsonPath();
    }

    @Override
    public void update(Long id, Long kitId, UUID lastModifiedBy, LocalDateTime lastModificationTime) {
        repository.removeKitId(kitId, lastModifiedBy, lastModificationTime);

        repository.updateById(id, kitId, lastModifiedBy, lastModificationTime);
    }

    @Override
    public Optional<String> loadDslFilePath(Long kitId) {
        return repository.findDslPathByKitId(kitId);
    }
}
