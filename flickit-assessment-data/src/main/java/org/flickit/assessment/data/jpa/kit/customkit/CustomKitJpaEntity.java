package org.flickit.assessment.data.jpa.kit.customkit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "fak_custom_kit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomKitJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "kit_id", nullable = false)
    private long kitId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "custom_data", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> customData = new HashMap<>();

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;
}
