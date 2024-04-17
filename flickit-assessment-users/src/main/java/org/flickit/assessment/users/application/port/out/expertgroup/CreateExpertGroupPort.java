package org.flickit.assessment.users.application.port.out.expertgroup;

import java.util.UUID;

public interface CreateExpertGroupPort {

    Long persist(Param param);

    record Param(String code,
                 String title,
                 String bio,
                 String about,
                 String picture,
                 String website,
                 UUID currentUserId) {
    }
}
