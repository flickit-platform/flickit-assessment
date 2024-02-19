package org.flickit.assessment.core.application.port.in.evidence;

import java.util.List;

public interface GetEvidenceTypesUseCase {

    Result getEvidenceTypes();

    record EvidenceTypeItem(String title, String code) {}

    record Result(List<EvidenceTypeItem> types) {}
}
