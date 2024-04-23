package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.space.CreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.application.domain.Space.generateSlugCode;

@Service
@RequiredArgsConstructor
public class CreateSpaceService implements CreateSpaceUseCase {

    private final CreateSpacePort createSpacePort;

    @Override
    public void create(String title, UUID currentUserId) {
        createSpacePort.persist(toParam(title, LocalDateTime.now(), currentUserId));
    }

    CreateSpacePort.Param toParam(String title, LocalDateTime creationTime, UUID currentUserId){
        return new CreateSpacePort.Param(
            generateSlugCode(title),
            title,
            currentUserId,
            creationTime,
            creationTime,
            currentUserId,
            currentUserId
        );
    }
}
