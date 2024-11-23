package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.customkit.KitCustomJpaEntity;
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
}
