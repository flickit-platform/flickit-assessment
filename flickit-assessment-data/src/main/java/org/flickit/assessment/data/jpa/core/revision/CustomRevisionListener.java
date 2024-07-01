package org.flickit.assessment.data.jpa.core.revision;

import org.hibernate.envers.RevisionListener;

import java.time.LocalDateTime;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionEntity customRevisionEntity = (RevisionEntity) revisionEntity;
        customRevisionEntity.setLastModificationTime(LocalDateTime.now());
    }
}
