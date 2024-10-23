package org.flickit.assessment.data.infra.db.seq.api;

public interface SequenceGenerator {

	Long generate(String sequenceName);

}
