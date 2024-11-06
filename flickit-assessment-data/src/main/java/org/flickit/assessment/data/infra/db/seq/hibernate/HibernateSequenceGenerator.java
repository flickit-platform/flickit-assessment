package org.flickit.assessment.data.infra.db.seq.hibernate;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.infra.db.seq.api.SequenceGenerator;
import org.flickit.assessment.data.infra.db.seq.hibernate.internal.HibernateDbSequenceGenerator;

@RequiredArgsConstructor
public class HibernateSequenceGenerator implements SequenceGenerator {

    private final HibernateDbSequenceGenerator hibernateDbSequenceGenerator;

    @Override
    public Long generate(String sequenceName) {
        return hibernateDbSequenceGenerator.generate(sequenceName);
    }
}
