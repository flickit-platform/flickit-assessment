package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.Set;

public interface LoadMLByKitPort {

    Set<MaturityLevel> loadMLByKitId(Long kitId);
}
