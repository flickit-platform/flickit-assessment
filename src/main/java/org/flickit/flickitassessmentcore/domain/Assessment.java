package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Assessment {
    private UUID id;
    private String code;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private AssessmentKit assessmentKit;
    private AssessmentColor color;
    private Long spaceId;
    private MaturityLevel maturityLevel;

    @Override
    public String toString() {
        return title;
    }
}
