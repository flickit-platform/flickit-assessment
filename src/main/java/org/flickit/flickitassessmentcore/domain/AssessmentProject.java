package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssessmentProject {
    private UUID id;
    private String code;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private Long assessmentKitId;
    private AssessmentColor colorId;
    private Long spaceId;
    private Long maturityLevelId;
    private List<AssessmentResult> assessmentResults;
    private List<Evidence> evidences;

    @Override
    public String toString() {
        return title;
    }
}
