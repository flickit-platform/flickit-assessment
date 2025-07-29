package org.flickit.assessment.users.adapter.out.persistence.usersurvey;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.users.usersurvey.UserSurveyJpaRepository;
import org.flickit.assessment.users.application.domain.UserSurvey;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.UpdateUserSurveyPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserSurveyPersistenceJpaAdapter implements
    LoadUserSurveyPort,
    CreateUserSurveyPort,
    UpdateUserSurveyPort {

    private final UserSurveyJpaRepository repository;
    private final AssessmentJpaRepository assessmentRepository;

    @Override
    public Optional<UserSurvey> loadByUserId(UUID userId) {
        return repository.findByUserId(userId)
            .map(UserSurveyMapper::mapToDomain);
    }

    @Override
    public long persist(CreateUserSurveyPort.Param param) {
        if (!assessmentRepository.existsByIdAndDeletedFalse(param.assessmentId()))
            throw new ResourceNotFoundException(COMMON_ASSESSMENT_ID_NOT_FOUND);

        var savedEntity = repository.save(UserSurveyMapper.mapCreateParamToJpaEntity(param));
        return savedEntity.getId();
    }

    @Override
    public void updateDontShowAgain(UpdateUserSurveyPort.Param param) {
        repository.updateDontShowAgainByUserId(param.userId(), param.dontShowAgain(), param.lastModificationTime());
    }
}
