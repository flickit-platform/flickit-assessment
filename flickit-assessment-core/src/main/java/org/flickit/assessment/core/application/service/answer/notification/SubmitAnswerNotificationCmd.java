package org.flickit.assessment.core.application.service.answer.notification;

import org.flickit.assessment.common.application.domain.notification.NotificationCmd;

import java.util.UUID;

/**
 * A command that may trigger a notification to the user identified by {@link #assessorId}.
 *
 * @param assessmentId  the ID of the assessment associated with the answer submission
 * @param assessorId    the ID of the user who submitted the answer
 * @param hasProgressed {@code true} if submitting the answer caused progress in the assessment
 */
public record SubmitAnswerNotificationCmd(UUID assessmentId,
                                          UUID assessorId,
                                          boolean hasProgressed) implements NotificationCmd {
}
