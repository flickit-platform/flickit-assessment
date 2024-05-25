package org.flickit.assessment.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageKey {

    public static final String VIEWER_DESCRIPTION="The user can only view the assessment results. " +
        "The User is not able to perform the assessment process (answering questions, editing responses, adding comments, etc.).";
    public static final String COMMENTER_DESCRIPTION= "The user, in addition to viewing the assessment results and the answers to questions, " +
        "is able to register comments on the questions and witness them as well.";
    public static final String ASSESSOR_DESCRIPTION="The user, in addition to the viewer's permissions," +
        " possesses all the necessary permissions to carry out the assessment process.";
    public static final String MANAGER_DESCRIPTION="The user not only has access for viewing and assessing" +
        " but also for managing the access of others to the assessments.";
}
