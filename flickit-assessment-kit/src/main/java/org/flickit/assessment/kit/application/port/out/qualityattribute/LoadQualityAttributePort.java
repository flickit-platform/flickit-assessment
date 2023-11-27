package org.flickit.assessment.kit.application.port.out.qualityattribute;

import org.flickit.assessment.kit.application.domain.Attribute;

import java.util.Optional;

public interface LoadQualityAttributePort {

    Optional<Attribute> load(Long id);
}
