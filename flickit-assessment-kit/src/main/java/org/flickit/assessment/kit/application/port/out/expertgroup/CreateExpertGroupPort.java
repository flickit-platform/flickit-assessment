package org.flickit.assessment.kit.application.port.out.expertgroup;


import java.util.UUID;

public interface CreateExpertGroupPort {
    Long persist(Param param);

    record Param(String name,
                 String about,
                 String picture,
                 String website,
                 String bio,
                 UUID currentUserId) {}
}
