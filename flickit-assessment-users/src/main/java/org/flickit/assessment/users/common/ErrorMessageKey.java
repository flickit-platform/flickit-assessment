package org.flickit.assessment.users.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String FILE_STORAGE_FILE_NOT_FOUND = "file-storage.file.notFound";

    public static final String EXPERT_GROUP_ID_NOT_FOUND = "expert-group.id.notFound";

    public static final String GET_EXPERT_GROUP_LIST_PAGE_MIN = "get-expert-group-list.page.min";
    public static final String GET_EXPERT_GROUP_LIST_SIZE_MIN = "get-expert-group-list.size.min";
    public static final String GET_EXPERT_GROUP_LIST_SIZE_MAX = "get-expert-group-list.size.max";

    public static final String GET_EXPERT_GROUP_EXPERT_GROUP_NOT_FOUND = "get-expert-group.expert-group.notFound";
    public static final String GET_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL = "get-expert-group.expert-group-id.notNull";

    public static final String CREATE_EXPERT_GROUP_TITLE_NOT_BLANK = "create-expert-group.title.notBlank";
    public static final String CREATE_EXPERT_GROUP_TITLE_SIZE_MIN = "create-expert-group.title.size.min";
    public static final String CREATE_EXPERT_GROUP_TITLE_SIZE_MAX = "create-expert-group.title.size.max";
    public static final String CREATE_EXPERT_GROUP_BIO_NOT_BLANK = "create-expert-group.bio.notBlank";
    public static final String CREATE_EXPERT_GROUP_BIO_SIZE_MIN = "create-expert-group.bio.size.min";
    public static final String CREATE_EXPERT_GROUP_BIO_SIZE_MAX = "create-expert-group.bio.size.max";
    public static final String CREATE_EXPERT_GROUP_ABOUT_NOT_BLANK = "create-expert-group.about.notBlank";
    public static final String CREATE_EXPERT_GROUP_ABOUT_SIZE_MIN = "create-expert-group.about.size.min";
    public static final String CREATE_EXPERT_GROUP_ABOUT_SIZE_MAX = "create-expert-group.about.size.max";
    public static final String CREATE_EXPERT_GROUP_WEBSITE_NOT_URL = "create-expert-group.website.notUrl";
    public static final String CREATE_EXPERT_GROUP_WEBSITE_SIZE_MIN = "create-expert-group.website.size.min";
    public static final String CREATE_EXPERT_GROUP_WEBSITE_SIZE_MAX = "create-expert-group.website.size.max";

    public static final String INVITE_EXPERT_GROUP_MEMBER_OWNER_ID_ACCESS_DENIED = "invite-expert-group-member.owner-id.accessDenied";

}
