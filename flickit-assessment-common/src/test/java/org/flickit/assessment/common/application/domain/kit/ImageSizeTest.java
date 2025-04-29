package org.flickit.assessment.common.application.domain.kit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImageSizeTest {

    @Test
    void testImageSize_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, ImageSize.SMALL.ordinal());
        assertEquals(1, ImageSize.LARGE.ordinal());
    }

    @Test
    void testImageSize_NameOfItemsShouldBeChanged() {
        assertEquals("SMALL", ImageSize.SMALL.name());
        assertEquals("LARGE", ImageSize.LARGE.name());
    }

    @Test
    void testImageSize_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, ImageSize.values().length);
    }
}
