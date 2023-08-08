package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AssessmentKit {

    private final long id;
    private final List<MaturityLevel> maturityLevels;
}
