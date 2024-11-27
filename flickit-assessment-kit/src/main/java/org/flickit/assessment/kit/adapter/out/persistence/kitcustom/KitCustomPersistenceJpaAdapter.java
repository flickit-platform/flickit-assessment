package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.KitCustom;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.LoadKitCustomPort;
import org.flickit.assessment.kit.application.port.out.kitcustom.UpdateKitCustomPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class KitCustomPersistenceJpaAdapter implements
    CreateKitCustomPort,
    LoadKitCustomPort,
    UpdateKitCustomPort {

    private final KitCustomJpaRepository repository;
    private final AssessmentKitJpaRepository kitRepository;
    private final KitDbSequenceGenerators sequenceGenerators;
    private final SubjectJpaRepository subjectRepository;
    private final AttributeJpaRepository attributeRepository;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public long persist(CreateKitCustomPort.Param param) {
        String kitCustomJson = objectMapper.writeValueAsString(param.customData());

        validateCustomData(param.customData(), param.kitId());

        KitCustomJpaEntity entity = KitCustomMapper.mapToJpaEntity(param, kitCustomJson);
        entity.setId(sequenceGenerators.generateKitCustomId());
        return repository.save(entity).getId();
    }

    @Override
    @SneakyThrows
    public LoadKitCustomPort.Result loadByIdAndKitId(long kitCustomId, long kitId) {
        var kitCustomEntity = repository.findByIdAndKitId(kitCustomId, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        KitCustomData customData = objectMapper.readValue(kitCustomEntity.getCustomData(), KitCustomData.class);
        return new LoadKitCustomPort.Result(kitCustomId, kitCustomEntity.getTitle(), kitCustomEntity.getKitId(), customData);
    }

    @Override
    @SneakyThrows
    public KitCustom load(long kitCustomId) {
        var entity = repository.findById(kitCustomId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        KitCustomData customData = objectMapper.readValue(entity.getCustomData(), KitCustomData.class);
        return KitCustomMapper.mapToDomain(entity, customData);
    }

    @Override
    @SneakyThrows
    public void update(UpdateKitCustomPort.Param param) {
        if (!repository.existsByIdAndKitId(param.id(), param.kitId()))
            throw new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND);

        validateCustomData(param.customData(), param.kitId());

        String kitCustomJson = objectMapper.writeValueAsString(param.customData());
        repository.update(param.id(),
            param.title(),
            param.code(),
            kitCustomJson,
            param.lastModificationTime(),
            param.lastModifiedBy()
        );
    }

    private void validateCustomData(KitCustomData customData, Long kitId) {
        var kitVersionId = kitRepository.loadKitVersionId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        Set<Long> customSubjectIds;
        if (customData.subjects() != null) {
            customSubjectIds = customData.subjects().stream()
                .map(KitCustomData.Subject::id)
                .collect(toSet());

            List<SubjectJpaEntity> se = subjectRepository.findAllByIdInAndKitVersionId(customSubjectIds, kitVersionId);
            if (se.size() != customSubjectIds.size())
                throw new ResourceNotFoundException(SUBJECT_ID_NOT_FOUND);
        }

        Set<Long> customAttributeIds;
        if (customData.attributes() != null) {
            customAttributeIds = customData.attributes().stream()
                .map(KitCustomData.Attribute::id)
                .collect(toSet());

            List<AttributeJpaEntity> ae = attributeRepository.findAllByIdInAndKitVersionId(customAttributeIds, kitVersionId);
            if (ae.size() != customAttributeIds.size())
                throw new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND);
        }
    }
}
