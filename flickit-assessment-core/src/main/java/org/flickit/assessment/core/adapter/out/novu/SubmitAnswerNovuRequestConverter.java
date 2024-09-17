package org.flickit.assessment.core.adapter.out.novu;

import org.flickit.assessment.common.adapter.out.novu.AbstractNovuRequestConverter;
import org.flickit.assessment.common.adapter.out.novu.TenantProperties;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationPayload;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.common.adapter.out.novu.NotificationType.COMPLETE_ASSESSMENT;

@Component
public class SubmitAnswerNovuRequestConverter extends AbstractNovuRequestConverter {

    public SubmitAnswerNovuRequestConverter(TenantProperties tenantProperties) {
        super(tenantProperties);
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
