package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectMapper {

    public static Subject mapToDomainModel(SubjectJpaEntity entity, List<Attribute> attributes) {
        return new Subject(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getWeight(),
            entity.getDescription(),
            attributes,
            entity.getCreatedBy(),
            entity.getLastModifiedBy(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }

    public static SubjectJpaEntity mapToJpaEntity(CreateSubjectPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new SubjectJpaEntity(
            null,
            param.kitVersionId(),
            param.code(),
            param.index(),
            param.title(),
            param.description(),
            param.weight(),
            null, // TODO: Consider replacing this with the actual value after editing the service.
            creationTime,
            creationTime,
            param.createdBy(),
            param.createdBy());
    }

    public static SubjectDslModel mapToDslModel(SubjectJpaEntity e) {
        return SubjectDslModel.builder()
            .code(e.getCode())
            .index(e.getIndex())
            .title(e.getTitle())
            .description(e.getDescription())
            .weight(e.getWeight())
            .build();
    }
}

