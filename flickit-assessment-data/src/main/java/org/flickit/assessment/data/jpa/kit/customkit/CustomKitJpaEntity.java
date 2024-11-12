package org.flickit.assessment.data.jpa.kit.customkit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

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

    @Column(name = "custom_data", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> customData = new HashMap<>();
}
