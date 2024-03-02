package org.flickit.assessment.core.application.port.in.evidence;

import java.util.List;

public interface GetEvidenceTypesUseCase {

    Result getEvidenceTypes();

    record Result(List<EvidenceTypeItem> types) {}

    record EvidenceTypeItem(String code, String title) {}
}
