package org.flickit.assessment.data.infra.db.seq.hibernate.internal;

import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;

public class HibernateSeqSupportedDbSequenceGenerator extends HibernateDbSequenceGenerator {

	public HibernateSeqSupportedDbSequenceGenerator(JdbcTemplate jdbcTemplate, Dialect dialect) {
		super(jdbcTemplate, dialect);
	}

	@Override
	public Long generate(String sequenceName) {
		String nextValSql = dialect.getSequenceSupport().getSequenceNextValString(sequenceName);
		return jdbcTemplate.queryForObject(nextValSql, Long.class);
	}
}
