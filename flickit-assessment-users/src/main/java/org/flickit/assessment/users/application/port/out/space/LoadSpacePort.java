package org.flickit.assessment.users.application.port.out.space;

public interface LoadSpacePort {

    Result loadById(long spaceId);

    record Result(String title){
    }
}
