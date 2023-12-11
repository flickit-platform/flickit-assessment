package org.flickit.assessment.kit.application.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.user.DeleteUserAccessUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitByIdPort;
import org.flickit.assessment.kit.application.port.out.kituser.LoadKitUserByKitAndUserPort;
import org.flickit.assessment.kit.application.port.out.user.DeleteUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserAccessService implements DeleteUserAccessUseCase {

    private final LoadKitByIdPort loadKitByIdPort;
    private final LoadUserByIdPort loadUserByIdPort;
    private final LoadKitUserByKitAndUserPort loadKitUserByKitAndUserPort;
    private final DeleteUserAccessPort deleteUserAccessPort;

    @Override
    public void delete(Param param) {
        loadKitByIdPort.load(param.getKitId()).orElseThrow(() -> new ResourceNotFoundException(DELETE_USER_ACCESS_KIT_NOT_FOUND));
        loadUserByIdPort.load(param.getUserId()).orElseThrow(() -> new ResourceNotFoundException(DELETE_USER_ACCESS_USER_NOT_FOUND));
        loadKitUserByKitAndUserPort.loadByKitAndUser(param.getKitId(), param.getUserId()).orElseThrow(
            () -> new ResourceNotFoundException(DELETE_USER_ACCESS_KIT_USER_NOT_FOUND)
        );

        deleteUserAccessPort.delete(param.getKitId(), param.getUserId());
        log.debug("User [{}] access to private kit [{}] is removed.", param.getUserId(), param.getUserId());
    }
}
