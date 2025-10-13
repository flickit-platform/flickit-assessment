package org.flickit.assessment.users.application.port.out.usersurvey;

public interface CreateRedirectUrlLinkPort {

    String createRedirectUrlLink(String baseUrl, long surveyId);
}
