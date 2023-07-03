package org.flickit.flickitassessmentcore.adapter.out.persistence.maturitylevel;

import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.List;
import java.util.stream.Collectors;

public class MaturityLevelMapper {
    public static LoadMaturityLevelByKitPort.Result toResult(List<MaturityLevelPersistenceJpaAdapter.MaturityLevelDto> dto) {
        return new LoadMaturityLevelByKitPort.Result(
            dto.stream().
                map(MaturityLevelMapper::toDomainModel).
                collect(Collectors.toSet())
        );
    }

    private static MaturityLevel toDomainModel(MaturityLevelPersistenceJpaAdapter.MaturityLevelDto dto) {
        return new MaturityLevel(
            dto.id(),
            dto.title(),
            dto.value(),
            null
        );
    }
}
