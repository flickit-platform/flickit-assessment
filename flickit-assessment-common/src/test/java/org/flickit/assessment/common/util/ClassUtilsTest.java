package org.flickit.assessment.common.util;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsTest {

    @Test
    public void testIsFieldEmpty_withNull_shouldReturnTrue() {
        assertThat(ClassUtils.isFieldEmpty(null)).isTrue();
    }

    @Test
    public void testIsFieldEmpty_withBlankString_shouldReturnTrue() {
        assertThat(ClassUtils.isFieldEmpty("")).isTrue();
        assertThat(ClassUtils.isFieldEmpty("   ")).isTrue();
    }

    @Test
    public void testIsFieldEmpty_withEmptyCollection_shouldReturnTrue() {
        assertThat(ClassUtils.isFieldEmpty(Collections.emptyList())).isTrue();
        assertThat(ClassUtils.isFieldEmpty(Collections.emptySet())).isTrue();
    }

    @Test
    public void testIsFieldEmpty_withEmptyArray_shouldReturnTrue() {
        assertThat(ClassUtils.isFieldEmpty(new int[]{})).isTrue();
        assertThat(ClassUtils.isFieldEmpty(new String[]{})).isTrue();
    }

    @Test
    public void testIsFieldEmpty_withEmptyMap_shouldReturnTrue() {
        assertThat(ClassUtils.isFieldEmpty(Collections.emptyMap())).isTrue();
    }

    @Test
    public void testIsFieldEmpty_withNonBlankString_shouldReturnFalse() {
        assertThat(ClassUtils.isFieldEmpty("  a  ")).isFalse();
    }

    @Test
    public void testIsFieldEmpty_withNonEmptyCollection_shouldReturnFalse() {
        assertThat(ClassUtils.isFieldEmpty(List.of(1, 2, 3))).isFalse();
        assertThat(ClassUtils.isFieldEmpty(Set.of("a", "b"))).isFalse();
    }

    @Test
    public void testIsFieldEmpty_withNonEmptyMap_shouldReturnFalse() {
        assertThat(ClassUtils.isFieldEmpty(Map.of(1, "one", 2, "two"))).isFalse();
    }

    @Test
    public void testIsFieldEmpty_withNonEmptyArray_shouldReturnFalse() {
        assertThat(ClassUtils.isFieldEmpty(new int[]{1, 2, 3})).isFalse();
        assertThat(ClassUtils.isFieldEmpty(new String[]{"A"})).isFalse();
    }
}
