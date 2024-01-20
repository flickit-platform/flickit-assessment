package application.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public final class Target {

    private final int currentGain;
    private final int minGain;
    private final int neededGain;
    @Setter
    private List<Question> questions;

    public Target(int currentGain, int minGain) {
        this.currentGain = currentGain;
        this.minGain = minGain;
        this.neededGain = minGain - currentGain;
    }

    @Override
    public String toString() {
        return "Target[" +
            "currentGain=" + currentGain + ", " +
            "minGain=" + minGain + ", " +
            "neededGain=" + neededGain + ']';
    }

    public int getScore() {
        Double gain = questions.stream().map(q -> q.getTargetGain(this))
            .reduce(Double::sum)
            .orElse((double) 0);
        return (int) Math.ceil(neededGain - gain);
    }

}
