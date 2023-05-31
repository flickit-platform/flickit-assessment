package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.time.LocalDateTime;

@Getter
public class CreateAssessmentCommand extends SelfValidating<CreateAssessmentCommand> {

    private String code;
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

    public CreateAssessmentCommand(String title,
                                   String description,
                                   Long spaceId,
                                   Long assessmentKitId,
                                   Long colorId) {

        this.title = title;
        this.description = description;
        this.spaceId = spaceId;
        this.assessmentKitId = assessmentKitId;
        this.color = new AssessmentColorDto(colorId);
        this.creationTime = LocalDateTime.now();
        this.lastModificationDate = LocalDateTime.now();
        this.validateSelf();
    }

    public CreateAssessmentCommand(String code,
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
