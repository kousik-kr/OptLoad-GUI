import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * Branch-and-bound implementation inspired by the exact algorithm described in
 * "An Exact Algorithm for the Vehicle Routing Problem with Loading and
 * Unloading Constraints". The solver enumerates feasible pickup and delivery
 * sequences while enforcing precedence, vehicle capacity and time-window
 * constraints. It keeps the best complete tour according to processed demand,
 * travel distance and loading/unloading cost.
 */
public class ExactAlgorithmSolver {

    private static class LegResult {
        final double distance;
        final double arrivalTime;

        LegResult(double distance, double arrivalTime) {
            this.distance = distance;
            this.arrivalTime = arrivalTime;
        }
    }

    private final Query query;
    private final List<Point> pickups;
    private final List<Point> deliveries;
    private final List<Integer> quantities;
    private ExactSolution bestSolution;

    public ExactAlgorithmSolver(Query query) {
        this.query = query;
        this.pickups = new ArrayList<>();
        this.deliveries = new ArrayList<>();
        this.quantities = new ArrayList<>();
        extractRequests();
    }

    private void extractRequests() {
        for (Entry<Integer, Service> entry : query.getServices().entrySet()) {
            Service service = entry.getValue();
            this.pickups.add(service.getStartPoint());
            this.deliveries.add(service.getEndPoint());
            this.quantities.add(service.getServiceQuantity());
        }
    }

    public List<ExactSolution> solve() {
        System.out.println("Starting exact algorithm solver for query " + query.getID());
        boolean[] picked = new boolean[pickups.size()];
        boolean[] delivered = new boolean[pickups.size()];

        List<Point> route = new ArrayList<>();
        route.add(query.getDepot());

        explore(query.getDepot(), query.getQueryStartTime(), 0, 0, 0, 0, picked, delivered, route);

        if (bestSolution == null) {
            System.out.println("Exact algorithm solver finished without a feasible route for query " + query.getID());
            return Collections.emptyList();
        }
        System.out.println("Finished exact algorithm solver for query " + query.getID());
        return Collections.singletonList(bestSolution);
    }

    private void explore(Point currentPoint, double currentTime, double distance, int luCost, int load,
            int completedQuantity, boolean[] picked, boolean[] delivered, List<Point> route) {

        if (allDelivered(delivered)) {
            LegResult backLeg = shortestLeg(currentPoint.getNode().getNodeID(), query.getDepot().getNode().getNodeID(),
                    currentTime);
            if (backLeg == null) {
                return;
            }

            double arrivalAtDepot = Math.max(backLeg.arrivalTime, query.getDepot().getTimeWindow().getStartTime());
            if (arrivalAtDepot > query.getQueryEndTime()) {
                return;
            }

            List<Point> completedRoute = new ArrayList<>(route);
            completedRoute.add(query.getDepot());
            ExactSolution solution = new ExactSolution(completedRoute, completedQuantity, luCost,
                    distance + backLeg.distance);
            updateBestSolution(solution);
            return;
        }

        for (int i = 0; i < pickups.size(); i++) {
            if (!picked[i]) {
                tryMoveToPoint(i, pickups.get(i), quantities.get(i), true, currentPoint, currentTime, distance, luCost, load,
                        completedQuantity, picked, delivered, route);
            }

            if (picked[i] && !delivered[i]) {
                tryMoveToPoint(i, deliveries.get(i), quantities.get(i), false, currentPoint, currentTime, distance, luCost,
                        load, completedQuantity, picked, delivered, route);
            }
        }
    }

    private void tryMoveToPoint(int index, Point nextPoint, int quantity, boolean isPickup, Point currentPoint,
            double currentTime, double distance, int luCost, int load, int completedQuantity, boolean[] picked,
            boolean[] delivered, List<Point> route) {

        LegResult leg = shortestLeg(currentPoint.getNode().getNodeID(), nextPoint.getNode().getNodeID(), currentTime);
        if (leg == null) {
            return;
        }

        double arrivalTime = leg.arrivalTime;
        if (arrivalTime > nextPoint.getTimeWindow().getEndTime()) {
            return; // time-window violation
        }

        double serviceStart = Math.max(arrivalTime, nextPoint.getTimeWindow().getStartTime());
        if (serviceStart > query.getQueryEndTime()) {
            return;
        }

        int newLoad = isPickup ? load + quantity : load - quantity;
        if (newLoad < 0 || newLoad > query.getCapacity()) {
            return; // capacity violation
        }

        int newLuCost = luCost + quantity;
        int newCompletedQuantity = completedQuantity;
        if (isPickup) {
            // loading cost
        } else {
            // unloading cost with rearrangement penalty based on remaining load
            newLuCost += 2 * newLoad;
            newCompletedQuantity += quantity;
        }

        double newDistance = distance + leg.distance;

        if (bestSolution != null) {
            if (newCompletedQuantity < bestSolution.getNumberofProcessedRequests()) {
                // Still behind on served demand; keep exploring.
            } else if (newCompletedQuantity == bestSolution.getNumberofProcessedRequests()
                    && newDistance >= bestSolution.getDistance()) {
                return; // no chance to beat current best on distance
            }
        }

        picked[index] = picked[index] || isPickup;
        delivered[index] = delivered[index] || !isPickup;
        route.add(nextPoint);

        explore(nextPoint, serviceStart, newDistance, newLuCost, newLoad, newCompletedQuantity, picked, delivered, route);

        route.remove(route.size() - 1);
        if (isPickup) {
            picked[index] = false;
        } else {
            delivered[index] = false;
        }
    }

    private void updateBestSolution(ExactSolution candidate) {
        if (bestSolution == null) {
            bestSolution = candidate;
            return;
        }

        Comparator<ExactSolution> comparator = Comparator
                .comparingInt(ExactSolution::getNumberofProcessedRequests).reversed()
                .thenComparingDouble(ExactSolution::getDistance)
                .thenComparingInt(ExactSolution::getLUCost);

        if (comparator.compare(candidate, bestSolution) < 0) {
            bestSolution = candidate;
        }
    }

    private boolean allDelivered(boolean[] delivered) {
        for (boolean value : delivered) {
            if (!value) {
                return false;
            }
        }
        return true;
    }

    private LegResult shortestLeg(int src, int dest, double departureTime) {
        Map<Integer, Double> gCost = new HashMap<>();
        Map<Integer, Double> arrivalTime = new HashMap<>();
        Map<Integer, Double> fScore = new HashMap<>();

        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingDouble(fScore::get));

        gCost.put(src, 0.0);
        arrivalTime.put(src, departureTime);
        fScore.put(src, Graph.get_node(src).euclidean_distance(Graph.get_node(dest)));
        queue.add(src);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == dest) {
                return new LegResult(gCost.get(dest), arrivalTime.get(dest));
            }

            Node currentNode = Graph.get_node(current);
            for (Entry<Integer, Edge> entry : currentNode.get_outgoing_edges().entrySet()) {
                Edge edge = entry.getValue();
                int child = edge.get_destination();

                double tentativeDistance = gCost.get(current) + edge.getDistance();
                double tentativeArrival = edge.get_arrival_time(arrivalTime.get(current));

                boolean betterDistance = !gCost.containsKey(child) || tentativeDistance < gCost.get(child);
                if (betterDistance) {
                    gCost.put(child, tentativeDistance);
                    arrivalTime.put(child, tentativeArrival);
                    double priority = tentativeDistance + Graph.get_node(child).euclidean_distance(Graph.get_node(dest));
                    fScore.put(child, priority);
                    queue.add(child);
                }
            }
        }
        return null;
    }
}

