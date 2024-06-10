package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.ExpertGroupAccess;

import java.time.LocalDateTime;
import java.util.UUID;

public class ExpertGroupAccessMother {

    public static ExpertGroupAccess createExpertGroupAccess (int days,int status){
        return new ExpertGroupAccess(LocalDateTime.now().plusDays(days),
            UUID.randomUUID(),
            status);
    }
}
