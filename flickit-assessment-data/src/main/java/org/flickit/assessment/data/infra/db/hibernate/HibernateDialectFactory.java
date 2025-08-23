package org.flickit.assessment.data.infra.db.hibernate;

import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;
import java.sql.Connection;

@RequiredArgsConstructor
public class HibernateDialectFactory implements FactoryBean<Dialect> {

	private final DataSource dataSource;
	private final DialectResolver resolver;

	private Dialect dialect;

	@Override
	public Dialect getObject() throws Exception {
		if (dialect == null) {
			try (Connection connection = dataSource.getConnection()) {
				dialect = resolver.resolveDialect(new DatabaseMetaDataDialectResolutionInfoAdapter(connection.getMetaData()));
			}
		}
		return dialect;
	}

	@Override
	public Class<?> getObjectType() {
		return Dialect.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
