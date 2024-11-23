package org.flickit.assessment.kit.adapter.out.persistence.kitcustom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaEntity;
import org.flickit.assessment.kit.application.port.out.kitcustom.CreateKitCustomPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitCustomMapper {

    public static KitCustomJpaEntity mapToJpaEntity(CreateKitCustomPort.Param param) {
        return new KitCustomJpaEntity(null,
            param.kitId(),
            param.title(),
            param.code(),
            param.customData(),
            param.creationTime(),
            param.creationTime(),
            param.createdBy(),
            param.createdBy());
    }
}
