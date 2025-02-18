package org.flickit.assessment.scenario.test.kit.tag;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.scenario.helper.persistence.JpaTestTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KitTagTestHelper {

    private final JpaTestTemplate jpaTemplate;

    private long index = 1;

    public Long createKitTag() {
        index++;
        KitTagJpaEntity kitTagEntity = new KitTagJpaEntity(index, "tag-" + index, "tag " + index);
        jpaTemplate.persist(kitTagEntity);
        return kitTagEntity.getId();
    }
}
