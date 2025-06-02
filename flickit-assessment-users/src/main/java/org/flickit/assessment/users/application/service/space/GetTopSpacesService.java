package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTopSpacesService implements GetTopSpacesUseCase {

    @Override
    public SpaceListItem getSpaceList(Param param) {
        return null;
    }
}
