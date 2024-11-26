package org.flickit.assessment.kit.application.domain.excel;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubjectExcelModel extends BaseExcelModel {

    Integer weight;
}
