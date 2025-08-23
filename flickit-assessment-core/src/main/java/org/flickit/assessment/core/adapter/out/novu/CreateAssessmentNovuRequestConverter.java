package org.flickit.assessment.core.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.core.application.service.assessment.notification.CreateAssessmentNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.CREATE_ASSESSMENT;

@Component
public class CreateAssessmentNovuRequestConverter extends AbstractNovuRequestConverter {

    @Override
    protected String getEventName() {
        return CREATE_ASSESSMENT.getCode();
    }

    @Override
    public Class<CreateAssessmentNotificationPayload> payloadClass() {
        return CreateAssessmentNotificationPayload.class;
    }
}
