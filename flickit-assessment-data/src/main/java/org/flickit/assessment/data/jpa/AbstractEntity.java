package org.flickit.assessment.data.jpa;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import org.springframework.data.domain.Persistable;

public abstract class AbstractEntity<I> implements Persistable<I> {

    private boolean isNew = true;

    @PostLoad
    @PostPersist
    public void markAsNotNew() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
