package org.flickit.assessment.core.application.port.out.attribute;

import java.util.List;

public interface LoadAttributesPort {

    List<Result> loadAttributes(long kitVersionId);

    record Result(long id,
                  String title,
                  String description,
                  int index,
                  int weight,
                  double confidenceValue,
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
