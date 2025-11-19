import java.util.List;

/**
 * Simple contract that exposes the properties needed by the different routing
 * strategies. Both the existing heuristic Ordering class and the new exact
 * solution implementations conform to this interface so that downstream code
 * (like output formatting) can stay agnostic to the algorithm that generated
 * the plan.
 */
public interface RoutePlan {

    /**
     * Returns the ordered list of points visited in the route, including the
     * depot at the beginning and end of the tour.
     */
    List<Point> getOrder();

    /**
     * Aggregated amount of service successfully completed by the route.
     */
    int getNumberofProcessedRequests();

    /**
     * Loading and unloading cost of the route.
     */
    int getLUCost();

    /**
     * Total distance travelled along the route.
     */
    double getDistance();
}

