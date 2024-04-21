package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeDetailUseCase;

import java.util.List;

public interface LoadAttributeMaturityLevelPort {

    List<GetKitAttributeDetailUseCase.MaturityLevel> loadByAttributeId(Long attributeId);
}
