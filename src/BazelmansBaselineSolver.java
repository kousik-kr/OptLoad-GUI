import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * Baseline algorithm inspired by Bazelmans et al. (2025) that builds a
 * precedence-feasible pickup-and-delivery tour while explicitly checking
 * loading constraints. The implementation follows the paper's emphasis on
 * coupled pickup/delivery handling with non-crossing loading sequences (i.e.,
 * every delivery can be reached without rehandling earlier freight).
 */
public class BazelmansBaselineSolver {

        private static final double LOADING_WEIGHT = 0.1;

        private static class LegResult {
                final double distance;
                final double arrivalTime;

                LegResult(double distance, double arrivalTime) {
                        this.distance = distance;
                        this.arrivalTime = arrivalTime;
                }
        }

        private static class RouteEvaluation {
                final List<Point> route;
                final double distance;
                final int luCost;
                final int processedRequests;
                final boolean feasible;

                RouteEvaluation(List<Point> route, double distance, int luCost, int processedRequests, boolean feasible) {
                        this.route = route;
                        this.distance = distance;
                        this.luCost = luCost;
                        this.processedRequests = processedRequests;
                        this.feasible = feasible;
                }

                double score() {
                        return distance + LOADING_WEIGHT * luCost;
                }
        }

        private final Query query;

        public BazelmansBaselineSolver(Query query) {
                this.query = query;
        }

        public List<RoutePlan> solve() {
                List<Point> currentRoute = new ArrayList<Point>();
                currentRoute.add(query.getDepot());
                currentRoute.add(query.getDepot());

                RouteEvaluation currentEval = evaluateRoute(currentRoute);

                List<Service> pendingRequests = new ArrayList<Service>(query.getServices().values());
                pendingRequests.sort(Comparator.comparingDouble(
                                service -> service.getStartPoint().getTimeWindow().getStartTime()));

                while (!pendingRequests.isEmpty()) {
                        Service chosen = null;
                        RouteEvaluation bestEval = null;

                        for (Service request : pendingRequests) {
                                RouteEvaluation candidate = findBestInsertion(currentRoute, currentEval, request);
                                if (candidate == null) {
                                        continue;
                                }

                                if (bestEval == null || candidate.score() < bestEval.score()
                                                || (Math.abs(candidate.score() - bestEval.score()) < 1e-9
                                                                && candidate.distance < bestEval.distance)) {
                                        chosen = request;
                                        bestEval = candidate;
                                }
                        }

                        if (bestEval == null) {
                                break;
                        }

                        currentRoute = bestEval.route;
                        currentEval = bestEval;
                        pendingRequests.remove(chosen);
                }

                List<RoutePlan> result = new ArrayList<RoutePlan>();
                result.add(new ExactSolution(currentRoute, currentEval.processedRequests, currentEval.luCost,
                                currentEval.distance));
                return result;
        }

        private RouteEvaluation findBestInsertion(List<Point> currentRoute, RouteEvaluation currentEval, Service request) {
                double bestScore = Double.POSITIVE_INFINITY;
                RouteEvaluation bestEvaluation = null;

                int originalSize = currentRoute.size();
                Point pickup = request.getStartPoint();
                Point dropoff = request.getEndPoint();

                for (int pickupIndex = 1; pickupIndex < originalSize; pickupIndex++) {
                        for (int dropIndex = pickupIndex; dropIndex < originalSize; dropIndex++) {
                                List<Point> candidate = new ArrayList<Point>(currentRoute);
                                candidate.add(pickupIndex, pickup);
                                candidate.add(dropIndex + 1, dropoff);

                                RouteEvaluation evaluation = evaluateRoute(candidate);
                                if (!evaluation.feasible) {
                                        continue;
                                }

                                double score = evaluation.score();

                                if (score < bestScore || (Math.abs(score - bestScore) < 1e-9
                                                && (bestEvaluation == null || evaluation.distance < bestEvaluation.distance))) {
                                        bestScore = score;
                                        bestEvaluation = evaluation;
                                }
                        }
                }

                return bestEvaluation;
        }

        private RouteEvaluation evaluateRoute(List<Point> sequence) {
                double currentTime = query.getQueryStartTime();
                double totalDistance = 0.0;
                int luCost = 0;
                int processedRequests = 0;
                int currentLoad = 0;
                Map<Integer, Boolean> pickedUp = new HashMap<Integer, Boolean>();

                for (int i = 0; i < sequence.size(); i++) {
                                Point point = sequence.get(i);
                                if (i > 0) {
                                        Point previous = sequence.get(i - 1);
                                        LegResult leg = shortestLeg(previous.getNode().getNodeID(), point.getNode().getNodeID(),
                                                        currentTime);
                                        if (leg == null) {
                                                return new RouteEvaluation(sequence, Double.POSITIVE_INFINITY, luCost,
                                                                processedRequests, false);
                                        }

                                        totalDistance += leg.distance;
                                        currentTime = leg.arrivalTime;
                                }

                                currentTime = Math.max(currentTime, point.getTimeWindow().getStartTime());
                                if (currentTime > point.getTimeWindow().getEndTime() || currentTime > query.getQueryEndTime()) {
                                        return new RouteEvaluation(sequence, Double.POSITIVE_INFINITY, luCost, processedRequests,
                                                        false);
                                }

                                if (point.getType().equals("Source")) {
                                        int quantity = point.getServiceObject().getServiceQuantity();
                                        currentLoad += quantity;
                                        if (currentLoad > query.getCapacity()) {
                                                return new RouteEvaluation(sequence, Double.POSITIVE_INFINITY, luCost,
                                                                processedRequests, false);
                                        }
                                        luCost += quantity;
                                        processedRequests += quantity;
                                        pickedUp.put(point.getID(), true);
                                } else if (point.getType().equals("Destination")) {
                                        if (!pickedUp.containsKey(point.getID())) {
                                                return new RouteEvaluation(sequence, Double.POSITIVE_INFINITY, luCost,
                                                                processedRequests, false);
                                        }
                                        int quantity = point.getServiceObject().getServiceQuantity();
                                        currentLoad -= quantity;
                                        if (currentLoad < 0) {
                                                return new RouteEvaluation(sequence, Double.POSITIVE_INFINITY, luCost,
                                                                processedRequests, false);
                                        }
                                        luCost += quantity;
                                        luCost += 2 * currentLoad;
                                        pickedUp.remove(point.getID());
                                }
                        }

                if (!loadingPlanExists(sequence)) {
                        return new RouteEvaluation(sequence, Double.POSITIVE_INFINITY, luCost, processedRequests, false);
                }

                return new RouteEvaluation(sequence, totalDistance, luCost, processedRequests, true);
        }

        private boolean loadingPlanExists(List<Point> sequence) {
                Map<Integer, Integer> pickupPosition = new HashMap<Integer, Integer>();
                Map<Integer, Integer> deliveryPosition = new HashMap<Integer, Integer>();

                for (int i = 0; i < sequence.size(); i++) {
                        Point point = sequence.get(i);
                        if (point.getType().equals("Source")) {
                                pickupPosition.put(point.getID(), i);
                        } else if (point.getType().equals("Destination")) {
                                deliveryPosition.put(point.getID(), i);
                        }
                }

                for (Entry<Integer, Integer> entry : pickupPosition.entrySet()) {
                        int id = entry.getKey();
                        if (!deliveryPosition.containsKey(id) || entry.getValue() >= deliveryPosition.get(id)) {
                                return false;
                        }
                }

                List<Integer> ids = new ArrayList<Integer>(pickupPosition.keySet());
                for (int i = 0; i < ids.size(); i++) {
                        for (int j = i + 1; j < ids.size(); j++) {
                                int first = ids.get(i);
                                int second = ids.get(j);
                                int firstPickup = pickupPosition.get(first);
                                int secondPickup = pickupPosition.get(second);
                                int firstDrop = deliveryPosition.get(first);
                                int secondDrop = deliveryPosition.get(second);

                                if (firstPickup < secondPickup && firstDrop > secondDrop) {
                                        return false;
                                }
                                if (secondPickup < firstPickup && secondDrop > firstDrop) {
                                        return false;
                                }
                        }
                }

                return true;
        }

        private LegResult shortestLeg(int src, int dest, double departureTime) {
                Map<Integer, Double> gCost = new HashMap<Integer, Double>();
                Map<Integer, Double> arrivalTime = new HashMap<Integer, Double>();
                Map<Integer, Double> fScore = new HashMap<Integer, Double>();

                PriorityQueue<Integer> queue = new PriorityQueue<Integer>(1, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer i, Integer j) {
                                if (fScore.get(i) > fScore.get(j)) {
                                        return 1;
                                } else if (fScore.get(i) < fScore.get(j)) {
                                        return -1;
                                } else {
                                        return 0;
                                }
                        }
                });

                gCost.put(src, 0.0);
                arrivalTime.put(src, departureTime);
                fScore.put(src, Graph.get_node(src).euclidean_distance(Graph.get_node(dest)));
                queue.add(src);

                while (!queue.isEmpty()) {
                        int current = queue.poll();
                        if (current == dest) {
                                return new LegResult(gCost.get(dest), arrivalTime.get(dest));
                        }

                        Node node = Graph.get_node(current);
                        for (Entry<Integer, Edge> edgeEntry : node.get_outgoing_edges().entrySet()) {
                                Edge edge = edgeEntry.getValue();
                                int child = edge.get_destination();

                                double tentativeArrival = edge.get_arrival_time(arrivalTime.get(current));
                                double tentativeDistance = gCost.get(current) + edge.getDistance();

                                if (!gCost.containsKey(child) || tentativeDistance < gCost.get(child)) {
                                        gCost.put(child, tentativeDistance);
                                        arrivalTime.put(child, tentativeArrival);
                                        fScore.put(child,
                                                        tentativeDistance + Graph.get_node(child)
                                                                        .euclidean_distance(Graph.get_node(dest)));
                                        queue.add(child);
                                }
                        }
                }

                return null;
        }
}
