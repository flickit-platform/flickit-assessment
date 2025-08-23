package org.flickit.assessment.common.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AspectOrdersTest {

    @Test
    public void testAspectOrders() {
        assertThat(AspectOrders.NOTIFICATION_ORDER).isLessThan(AspectOrders.TRANSACTIONAL_ORDER);
    }
}
