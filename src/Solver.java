import java.util.List;

/**
 * Functional interface that encapsulates a VRP-LU solving strategy.
 * Implementations produce a list of {@link RoutePlan} objects for a given query.
 */
@FunctionalInterface
public interface Solver {
    /**
     * Solve the configured query and return the resulting set of route plans.
     *
     * @return ordered list of routes that satisfy (or best-effort satisfy) the query
     */
    List<RoutePlan> solve();
}
