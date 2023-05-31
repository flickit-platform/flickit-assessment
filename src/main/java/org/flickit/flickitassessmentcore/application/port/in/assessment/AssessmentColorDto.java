package org.flickit.flickitassessmentcore.application.port.in.assessment;

import lombok.Getter;

@Getter
public class AssessmentColorDto {

    private final Long id;
    private String title;
    private String colorCode;

    public AssessmentColorDto(Long id) {
        this.id = id;
    }

    public AssessmentColorDto(Long id, String title, String colorCode) {
        this.id = id;
        this.title = title;
        this.colorCode = colorCode;
    }
}
