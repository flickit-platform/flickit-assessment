package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaEntity;
import org.flickit.assessment.kit.application.domain.KitCustom;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitCustomMapper {

    public static KitCustomJpaEntity mapToJpaEntity(CreateKitCustomPort.Param param, String kitCustomJson) {
        return new KitCustomJpaEntity(null,
            param.kitId(),
            param.title(),
            param.code(),
            kitCustomJson,
            param.creationTime(),
            param.creationTime(),
            param.createdBy(),
            param.createdBy());
    }

    public static KitCustom mapToDomain(KitCustomJpaEntity entity, KitCustomData customData) {
        return new KitCustom(
            entity.getId(),
            entity.getKitId(),
            entity.getTitle(),
            customData,
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy()
        );
    }
}
