package org.flickit.assessment.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageKey {

    public static final String ASSESSMENT_AI_IS_DISABLED = "assessment.ai.disabled";

    public static final String ADVANCED_ASSESSMENT_DEFAULT_INSIGHT_COMPLETED = "advanced-assessment-default-insight.completed";
    public static final String QUICK_ASSESSMENT_DEFAULT_INSIGHT_COMPLETED = "quick-assessment-default-insight.completed";
    public static final String ADVANCED_ASSESSMENT_DEFAULT_INSIGHT_INCOMPLETE = "advanced-assessment-default-insight.incomplete";
    public static final String QUICK_ASSESSMENT_DEFAULT_INSIGHT_INCOMPLETE = "quick-assessment-default-insight.incomplete";

    public static final String ADVANCED_ASSESSMENT_SUBJECT_DEFAULT_INSIGHT = "advanced-assessment-subject-default-insight";
    public static final String QUICK_ASSESSMENT_SUBJECT_DEFAULT_INSIGHT = "quick-assessment-subject-default-insight";

    public static final String NOTIFICATION_TITLE_GRANT_ASSESSMENT_USER_ROLE = "notification-title.grant-assessment-user-role";
    public static final String NOTIFICATION_TITLE_GRANT_ACCESS_TO_REPORT = "notification-title.grant-access-to-report";
    public static final String NOTIFICATION_TITLE_CREATE_ASSESSMENT = "notification-title.create-assessment";
    public static final String NOTIFICATION_TITLE_COMPLETE_ASSESSMENT = "notification-title.completed-assessment";
    public static final String NOTIFICATION_TITLE_ACCEPT_ASSESSMENT_INVITATION = "notification-title.accept-assessment-invitation";

    public static final String GRANT_ACCESS_TO_REPORT_INVITE_TO_REGISTER_EMAIL_SUBJECT = "grant-access-to-report.invite-to-register.email.subject";
    public static final String GRANT_ACCESS_TO_REPORT_INVITE_TO_REGISTER_EMAIL_BODY = "grant-access-to-report.invite-to-register.email.body";
    public static final String GRANT_ACCESS_TO_REPORT_INVITE_TO_REGISTER_EMAIL_BODY_WITHOUT_SUPPORT_EMAIL = "grant-access-to-report.invite-to-register.email.body.without.support.email";
}
