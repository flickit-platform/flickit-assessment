package org.flickit.assessment.common.application.domain.kit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KitLanguageTest {

    @Test
    void testKitLanguage_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, KitLanguage.EN.ordinal());
        assertEquals(1, KitLanguage.FA.ordinal());
    }

    @Test
    void testKitLanguage_NameOfItemsShouldNotBeChanged() {
        assertEquals("EN", KitLanguage.EN.name());
        assertEquals("FA", KitLanguage.FA.name());
    }

    @Test
    void testKitLanguage_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, KitLanguage.values().length);
    }

}