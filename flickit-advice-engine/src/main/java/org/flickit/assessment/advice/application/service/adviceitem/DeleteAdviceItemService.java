package org.flickit.assessment.advice.application.service.adviceitem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.advice.application.port.in.adviceitem.DeleteAdviceItemUseCase;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAdviceItemService implements DeleteAdviceItemUseCase {

    @Override
    public void deleteAdviceItem(Param param) {

    }
}
