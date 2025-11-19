import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.FirstSolutionStrategy.Value;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;
import com.google.ortools.constraintsolver.LongLongToLongFunction;
import com.google.protobuf.Duration;

/**
 * Baseline solver that leverages Google's OR-Tools VRP with time windows,
 * capacity, and pickup-delivery pairing. The solver minimizes travel cost while
 * applying a small service-time offset to approximate loading/unloading effort.
 */
public class OrToolsVRPTWBaseline {

    private final Query query;

    // Simple loading/unloading knobs that lightly influence the objective and
    // are also reported as a post-processing cost.
    private static final long SERVICE_TIME_PADDING = 3; // minutes added to travel between non-depot stops
    private static final int LOADING_COST_PER_UNIT = 2; // unit cost per quantity served
    private static final int STOP_FEE = 1; // flat fee per visited pickup or drop-off
    private static final long UNASSIGNED_PENALTY = 50_000L;
    private static final int DEFAULT_VEHICLE_LIMIT = 4;

    public OrToolsVRPTWBaseline(Query query) {
        this.query = query;
    }

    /**
     * Build and solve the VRPTW using OR-Tools. Each service is modelled as a
     * pickup-and-delivery pair with capacity consumption and time windows.
     */
    public List<RoutePlan> solve() {
        Loader.loadNativeLibraries();

        List<Point> indexedPoints = buildPointIndex();
        double[][] distanceMatrix = buildDistanceMatrix(indexedPoints);
        long[][] travelTimes = buildTravelTimeMatrix(distanceMatrix, indexedPoints);
        long[][] timeWindows = buildTimeWindows(indexedPoints);
        int[] demands = buildDemands(indexedPoints);
        int vehicleCount = Math.max(1, Math.min(DEFAULT_VEHICLE_LIMIT, query.getNumberofRequests()));
        int depotIndex = 0;

        RoutingIndexManager manager = new RoutingIndexManager(indexedPoints.size(), vehicleCount, depotIndex);
        RoutingModel routing = new RoutingModel(manager);

        final LongLongToLongFunction transitCallback = (long fromIndex, long toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            long travel = travelTimes[fromNode][toNode];
            // Add a small per-stop padding for non-depot hops to reflect loading/unloading.
            if (fromNode != depotIndex && toNode != depotIndex) {
                travel += SERVICE_TIME_PADDING;
            }
            return travel;
        };
        int transitCallbackIndex = routing.registerTransitCallback(transitCallback);
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            return demands[fromNode];
        });
        routing.addDimensionWithVehicleCapacity(
                demandCallbackIndex,
                0,
                buildVehicleCapacities(vehicleCount),
                true,
                "Capacity");

        routing.addDimension(
                transitCallbackIndex,
                (int) query.getQueryEndTime(),
                (int) query.getQueryEndTime(),
                false,
                "Time");
        RoutingDimension timeDimension = routing.getMutableDimension("Time");

        for (int i = 0; i < indexedPoints.size(); i++) {
            long index = manager.nodeToIndex(i);
            timeDimension.cumulVar(index).setRange(timeWindows[i][0], timeWindows[i][1]);
        }

        attachPickupDeliveryConstraints(query, manager, routing, timeDimension);

        RoutingSearchParameters searchParameters = RoutingSearchParameters.newBuilder()
                .mergeFrom(com.google.ortools.constraintsolver.Main.defaultRoutingSearchParameters())
                .setFirstSolutionStrategy(Value.PARALLEL_CHEAPEST_INSERTION)
                .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                .setTimeLimit(Duration.newBuilder().setSeconds(10).build())
                .build();

        Assignment solution = routing.solveWithParameters(searchParameters);
        if (solution == null) {
            throw new IllegalStateException("OR-Tools failed to find a feasible assignment for query " + query.getID());
        }

        return extractPlans(indexedPoints, distanceMatrix, manager, routing, solution);
    }

    private List<Point> buildPointIndex() {
        List<Point> orderedPoints = new ArrayList<>();
        orderedPoints.add(query.getDepot());
        for (Service service : query.getServices().values()) {
            orderedPoints.add(service.getStartPoint());
            orderedPoints.add(service.getEndPoint());
        }
        return orderedPoints;
    }

    private double[][] buildDistanceMatrix(List<Point> points) {
        int size = points.size();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    matrix[i][j] = points.get(i).getNode().euclidean_distance(points.get(j).getNode());
                }
            }
        }
        return matrix;
    }

    private long[][] buildTravelTimeMatrix(double[][] distances, List<Point> points) {
        int size = distances.length;
        long[][] times = new long[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Assume average speed = 1 distance unit per minute; adjust as needed.
                double serviceDelay = (points.get(j).getType().equals("Depot")) ? 0 : 0;
                times[i][j] = Math.round(distances[i][j] + serviceDelay);
            }
        }
        return times;
    }

    private long[][] buildTimeWindows(List<Point> points) {
        long[][] windows = new long[points.size()][2];
        for (int i = 0; i < points.size(); i++) {
            TimeWindow tw = points.get(i).getTimeWindow();
            windows[i][0] = Math.round(tw.getStartTime());
            windows[i][1] = Math.round(tw.getEndTime());
        }
        return windows;
    }

    private int[] buildDemands(List<Point> points) {
        int[] demands = new int[points.size()];
        demands[0] = 0; // depot
        int index = 1;
        for (Service service : query.getServices().values()) {
            int quantity = service.getServiceQuantity();
            demands[index++] = quantity; // pickup adds load
            demands[index++] = -quantity; // delivery releases load
        }
        return demands;
    }

    private long[] buildVehicleCapacities(int vehicleCount) {
        long[] capacities = new long[vehicleCount];
        for (int i = 0; i < vehicleCount; i++) {
            capacities[i] = query.getCapacity();
        }
        return capacities;
    }

    private void attachPickupDeliveryConstraints(Query query, RoutingIndexManager manager, RoutingModel routing,
            RoutingDimension timeDimension) {
        int serviceIdx = 0;
        for (Service service : query.getServices().values()) {
            int pickupNode = 1 + serviceIdx * 2;
            int deliveryNode = pickupNode + 1;
            long pickupIndex = manager.nodeToIndex(pickupNode);
            long deliveryIndex = manager.nodeToIndex(deliveryNode);

            routing.addPickupAndDelivery(pickupIndex, deliveryIndex);
            routing.solver()
                    .add(routing.solver().makeLessOrEqual(timeDimension.cumulVar(pickupIndex),
                            timeDimension.cumulVar(deliveryIndex)));

            routing.solver()
                    .add(routing.vehicleVar(pickupIndex).eq(routing.vehicleVar(deliveryIndex)));

            routing.addDisjunction(new long[] { pickupIndex, deliveryIndex }, UNASSIGNED_PENALTY);
            serviceIdx++;
        }
    }

    private List<RoutePlan> extractPlans(List<Point> points, double[][] distances, RoutingIndexManager manager,
            RoutingModel routing, Assignment solution) {
        List<RoutePlan> plans = new ArrayList<>();
        for (int vehicleId = 0; vehicleId < manager.getNumberOfVehicles(); vehicleId++) {
            List<Point> route = new ArrayList<>();
            Set<Integer> completedRequests = new HashSet<>();
            double traveledDistance = 0;

            long index = routing.start(vehicleId);
            long previousIndex = -1;
            while (!routing.isEnd(index)) {
                int node = manager.indexToNode(index);
                route.add(points.get(node));
                previousIndex = index;
                index = solution.value(routing.nextVar(index));
                if (!routing.isEnd(index)) {
                    int nextNode = manager.indexToNode(index);
                    traveledDistance += distances[node][nextNode];
                    if (points.get(node).getType().equals("Destination")) {
                        completedRequests.add(points.get(node).getID());
                    }
                }
            }

            // Close the route at the depot if the vehicle actually visited stops.
            if (route.size() > 1 && previousIndex != -1) {
                int lastNode = manager.indexToNode(previousIndex);
                traveledDistance += distances[lastNode][0];
                route.add(points.get(0));
                int luCost = computeLoadingCost(route, completedRequests);
                plans.add(new OrToolsRoutePlan(route, completedRequests.size(), luCost, traveledDistance));
            }
        }
        return plans;
    }

    private int computeLoadingCost(List<Point> route, Set<Integer> completedRequests) {
        int totalQuantity = 0;
        int stopCount = 0;
        Map<Integer, Integer> quantities = new HashMap<>();
        for (Point point : route) {
            if (point.getType().equals("Depot")) {
                continue;
            }
            stopCount++;
            Service svc = point.getServiceObject();
            if (svc != null) {
                quantities.put(svc.getStartPoint().getID(), svc.getServiceQuantity());
            }
        }
        for (Integer quantity : quantities.values()) {
            totalQuantity += quantity;
        }
        return totalQuantity * LOADING_COST_PER_UNIT + stopCount * STOP_FEE + completedRequests.size();
    }

    private static class OrToolsRoutePlan implements RoutePlan {
        private final List<Point> order;
        private final int processedRequests;
        private final int luCost;
        private final double distance;

        OrToolsRoutePlan(List<Point> order, int processedRequests, int luCost, double distance) {
            this.order = order;
            this.processedRequests = processedRequests;
            this.luCost = luCost;
            this.distance = distance;
        }

        @Override
        public List<Point> getOrder() {
            return order;
        }

        @Override
        public int getNumberofProcessedRequests() {
            return processedRequests;
        }

        @Override
        public int getLUCost() {
            return luCost;
        }

        @Override
        public double getDistance() {
            return distance;
        }
    }
}
