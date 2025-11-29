# VRPLU-OptLoad API Documentation

## Core Interfaces

### Solver Interface

```java
public interface Solver {
    /**
     * Solve the VRP problem
     * @param graph The problem graph
     * @param queries List of customer queries
     * @return Solution route plan
     */
    RoutePlan solve(Graph graph, List<Query> queries);
    
    /**
     * Get solver name
     * @return Name of the solver
     */
    String getName();
    
    /**
     * Get solver type
     * @return Type enumeration
     */
    SolverType getType();
}
```

### Graph Interface

```java
public class Graph {
    /**
     * Add a node to the graph
     * @param node Node to add
     */
    public void addNode(Node node);
    
    /**
     * Add an edge to the graph
     * @param edge Edge to add
     */
    public void addEdge(Edge edge);
    
    /**
     * Get shortest path between two nodes
     * @param from Starting node
     * @param to Ending node
     * @return Path object
     */
    public Path getShortestPath(Node from, Node to);
    
    /**
     * Get all nodes
     * @return List of nodes
     */
    public List<Node> getNodes();
    
    /**
     * Get all edges
     * @return List of edges
     */
    public List<Edge> getEdges();
}
```

## Usage Examples

### Basic Usage

```java
// Load graph from files
Graph graph = DataLoader.loadGraph("data/raw/nodes.txt", "data/raw/edges.txt");

// Load queries
List<Query> queries = DataLoader.loadQueries("data/raw/queries.txt");

// Create solver
Solver solver = SolverFactory.createSolver(SolverType.INSERTION_HEURISTIC);

// Solve problem
RoutePlan solution = solver.solve(graph, queries);

// Write results
ResultWriter.writeRoutePlan(solution, "output.txt");
```

### Advanced Configuration

```java
// Configure solver with custom properties
Properties config = new Properties();
config.load(new FileInputStream("config/application.properties"));

// Create solver with configuration
Solver solver = SolverFactory.createSolver(
    SolverType.EXACT_ALGORITHM,
    config
);

// Set time limit
solver.setTimeLimit(3600); // 1 hour

// Solve with callbacks
RoutePlan solution = solver.solve(graph, queries, new SolverCallback() {
    @Override
    public void onProgress(double progress) {
        System.out.println("Progress: " + (progress * 100) + "%");
    }
    
    @Override
    public void onSolutionFound(RoutePlan plan) {
        System.out.println("Found solution with cost: " + plan.getTotalCost());
    }
});
```

## Configuration Properties

### Algorithm Configuration
- `algorithm.default`: Default algorithm to use
- `algorithm.max_iterations`: Maximum iterations for iterative algorithms
- `algorithm.time_limit_seconds`: Time limit in seconds

### Graph Configuration
- `graph.default_capacity`: Default vehicle capacity
- `graph.use_time_windows`: Enable/disable time window constraints

### Solver Configuration
- `solver.parallel_execution`: Enable parallel solving
- `solver.num_threads`: Number of threads for parallel execution
- `solver.log_level`: Logging level (DEBUG, INFO, WARN, ERROR)

## Error Handling

All methods may throw:
- `IllegalArgumentException`: Invalid parameters
- `GraphException`: Graph-related errors
- `SolverException`: Solver execution errors
- `IOException`: File I/O errors

Example error handling:

```java
try {
    RoutePlan solution = solver.solve(graph, queries);
} catch (SolverException e) {
    logger.error("Failed to solve problem", e);
    // Handle solver error
} catch (IllegalArgumentException e) {
    logger.error("Invalid input", e);
    // Handle validation error
}
```
