package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExpertGroupAccessId implements Serializable {

    private Long expertGroupId;
    private UUID userId;
}
