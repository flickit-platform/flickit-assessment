package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Assessment {
    private UUID id;
    private String code;
    private String title;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private Long assessmentKitId;
    private Integer colorId;
    private Long spaceId;
    private Long deletionTime;

    @Override
    public String toString() {
        return title;
    }
}
