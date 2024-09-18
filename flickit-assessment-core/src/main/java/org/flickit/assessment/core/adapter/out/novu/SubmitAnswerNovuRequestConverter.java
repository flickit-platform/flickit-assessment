package org.flickit.assessment.core.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.common.application.domain.notification.Tenant;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.COMPLETE_ASSESSMENT;

@Component
public class SubmitAnswerNovuRequestConverter extends AbstractNovuRequestConverter {

    public SubmitAnswerNovuRequestConverter(Tenant tenant) {
        super(tenant);
    }

    @Override
    protected String getEventName() {
        return COMPLETE_ASSESSMENT.getCode();
    }

    @Override
    public Class<SubmitAnswerNotificationPayload> payloadClass() {
        return SubmitAnswerNotificationPayload.class;
    }
}
