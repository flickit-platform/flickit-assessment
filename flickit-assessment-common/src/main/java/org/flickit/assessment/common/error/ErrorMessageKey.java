package org.flickit.assessment.common.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String COMMON_CURRENT_USER_NOT_ALLOWED = "common.currentUser.notAllowed";
    public static final String COMMON_CURRENT_USER_ID_NOT_NULL = "common.currentUserId.notNull";
    public static final String COMMON_AUTHORIZATION_HEADER_NOT_NULL = "common.authorizationHeader.notNull";

}
