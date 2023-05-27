package org.flickit.flickitassessmentcore.application.port.in.AssessmentProject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.time.LocalDateTime;

@Getter
public class CreateAssessmentProjectCommand extends SelfValidating<CreateAssessmentProjectCommand> {

    private final String code;
    @NotBlank
    private final String title;
    @NotBlank
    private final String description;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationDate;
    @NotNull
    private final Long assessmentKitId;
    @Setter
    @NotNull
    private AssessmentColorDto color;
    @NotNull
    private final Long spaceId;

    public CreateAssessmentProjectCommand(String code,
                                          String title,
                                          String description,
                                          LocalDateTime creationTime,
                                          LocalDateTime lastModificationDate,
                                          Long assessmentKitId,
                                          AssessmentColorDto color,
                                          Long spaceId) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.creationTime = creationTime;
        this.lastModificationDate = lastModificationDate;
        this.assessmentKitId = assessmentKitId;
        this.color = color;
        this.spaceId = spaceId;
        this.validateSelf();
    }

    public String generateSlugCodeByTitle() {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\s+", "-");
    }
}
