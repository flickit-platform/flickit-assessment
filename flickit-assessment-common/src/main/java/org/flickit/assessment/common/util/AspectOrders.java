package org.flickit.assessment.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.Ordered;

@UtilityClass
public class AspectOrders {

    public static final int TRANSACTIONAL_ORDER = Ordered.LOWEST_PRECEDENCE;
    public static final int NOTIFICATION_ORDER = TRANSACTIONAL_ORDER - 1;
}
