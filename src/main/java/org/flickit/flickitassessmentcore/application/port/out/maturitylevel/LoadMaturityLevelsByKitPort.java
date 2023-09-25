package org.flickit.flickitassessmentcore.application.port.out.maturitylevel;

import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;

import java.util.List;

public interface LoadMaturityLevelsByKitPort {

    List<MaturityLevel> loadByKitId(Long assessmentKitId);
}
