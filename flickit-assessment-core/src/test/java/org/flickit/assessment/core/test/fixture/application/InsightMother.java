package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.insight.Insight;

import java.time.LocalDateTime;

public class InsightMother {

    public static Insight emptyInsight() {
        return new Insight(null, null, true, null);
    }

    public static Insight defaultInsight() {
        return new Insight(insightDetail(LocalDateTime.now()), null, true, true);
    }

    public static Insight defaultInsightWithMinLastModificationTime() {
        return new Insight(insightDetail(LocalDateTime.MIN), null, true, true);
    }

    public static Insight unapprovedAssessorInsightWithMinLastModificationTime() {
        return new Insight(null, insightDetail(LocalDateTime.MIN), true, false);
    }

    private static Insight.InsightDetail insightDetail(LocalDateTime creationTime) {
        return new Insight.InsightDetail("insight", creationTime, true, creationTime);
    }
}
