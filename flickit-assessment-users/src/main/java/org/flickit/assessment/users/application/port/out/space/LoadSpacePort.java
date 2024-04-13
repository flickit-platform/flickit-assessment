package org.flickit.assessment.users.application.port.out.space;

public interface LoadSpacePort {

    Result getById(long spaceId);

    record Result(String title){
    }
}
