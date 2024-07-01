package org.flickit.assessment.data.jpa.core.revision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "revinfo")
@org.hibernate.envers.RevisionEntity(CustomRevisionListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevisionEntity extends DefaultRevisionEntity {

    @Column(name = "last_modification_time")
    private LocalDateTime lastModificationTime;
}
