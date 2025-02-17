package org.flickit.assessment.common.util;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsTest {

    @Test
    public void testIsMetadataEmpty_withNull_shouldReturnTrue() {
        assertThat(ClassUtils.isMetadataEmpty(null)).isTrue();
    }

    @Test
    public void testIsMetadataEmpty_withBlankString_shouldReturnTrue() {
        assertThat(ClassUtils.isMetadataEmpty("")).isTrue();
        assertThat(ClassUtils.isMetadataEmpty("   ")).isTrue();
    }

    @Test
    public void testIsMetadataEmpty_withEmptyCollection_shouldReturnTrue() {
        assertThat(ClassUtils.isMetadataEmpty(Collections.emptyList())).isTrue();
        assertThat(ClassUtils.isMetadataEmpty(Collections.emptySet())).isTrue();
    }

    @Test
    public void testIsMetadataEmpty_withEmptyArray_shouldReturnTrue() {
        assertThat(ClassUtils.isMetadataEmpty(new int[]{})).isTrue();
        assertThat(ClassUtils.isMetadataEmpty(new String[]{})).isTrue();
    }

    @Test
    public void testIsMetadataEmpty_withEmptyMap_shouldReturnTrue() {
        assertThat(ClassUtils.isMetadataEmpty(Collections.emptyMap())).isTrue();
    }

    @Test
    public void testIsMetadataEmpty_withNonBlankString_shouldReturnFalse() {
        assertThat(ClassUtils.isMetadataEmpty("  a  ")).isFalse();
    }

    @Test
    public void testIsMetadataEmpty_withNonEmptyCollection_shouldReturnFalse() {
        assertThat(ClassUtils.isMetadataEmpty(List.of(1, 2, 3))).isFalse();
        assertThat(ClassUtils.isMetadataEmpty(Set.of("a", "b"))).isFalse();
    }

    @Test
    public void testIsMetadataEmpty_withNonEmptyMap_shouldReturnFalse() {
        assertThat(ClassUtils.isMetadataEmpty(Map.of(1, "one", 2, "two"))).isFalse();
    }

    @Test
    public void testIsMetadataEmpty_withNonEmptyArray_shouldReturnFalse() {
        assertThat(ClassUtils.isMetadataEmpty(new int[]{1, 2, 3})).isFalse();
        assertThat(ClassUtils.isMetadataEmpty(new String[]{"A"})).isFalse();
    }
}
