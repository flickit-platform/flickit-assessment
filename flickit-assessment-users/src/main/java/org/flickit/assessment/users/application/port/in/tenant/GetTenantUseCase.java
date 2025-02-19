package org.flickit.assessment.users.application.port.in.tenant;

public interface GetTenantUseCase {

    Result getTenant();

    record Result(String name,
                  Logo logo,
                  boolean aiEnabled) {
    }

    record Logo(String logoLink, String favLink) {
    }
}
