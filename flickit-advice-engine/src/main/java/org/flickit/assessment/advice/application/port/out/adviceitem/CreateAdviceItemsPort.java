package org.flickit.assessment.advice.application.port.out.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;

import java.util.List;

public interface CreateAdviceItemsPort {

    void persist(List<AdviceItem> adviceItems);
}
