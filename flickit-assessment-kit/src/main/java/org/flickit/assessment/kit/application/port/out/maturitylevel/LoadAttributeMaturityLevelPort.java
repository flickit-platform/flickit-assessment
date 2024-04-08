package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase;

import java.util.List;

public interface LoadAttributeMaturityLevelPort {

    List<GetAttributeDetailUseCase.MaturityLevel> loadByAttributeId(Long attributeId);
}
