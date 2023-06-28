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
public class Evidence {
    private UUID id;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private Long createdById;
    private UUID assessmentId;
    private Long questionId;

    @Override
    public String toString() {
        return id.toString();
    }
}
