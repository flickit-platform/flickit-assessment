package org.flickit.assessment.data.infra.db.seq.hibernate.internal;

import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public abstract class HibernateDbSequenceGenerator {

	protected final JdbcTemplate jdbcTemplate;
	protected final Dialect dialect;

    public abstract Long generate(String sequenceName);
}
