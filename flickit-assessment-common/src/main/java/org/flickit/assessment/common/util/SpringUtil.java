package org.flickit.assessment.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class SpringUtil {

    private static ApplicationContext context;

    @Autowired
    public SpringUtil(ApplicationContext context) {
        SpringUtil.context = context;
    }

    @Nullable
    public static <T> T getBeanSafe(Class<T> beanClass) {
        try {
            return getBean(beanClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return context.getBean(beanName, beanClass);
    }
}
