package org.flickit.assessment.core.application.service.answer.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.notification.NotificationCreator;
import org.flickit.assessment.common.application.domain.notification.NotificationEnvelope;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.notification.SubmitAnswerNotificationCmd;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationPayload.AssessmentModel;
import org.flickit.assessment.core.application.service.answer.notification.SubmitAnswerNotificationPayload.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_NOTIFICATION_TITLE;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubmitAnswerNotificationCreator implements
    NotificationCreator<SubmitAnswerNotificationCmd> {

    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;

    @Override
    public List<NotificationEnvelope> create(SubmitAnswerNotificationCmd cmd) {
        if (!cmd.hasProgressed())
            return List.of();

        var assessment = getAssessmentPort.getAssessmentById(cmd.assessmentId());
        var user = loadUserPort.loadById(cmd.assessorId());
        if (assessment.isEmpty() || user.isEmpty()) {
            log.warn("assessment or user not found");
            return List.of();
        }

        var progress = getAssessmentProgressPort.getProgress(cmd.assessmentId());

        if (isFinished(progress) && !isFinishedByCreator(cmd, assessment.get())) {
            return List.of(new NotificationEnvelope(
                assessment.get().getCreatedBy(),
                new SubmitAnswerNotificationPayload(new AssessmentModel(assessment.get()), new UserModel(user.get())),
                MessageBundle.message(GET_ASSESSMENT_PROGRESS_NOTIFICATION_TITLE)
            ));
        }
        return List.of();
    }

    private boolean isFinished(GetAssessmentProgressPort.Result progress) {
        return progress.answersCount() == progress.questionsCount();
    }

    private boolean isFinishedByCreator(SubmitAnswerNotificationCmd cmd, Assessment assessment) {
        return cmd.assessorId().equals(assessment.getCreatedBy());
    }

    @Override
    public Class<SubmitAnswerNotificationCmd> cmdClass() {
        return SubmitAnswerNotificationCmd.class;
    }
}
