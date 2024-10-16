package org.flickit.assessment.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageKey {

    public static final String ASSESSMENT_AI_IS_DISABLED = "assessment.ai.disabled";

    public static final String ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED = "assessment-default-insight.completed";
    public static final String ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE = "assessment-default-insight.incomplete";

    public static final String SUBJECT_DEFAULT_INSIGHT = "subject-default-insight";

    public static final String NOTIFICATION_TITLE_GRANT_ASSESSMENT_USER_ROLE = "notification-title.grant-assessment-user-role";
    public static final String NOTIFICATION_TITLE_CREATE_ASSESSMENT = "notification-title.create-assessment";
    public static final String NOTIFICATION_TITLE_COMPLETE_ASSESSMENT = "notification-title.completed-assessment";
    public static final String NOTIFICATION_TITLE_ACCEPT_ASSESSMENT_INVITATION = "notification-title.accept-assessment-invitation";

}
