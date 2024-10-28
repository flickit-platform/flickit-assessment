package org.flickit.assessment.data.config.db;

import org.flickit.assessment.data.infra.db.hibernate.HibernateDialectProvider;
import org.flickit.assessment.data.infra.db.seq.hibernate.HibernateSequenceGenerator;
import org.flickit.assessment.data.infra.db.seq.hibernate.internal.HibernateDbSequenceGenerator;
import org.flickit.assessment.data.infra.db.seq.hibernate.internal.HibernateSeqNotSupportedDbSequenceGenerator;
import org.flickit.assessment.data.infra.db.seq.hibernate.internal.HibernateSeqSupportedDbSequenceGenerator;
import org.hibernate.dialect.Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbSequenceGeneratorConfig {

    @Bean
    HibernateSequenceGenerator hibernateSequenceGenerator(JdbcTemplate jdbcTemplate, Dialect dialect) {
        var hibernateDbSequenceGenerator = hibernateDbSequenceGenerator(jdbcTemplate, dialect);
        return new HibernateSequenceGenerator(hibernateDbSequenceGenerator);
    }

    private HibernateDbSequenceGenerator hibernateDbSequenceGenerator(JdbcTemplate jdbcTemplate, Dialect dialect) {
        return dialect.getSequenceSupport().supportsSequences() ?
            new HibernateSeqSupportedDbSequenceGenerator(jdbcTemplate, dialect) :
            new HibernateSeqNotSupportedDbSequenceGenerator(jdbcTemplate, dialect);
    }

    @Bean
    Dialect hibernateDialect(DataSource dataSource) {
        return new HibernateDialectProvider(dataSource).getInstance();
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
