package org.flickit.assessment.kit.application.domain.excel;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BaseExcelModel {

    private String code;
    private Integer index;
    private String title;
    private String description;
}
