package org.flickit.assessment.users.adapter.out.formsaz;

import org.flickit.assessment.users.application.port.out.usersurvey.CreateRedirectUrlLinkPort;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FormsazAdapter implements CreateRedirectUrlLinkPort {

    private final static String UNIQUE_ID = "uniqueId";

    @Override
    public String createRedirectUrlLink(String baseUrl, long surveyId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam(UNIQUE_ID, surveyId)
            .toUriString();
    }
}
