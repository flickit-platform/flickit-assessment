package org.flickit.assessment.common.adapter.out.persistence.util;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstraintExtractor {

	/**
	 * - oracle, postgres, mysql pattern: 'schemaName:constraintName' <p>
	 * - h2 pattern: schemaName:constraintName_index_d .*
	 */
	private static final Pattern CONSTRAINT_NAME_PATTERN = Pattern.compile("\\.([a-z]+)");

	@Nullable
	public static String extractConstraint(@Nullable String rawConstraint) {
		String finalConstraint = doExtractConstraint(rawConstraint);
        log.debug("Extract constraint: input=[{}] => output=[{}]", rawConstraint, finalConstraint);
		return finalConstraint;
	}

	private static String doExtractConstraint(@Nullable String rawConstraint) {
		if (isBlank(rawConstraint))
			return null;
		String normalizedConstraint = normalize(rawConstraint);
		Matcher matcher = CONSTRAINT_NAME_PATTERN.matcher(normalizedConstraint);
		if (matcher.find())
			return matcher.group(1);
		return normalizedConstraint;
	}

	private static String normalize(String constraint) {
		return constraint.toLowerCase();
	}
}
