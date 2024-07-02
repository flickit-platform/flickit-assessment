package org.flickit.assessment.users.application.port.in.tenant;

public interface GetTenantLogoUseCase {

    Result getTenantLogo();

    record Result(String logoLink, String favLink){
    }
}
