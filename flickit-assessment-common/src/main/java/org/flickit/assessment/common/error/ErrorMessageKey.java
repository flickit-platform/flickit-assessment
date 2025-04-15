package org.flickit.assessment.common.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String COMMON_CURRENT_USER_NOT_ALLOWED = "common.currentUser.notAllowed";
    public static final String COMMON_CURRENT_USER_ID_NOT_NULL = "common.currentUserId.notNull";
    public static final String COMMON_HEADER_AUTHORIZATION_NOT_NULL = "common.header.authorization.notNull";
    public static final String COMMON_CURRENT_USER_NOT_FOUND = "common.currentUser.notFound";

    public static final String COMMON_ASSESSMENT_RESULT_NOT_FOUND = "common.assessmentResult.notFound";
    public static final String COMMON_ASSESSMENT_RESULT_NOT_VALID = "common.assessmentResult.notValid";
    public static final String COMMON_ASSESSMENT_KIT_NOT_FOUND = "common.assessmentKit.notFound";
    public static final String COMMON_ASSESSMENT_RESULT_KIT_VERSION_DEPRECATED = "common.assessmentResult.kitVersion.deprecated";

    public static final String UPLOAD_FILE_PICTURE_SIZE_MAX = "upload-file.picture-size.max";
    public static final String UPLOAD_FILE_DSL_SIZE_MAX = "upload-file.dsl-size.max";
    public static final String UPLOAD_FILE_SIZE_MAX = "upload-file.size.max";
    public static final String UPLOAD_FILE_FORMAT_NOT_VALID = "upload-file.format.notValid";

    public static final String COMMON_EMAIL_FORMAT_NOT_VALID = "common.email.format.notValid";

    public static final String FILE_STORAGE_FILE_NOT_FOUND = "file-storage.file.notFound";

    public static final String COMMON_SPACE_ID_NOT_FOUND = "common.space.notFound";

    public static final String INVITE_TO_REGISTER_EMAIL_SUBJECT = "invite-to-register.email.subject";
    public static final String INVITE_TO_REGISTER_EMAIL_BODY = "invite-to-register.email.body";
    public static final String INVITE_TO_REGISTER_EMAIL_BODY_WITHOUT_SUPPORT_EMAIL = "invite-to-register.email.body.without.support.email";

    public static final String COMMON_KIT_LANGUAGE_NOT_VALID = "kit-language.notValid";

    public static final String TRANSLATION_ATTRIBUTE_TITLE_SIZE_MIN = "translations-attribute.title.size.min";
    public static final String TRANSLATION_ATTRIBUTE_TITLE_SIZE_MAX = "translations-attribute.title.size.max";
    public static final String TRANSLATION_ATTRIBUTE_DESCRIPTION_SIZE_MIN = "translations-attribute.description.size.min";
    public static final String TRANSLATION_ATTRIBUTE_DESCRIPTION_SIZE_MAX = "translations-attribute.description.size.max";

    public static final String TRANSLATION_SUBJECT_TITLE_SIZE_MIN = "translations-subject.title.size.min";
    public static final String TRANSLATION_SUBJECT_TITLE_SIZE_MAX = "translations-subject.title.size.max";
    public static final String TRANSLATION_SUBJECT_DESCRIPTION_SIZE_MIN = "translations-subject.description.size.min";
    public static final String TRANSLATION_SUBJECT_DESCRIPTION_SIZE_MAX = "translations-subject.description.size.max";

    public static final String TRANSLATION_ASSESSMENT_KIT_TITLE_SIZE_MIN = "translations-assessmentKit.title.size.min";
    public static final String TRANSLATION_ASSESSMENT_KIT_TITLE_SIZE_MAX = "translations-assessmentKit.title.size.max";
    public static final String TRANSLATION_ASSESSMENT_KIT_SUMMARY_SIZE_MIN = "translations-assessmentKit.summary.size.min";
    public static final String TRANSLATION_ASSESSMENT_KIT_SUMMARY_SIZE_MAX = "translations-assessmentKit.summary.size.max";
    public static final String TRANSLATION_ASSESSMENT_KIT_ABOUT_SIZE_MIN = "translations-assessmentKit.about.size.min";
    public static final String TRANSLATION_ASSESSMENT_KIT_ABOUT_SIZE_MAX = "translations-assessmentKit.about.size.max";

    public static final String TRANSLATION_QUESTION_TITLE_SIZE_MIN = "translations-question.title.size.min";
    public static final String TRANSLATION_QUESTION_TITLE_SIZE_MAX = "translations-question.title.size.max";
    public static final String TRANSLATION_QUESTION_HINT_SIZE_MIN = "translations-question.hint.size.min";
    public static final String TRANSLATION_QUESTION_HINT_SIZE_MAX = "translations-question.hint.size.max";
}
