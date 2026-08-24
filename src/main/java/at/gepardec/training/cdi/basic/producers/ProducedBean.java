package at.gepardec.training.cdi.basic.producers;

import java.io.Serializable;

/**
 * {@code @Vetoed} is necessary because we use bean-discovery-mode 'all'
 * <p>
 * Spring only registers what carries a stereotype, so the counterpart of {@code @Vetoed} is
 * the absence of any annotation: this class never becomes a bean on its own and only reaches
 * the container through the factory method in {@link Producer}.
 */
public class ProducedBean implements Serializable {
    private int value = 0;

    public int getValue() {
        return ++value;
    }
}
