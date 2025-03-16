package org.flickit.assessment.core.application.domain.insight;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class Insight {
    private final InsightDetail defaultInsight;
    private final InsightDetail assessorInsight;
    private final boolean editable;
    private final Boolean approved;

    @Getter
    @RequiredArgsConstructor
    public static class InsightDetail {

        private final String insight;
        private final LocalDateTime creationTime;
        private final boolean isValid;
        private final LocalDateTime lastModificationTime;
    }

    public static Predicate<Insight> isExpired(LocalDateTime lastCalculationTime) {
        return insight -> {
            if (insight.getAssessorInsight() != null)
                return insight.getAssessorInsight().getLastModificationTime().isBefore(lastCalculationTime);
            if (insight.getDefaultInsight() != null)
                return insight.getDefaultInsight().getLastModificationTime().isBefore(lastCalculationTime);
            return false;
        };
    }

    public static Predicate<Insight> isNotGenerated() {
        return insight -> insight.getDefaultInsight() == null && insight.getAssessorInsight() == null;
    }

    public static Predicate<Insight> isUnapproved() {
        return insight -> {
            if (insight.getApproved() != null || insight.getDefaultInsight() != null)
                return !Boolean.TRUE.equals(insight.getApproved());
            return false;
        };
    }
}
