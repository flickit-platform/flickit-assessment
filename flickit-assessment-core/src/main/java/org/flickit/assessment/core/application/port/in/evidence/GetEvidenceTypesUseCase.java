package org.flickit.assessment.core.application.port.in.evidence;

import java.util.List;

public interface GetEvidenceTypesUseCase {

    Result getEvidenceTypes();

    record EvidenceTypeItem(int id, String title) {}

    record Result(List<EvidenceTypeItem> evidenceTypes) {}
}
