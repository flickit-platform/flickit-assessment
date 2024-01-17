package adapter;

import application.domain.Plan;
import application.port.out.LoadAdviceCalculationInfoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoadAdviceCalculationAdapter implements LoadAdviceCalculationInfoPort {


    @Override
    public Plan load(UUID assessmentId, Map<Long, Long> targets) {

        return null;
    }
}
