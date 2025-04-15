package org.flickit.assessment.data.jpa.kit.kitbanner;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Entity
@Table(name = "fak_kit_banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitBannerJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "in_slider", nullable = false)
    private Boolean inSlider;

    @Column(name = "lang_id", nullable = false)
    private Integer langId;
}
