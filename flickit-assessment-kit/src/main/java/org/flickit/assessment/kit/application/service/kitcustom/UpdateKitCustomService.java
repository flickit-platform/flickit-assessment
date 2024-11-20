package org.flickit.assessment.kit.application.service.kitcustom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitCustomService implements UpdateKitCustomUseCase {

    @Override
    public void updateKitCustom(Param param) {

    }
}
