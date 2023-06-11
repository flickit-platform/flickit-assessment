package org.flickit.flickitassessmentcore.adapter.in.rest.advice.common;

import java.util.ResourceBundle;

public class ErrorBundleLoader {
    private static final String RESOURCE_BUNDLE_BASE_NAME = "message/error-messages";
    private static final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME);

    public static String getErrorMessage(String template) {
        return bundle.containsKey(template) ? bundle.getString(template) : template;
    }
}
