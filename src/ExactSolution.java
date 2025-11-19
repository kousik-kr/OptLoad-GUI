import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable container for an exact solution built by the exact solver. It
 * records the visiting order alongside the objective values that are used by
 * the rest of the application.
 */
public class ExactSolution implements RoutePlan {

    private final List<Point> order;
    private final int processedRequests;
    private final int luCost;
    private final double distance;

    public ExactSolution(List<Point> order, int processedRequests, int luCost, double distance) {
        this.order = Collections.unmodifiableList(new ArrayList<>(order));
        this.processedRequests = processedRequests;
        this.luCost = luCost;
        this.distance = distance;
    }

    @Override
    public List<Point> getOrder() {
        return this.order;
    }

    @Override
    public int getNumberofProcessedRequests() {
        return this.processedRequests;
    }

    @Override
    public int getLUCost() {
        return this.luCost;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }
}

