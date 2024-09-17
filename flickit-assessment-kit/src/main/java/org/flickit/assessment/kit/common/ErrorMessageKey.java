package org.flickit.assessment.kit.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON = "translate-kit-dsl.unable.to.parse.json";

    public static final String FIND_MATURITY_LEVEL_ID_NOT_FOUND = "find-maturity-level.id.notFound";
    public static final String KIT_ID_NOT_FOUND = "kit.id.notFound";
    public static final String EXPERT_GROUP_ID_NOT_FOUND = "expert-group.id.notFound";
    public static final String QUESTIONNAIRE_ID_NOT_FOUND = "questionnaire.id.notFound";
    public static final String QUESTION_ID_NOT_FOUND = "question.id.notFound";
    public static final String ATTRIBUTE_ID_NOT_FOUND = "attribute.id.notFound";
    public static final String MATURITY_LEVEL_ID_NOT_FOUND = "maturity-level.id.notFound";

    public static final String UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL = "update-kit-by-dsl.kitId.notNull";
    public static final String UPDATE_KIT_BY_DSL_KIT_DSL_ID_NOT_NULL = "update-kit-by-dsl.kit-dsl-id.notNull";
    public static final String UPDATE_KIT_BY_DSL_ADDITION_UNSUPPORTED = "update-kit-by-dsl.addition.unsupported";
    public static final String UPDATE_KIT_BY_DSL_DELETION_UNSUPPORTED = "update-kit-by-dsl.deletion.unsupported";
    public static final String UPDATE_KIT_BY_DSL_ANSWER_OPTION_NOT_FOUND = "update-kit-by-dsl.answer-option.notFound";

    public static final String GET_KIT_DSL_DOWNLOAD_LINK_FILE_PATH_NOT_FOUND = "get-kit-dsl-download-link.filePath.notFound";
    public static final String GET_KIT_DSL_DOWNLOAD_LINK_KIT_ID_NOT_NULL =  "get-kit-dsl-download-link.kitId.notNull";

    public static final String GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_NULL = "grant-user-access-to-kit.kitId.notNull";
    public static final String GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_NULL = "grant-user-access-to-kit.userId.notNull";
    public static final String GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND = "grant-user-access-to-kit.kitId.notFound";
    public static final String GRANT_USER_ACCESS_TO_KIT_USER_ID_NOT_FOUND = "grant-user-access-to-kit.userId.notFound";
    public static final String GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE = "grant-user-access-to-kit.userId.duplicate";

    public static final String GET_KIT_USER_LIST_KIT_ID_NOT_NULL = "get-kit-user-list.kit-id.notNull";
    public static final String GET_KIT_USER_LIST_PAGE_MIN = "get-kit-user-list.page.min";
    public static final String GET_KIT_USER_LIST_SIZE_MIN = "get-kit-user-list.size.min";
    public static final String GET_KIT_USER_LIST_SIZE_MAX = "get-kit-user-list.size.max";

    public static final String GET_KIT_TAG_LIST_PAGE_MIN = "get-kit-tag-list.page.min";
    public static final String GET_KIT_TAG_LIST_SIZE_MIN = "get-kit-tag-list.size.min";
    public static final String GET_KIT_TAG_LIST_SIZE_MAX = "get-kit-tag-list.size.max";

    public static final String GET_KIT_LIST_IS_PRIVATE_NOT_NULL = "get-kit-list.isPrivate.notNull";
    public static final String GET_KIT_LIST_PAGE_MIN = "get-kit-list.page.min";
    public static final String GET_KIT_LIST_SIZE_MIN = "get-kit-list.size.min";
    public static final String GET_KIT_LIST_SIZE_MAX = "get-kit-list.size.max";

    public static final String SEARCH_KIT_OPTIONS_PAGE_MIN = "search-kit-options.page.min";
    public static final String SEARCH_KIT_OPTION_SIZE_MIN = "search-kit-options.size.min";
    public static final String SEARCH_KIT_OPTIONS_SIZE_MAX = "search-kit-options.size.max";

    public static final String GET_KIT_MINIMAL_INFO_KIT_ID_NOT_NULL = "get-kit-minimal-info.kitId.notNull";
    public static final String GET_KIT_MINIMAL_INFO_KIT_ID_NOT_FOUND = "get-kit-minimal-info.kitId.notFound";

    public static final String DELETE_KIT_USER_ACCESS_KIT_ID_NOT_NULL = "delete-kit-user-access.kitId.notNull";
    public static final String DELETE_KIT_USER_ACCESS_USER_ID_NOT_NULL = "delete-kit-user-access.userId.notNull";
    public static final String DELETE_KIT_USER_ACCESS_KIT_USER_NOT_FOUND = "delete-kit-user-access.kit-user.notFound";
    public static final String DELETE_KIT_USER_ACCESS_KIT_ID_NOT_FOUND = "delete-kit-user-access.kitId.notFound";
    public static final String DELETE_KIT_USER_ACCESS_USER_NOT_FOUND = "delete-kit-user-access.user.notFound";
    public static final String DELETE_KIT_USER_ACCESS_USER_IS_EXPERT_GROUP_OWNER = "delete-kit-user-access.userId.isExpertGroupOwner";

    public static final String DELETE_KIT_KIT_ID_NOT_NULL = "delete-kit.kitId.notNull";
    public static final String DELETE_KIT_HAS_ASSESSMENT = "delete-kit.hasAssessment";

    public static final String UPLOAD_KIT_DSL_KIT_NOT_NULL = "upload-kit.dsl-file.notNull";
    public static final String UPLOAD_KIT_DSL_EXPERT_GROUP_ID_NOT_NULL = "upload-kit.expert-group-id.notNull";
    public static final String UPLOAD_KIT_DSL_DSL_HAS_ERROR = "upload-kit.dsl.has-error";

    public static final String CREATE_KIT_BY_DSL_KIT_DSL_ID_NOT_NULL = "create-kit-by-dsl.kit-dsl-id.notNull";
    public static final String CREATE_KIT_BY_DSL_KIT_DSL_NOT_FOUND = "create-kit-by-dsl.kit-dsl.notFound";
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

    public static final String GET_KIT_STATS_KIT_ID_NOT_NULL = "get-kit-stats.kitId.notNull";

    public static final String GET_KIT_EDITABLE_INFO_KIT_ID_NOT_NULL = "get-kit-editable-info.kitId.notNull";

    public static final String UPDATE_KIT_INFO_KIT_ID_NOT_NULL = "update-kit-info.kitId.notNull";
    public static final String UPDATE_KIT_INFO_KIT_ID_NOT_FOUND = "update-kit-info.kitId.notFound";
    public static final String UPDATE_KIT_INFO_TITLE_SIZE_MIN = "update-kit-info.title.size.min";
    public static final String UPDATE_KIT_INFO_TITLE_SIZE_MAX = "update-kit-info.title.size.max";
    public static final String UPDATE_KIT_INFO_SUMMARY_SIZE_MIN = "update-kit-info.summary.size.min";
    public static final String UPDATE_KIT_INFO_SUMMARY_SIZE_MAX = "update-kit-info.summary.size.max";
    public static final String UPDATE_KIT_INFO_ABOUT_SIZE_MIN = "update-kit-info.about.size.min";
    public static final String UPDATE_KIT_INFO_ABOUT_SIZE_MAX = "update-kit-info.about.size.max";
    public static final String UPDATE_KIT_INFO_TAGS_SIZE_MIN = "update-kit-info.tags.size.min";
    public static final String UPDATE_KIT_INFO_TAG_ID_NOT_FOUND = "update-kit-info.tagId.notFount";

    public static final String GET_KIT_DETAIL_KIT_ID_NOT_NULL = "get-kit-detail.kitId.notNull";

    public static final String GET_KIT_SUBJECT_DETAIL_KIT_ID_NOT_NULL = "get-kit-subject-detail.kitId.notNull";
    public static final String GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_NULL = "get-kit-subject-detail.subjectId.notNull";
    public static final String GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND = "get-kit-subject-detail.subjectId.notFound";

    public static final String GET_KIT_ATTRIBUTE_DETAIL_KIT_ID_NOT_NULL = "get-kit-attribute-detail.kitId.notNull";
    public static final String GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_NULL = "get-kit-attribute-detail.attributeId.notNull";
    public static final String GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_FOUND = "get-kit-attribute-detail.attributeId.notFound";

    public static final String GET_KIT_QUESTIONNAIRE_KIT_ID_NOT_NULL = "get-kit-questionnaire.kitId.notNull";
    public static final String GET_KIT_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL = "get-kit-questionnaire.questionnaireId.notNull";

    public static final String GET_KIT_QUESTION_DETAIL_KIT_ID_NOT_NULL = "get-kit-question-detail.kitId.notNull";
    public static final String GET_KIT_QUESTION_DETAIL_QUESTION_ID_NOT_NULL = "get-kit-question-detail.questionId.notNull";

    public static final String GET_ATTRIBUTE_LEVEL_QUESTIONS_KIT_ID_NOT_NULL = "get-attribute-level-questions.kitId.notNull";
    public static final String GET_ATTRIBUTE_LEVEL_QUESTIONS_ATTRIBUTE_ID_NOT_NULL = "get-attribute-level-questions.attributeId.notNull";
    public static final String GET_ATTRIBUTE_LEVEL_QUESTIONS_MATURITY_LEVEL_ID_NOT_NULL = "get-attribute-level-questions.maturityLevel.notNull";

    public static final String TOGGLE_KIT_LIKE_KIT_ID_NOT_NULL = "toggle-kit-like.kitId.notNull";

    public static final String GET_EXPERT_GROUP_KIT_LIST_EXPERT_GROUP_ID_NOT_NULL = "get-expert-group-kit-list.expertGroupId.notNull";
    public static final String GET_EXPERT_GROUP_KIT_LIST_PAGE_MIN = "get-expert-group-kit-list.page.min";
    public static final String GET_EXPERT_GROUP_KIT_LIST_SIZE_MIN = "get-expert-group-kit-list.size.min";
    public static final String GET_EXPERT_GROUP_KIT_LIST_SIZE_MAX = "get-expert-group-kit-list.size.max";

    public static final String GET_PUBLISHED_KIT_KIT_ID_NOT_NULL = "get-published-kit.kitId.notNull";

    public static final String UPDATE_KIT_ATTRIBUTE_KIT_ID_NOT_NULL = "update-kit-attribute.kitId.notNull";
    public static final String UPDATE_KIT_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL = "update-kit-attribute.attributeId.notNull";
    public static final String UPDATE_KIT_ATTRIBUTE_CODE_NOT_BLANK = "update-kit-attribute.code.notBlank";
    public static final String UPDATE_KIT_ATTRIBUTE_CODE_SIZE_MIN = "update-kit-attribute.code.sizeMin";
    public static final String UPDATE_KIT_ATTRIBUTE_CODE_SIZE_MAX = "update-kit-attribute.code.sizeMax";
    public static final String UPDATE_KIT_ATTRIBUTE_TITLE_NOT_BLANK = "update-kit-attribute.title.notBlank";
    public static final String UPDATE_KIT_ATTRIBUTE_TITLE_SIZE_MIN = "update-kit-attribute.title.sizeMin";
    public static final String UPDATE_KIT_ATTRIBUTE_TITLE_SIZE_MAX = "update-kit-attribute.title.sizeMax";
    public static final String UPDATE_KIT_ATTRIBUTE_DESCRIPTION_NOT_BLANK = "update-kit-attribute.description.notBlank";
    public static final String UPDATE_KIT_ATTRIBUTE_DESCRIPTION_SIZE_MIN = "update-kit-attribute.description.sizeMin";
    public static final String UPDATE_KIT_ATTRIBUTE_SUBJECT_ID_NOT_NULL = "update-kit-attribute.subjectId.notNull";
    public static final String UPDATE_KIT_ATTRIBUTE_INDEX_NOT_NULL = "update-kit-attribute.index.notNull";
    public static final String UPDATE_KIT_ATTRIBUTE_WEIGHT_NOT_NULL = "update-kit-attribute.weight.notNull";
    public static final String UPDATE_KIT_ATTRIBUTE_WEIGHT_MIN = "update-kit-attribute.weight.min";

    public static String entityNameSingleFirst(String fieldName) {
        return "entities.%s.single.first".formatted(fieldName);
    }

    public static String entityNamePlural(String fieldName) {
        return "entities.%s.plural".formatted(fieldName);
    }
}
