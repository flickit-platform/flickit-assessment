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
public class AssessmentKit {

    public AssessmentKit(Long id) {
        this.id = id;
    }

    private Long id;
    private String code;
    private String title;
    private String summary;
    private String about;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private Long expertGroupId;
    private boolean isActive;

    @Override
    public String toString() {
        return title;
    }
}
