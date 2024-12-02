package org.flickit.assessment.kit.application.domain.excel;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AnswerOptionExcelModel {

    Integer index;
    String title;
    Double value;
}
