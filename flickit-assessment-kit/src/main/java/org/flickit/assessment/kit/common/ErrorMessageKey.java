package org.flickit.assessment.kit.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON = "translate-kit-dsl.unable.to.parse.json";

    public static final String FIND_MATURITY_LEVEL_ID_NOT_FOUND = "find-maturity-level.id.notFound";
    public static final String KIT_ID_NOT_FOUND = "kit.id.notFound";
    public static final String EXPERT_GROUP_ID_NOT_FOUND = "expert-group.id.notFound";

    public static final String UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL = "update-kit-by-dsl.kitId.notNull";
    public static final String UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL = "update-kit-by-dsl.dsl-content.notNull";

    public static final String UPDATE_KIT_BY_DSL_ADDITION_UNSUPPORTED = "update-kit-by-dsl.addition.unsupported";
    public static final String UPDATE_KIT_BY_DSL_DELETION_UNSUPPORTED = "update-kit-by-dsl.deletion.unsupported";

    public static final String UPDATE_KIT_BY_DSL_ANSWER_OPTION_NOT_FOUND = "update-kit-by-dsl.answer-option.notFound";

    public static final String GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL = "grant-user-access-to-kit.kitId.notNull";
    public static final String GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_NULL = "grant-user-access-to-kit.userId.notNull";
    public static final String GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND = "grant-user-access-to-kit.kitId.notFound";
    public static final String GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_FOUND = "grant-user-access-to-kit.userId.notFound";
    public static final String GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE = "grant-user-access-to-kit.userId.duplicate";

    public static final String GET_KIT_USER_LIST_KIT_ID_NOT_NULL = "get-kit-user-list.kit-id.notNull";
    public static final String GET_KIT_USER_LIST_PAGE_MIN = "get-kit-user-list.page.min";
    public static final String GET_KIT_USER_LIST_SIZE_MIN = "get-kit-user-list.size.min";
    public static final String GET_KIT_USER_LIST_SIZE_MAX = "get-kit-user-list.size.max";

    public static final String GET_KIT_MINIMAL_INFO_KIT_ID_NOT_NULL = "get-kit-minimal-info.kitId.notNull";
    public static final String GET_KIT_MINIMAL_INFO_KIT_ID_NOT_FOUND = "get-kit-minimal-info.kitId.notFound";

    public static final String DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL = "delete-kit-user-access.kitId.notNull";
    public static final String DELETE_KIT_USER_ACCESS_USER_ID_NOT_NULL = "delete-kit-user-access.userId.notNull";
    public static final String DELETE_KIT_USER_ACCESS_KIT_USER_NOT_FOUND = "delete-kit-user-access.kit-user.notFound";
    public static final String DELETE_KIT_USER_ACCESS_KIT_ID_NOT_FOUND = "delete-kit-user-access.kitId.notFound";
    public static final String DELETE_KIT_USER_ACCESS_USER_NOT_FOUND = "delete-kit-user-access.user.notFound";

    public static final String GET_USER_BY_EMAIL_EMAIL_NOT_NULL = "get-user-by-email.email.notNull";
    public static final String GET_USER_BY_EMAIL_EMAIL_NOT_FOUND = "get-user-by-email.email.notFound";

    public static final String GET_EXPERT_GROUP_LIST_PAGE_MIN = "get-expert-group-list.page.min";
    public static final String GET_EXPERT_GROUP_LIST_SIZE_MIN = "get-expert-group-list.size.min";
    public static final String GET_EXPERT_GROUP_LIST_SIZE_MAX = "get-expert-group-list.size.max";

    public static final String CREATE_EXPERT_GROUP_TITLE_NOT_BLANK = "create-expert-group.title.notBlank";
    public static final String CREATE_EXPERT_GROUP_TITLE_SIZE_MIN = "create-expert-group.title.size.min";
    public static final String CREATE_EXPERT_GROUP_TITLE_SIZE_MAX = "create-expert-group.title.size.max";
    public static final String CREATE_EXPERT_GROUP_BIO_NOT_BLANK = "create-expert-group.bio.notBlank";
    public static final String CREATE_EXPERT_GROUP_BIO_SIZE_MIN = "create-expert-group.bio.size.min";
    public static final String CREATE_EXPERT_GROUP_BIO_SIZE_MAX = "create-expert-group.bio.size.max";
    public static final String CREATE_EXPERT_GROUP_PICTURE_FILE_FORMAT = "create-expert-group.picture.file-format";
    public static final String CREATE_EXPERT_GROUP_PICTURE_SIZE_MAX = "create-expert-group.picture.size.max";
    public static final String CREATE_EXPERT_GROUP_ABOUT_NOT_BLANK = "create-expert-group.about.notBlank";
    public static final String CREATE_EXPERT_GROUP_ABOUT_SIZE_MIN = "create-expert-group.about.size.min";
    public static final String CREATE_EXPERT_GROUP_ABOUT_SIZE_MAX = "create-expert-group.about.size.max";
    public static final String CREATE_EXPERT_GROUP_WEBSITE_NOT_URL = "create-expert-group.website.notUrl";
    public static final String CREATE_EXPERT_GROUP_WEBSITE_SIZE_MIN = "create-expert-group.website.size.min";
    public static final String CREATE_EXPERT_GROUP_WEBSITE_SIZE_MAX = "create-expert-group.website.size.max";

    public static final String UPLOAD_KIT_DSL_KIT_NOT_NULL = "upload-kit.dsl-file.notNull";
    public static final String UPLOAD_KIT_DSL_EXPERT_GROUP_ID_NOT_NULL = "upload-kit.expert-group-id.notNull";

    public static final String CREATE_KIT_BY_DSL_KIT_DSL_ID_NOT_NULL = "create-kit-by-dsl.kit-dsl-id.notNull";
    public static final String CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND = "create-kit-by-dsl.kit-dsl.notFound";
    public static final String CREATE_KIT_BY_DSL_KIT_DSL_FILE_NOT_FOUND = "create-kit-by-dsl.kit-dsl-file.notFound";
    public static final String CREATE_KIT_BY_DSL_TITLE_NOT_NULL = "create-kit-by-dsl.title.notNull";
    public static final String CREATE_KIT_BY_DSL_TITLE_SIZE_MIN = "create-kit-by-dsl.title.size.min";
    public static final String CREATE_KIT_BY_DSL_TITLE_SIZE_MAX = "create-kit-by-dsl.title.size.max";
    public static final String CREATE_KIT_BY_DSL_KIT_TITLE_DUPLICATE = "create-kit-by-dsl.title.duplicate";
    public static final String CREATE_KIT_BY_DSL_SUMMARY_NOT_NULL = "create-kit-by-dsl.summary.notNull";
    public static final String CREATE_KIT_BY_DSL_SUMMARY_SIZE_MIN = "create-kit-by-dsl.summary.size.min";
    public static final String CREATE_KIT_BY_DSL_SUMMARY_SIZE_MAX = "create-kit-by-dsl.summary.size.max";
    public static final String CREATE_KIT_BY_DSL_ABOUT_NOT_NULL = "create-kit-by-dsl.about.notNull";
    public static final String CREATE_KIT_BY_DSL_ABOUT_SIZE_MIN = "create-kit-by-dsl.about.size.min";
    public static final String CREATE_KIT_BY_DSL_ABOUT_SIZE_MAX = "create-kit-by-dsl.about.size.max";
    public static final String CREATE_KIT_BY_DSL_IS_PRIVATE_NOT_NULL = "create-kit-by-dsl.isPrivate.notNull";
    public static final String CREATE_KIT_BY_DSL_TAG_IDS_NOT_NULL = "create-kit-by-dsl.tag-ids.notNull";
    public static final String CREATE_KIT_BY_DSL_EXPERT_GROUP_ID_NOT_NULL = "create-kit-by-dsl.expert-group-id.notNull";
    public static final String CREATE_EXPERT_GROUP_DUPLICATE_TITLE = "create-expert-group.duplicate.title";

    public static String entityNameSingleFirst(String fieldName) {
        return String.format("entities.%s.single.first", fieldName);
    }

    public static String entityNamePlural(String fieldName) {
        return String.format("entities.%s.plural", fieldName);
    }

}
