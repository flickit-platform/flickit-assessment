package org.flickit.assessment.data.infra.db.seq.hibernate.internal;

import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;

public class HibernateSeqNotSupportedDbSequenceGenerator extends HibernateDbSequenceGenerator {

	public HibernateSeqNotSupportedDbSequenceGenerator(JdbcTemplate jdbcTemplate, Dialect dialect) {
		super(jdbcTemplate, dialect);
	}

	@Override
	public Long generate(String sequenceName) {
		jdbcTemplate.execute(incrementSql(sequenceName));
		return jdbcTemplate.queryForObject(nextValSql(sequenceName), Long.class);
	}

	private String incrementSql(String sequenceName) {
		return "UPDATE " + sequenceName + " SET next_val = next_val + 1";
	}

	private String nextValSql(String sequenceName) {
		return "SELECT next_val from " + sequenceName;
	}
}
