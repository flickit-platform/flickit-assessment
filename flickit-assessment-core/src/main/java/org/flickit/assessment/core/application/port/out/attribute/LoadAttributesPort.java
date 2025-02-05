package org.flickit.assessment.core.application.port.out.attribute;

import java.util.List;
import java.util.UUID;

public interface LoadAttributesPort {

    List<Result> loadAttributes(UUID assessmentId);

    record Result(long id,
                  String title,
                  String description,
                  int index,
                  int weight,
                  Double confidenceValue,
                  MaturityLevel maturityLevel,
                  Subject subject) {
    }

    record MaturityLevel(long id,
                         String title,
                         String description,
                         int index,
                         int value) {
    }

    record Subject(long id,
                   String title) {
    }
}
