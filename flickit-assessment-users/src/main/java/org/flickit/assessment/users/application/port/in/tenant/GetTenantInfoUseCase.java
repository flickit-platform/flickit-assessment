package org.flickit.assessment.users.application.port.in.tenant;

public interface GetTenantInfoUseCase {

    Result getTenantInfo();

    record Result(String name){
    }
}
