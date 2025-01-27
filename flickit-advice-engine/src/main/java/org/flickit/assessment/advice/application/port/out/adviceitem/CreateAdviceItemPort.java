package org.flickit.assessment.advice.application.port.out.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;

import java.util.List;
import java.util.UUID;

public interface CreateAdviceItemPort {

    UUID persist(AdviceItem adviceItem);

    void persist(List<AdviceItem> adviceItems);
}
