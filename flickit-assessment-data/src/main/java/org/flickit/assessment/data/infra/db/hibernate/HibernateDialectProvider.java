package org.flickit.assessment.data.infra.db.hibernate;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.internal.DialectResolverSet;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

import javax.sql.DataSource;

public class HibernateDialectProvider {

	private final Dialect dialect;

	public HibernateDialectProvider(DataSource dataSource) {
		this.dialect = hibernateDialect(dataSource);
	}

	public Dialect getInstance() {
		return dialect;
	}

	private Dialect hibernateDialect(DataSource dataSource) {
		try {
			return new HibernateDialectFactory(dataSource, hibernateDialectResolver()).getObject();
		} catch (Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	private DialectResolver hibernateDialectResolver() {
		DialectResolverSet dialectResolverSet = new DialectResolverSet();
		dialectResolverSet.addResolver(new StandardDialectResolver());
		return dialectResolverSet;
	}

}
