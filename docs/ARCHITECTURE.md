# VRPLU-OptLoad Architecture

## Overview
This document describes the architecture of the Vehicle Routing Problem with Loading and Unloading Optimization (VRPLU-OptLoad) system.

## System Architecture

### Package Structure

```
com.vrplu
├── domain/          # Domain models and entities
├── algorithm/       # Algorithm implementations
│   ├── exact/      # Exact algorithms
│   └── heuristic/  # Heuristic algorithms
├── solver/          # Problem solvers
├── util/            # Utility classes
└── io/              # Input/Output handlers
```

## Domain Layer (`com.vrplu.domain`)

Contains core business entities:
- **Node**: Represents locations (depot, customers)
- **Edge**: Represents connections between nodes
- **Graph**: Network of nodes and edges
- **Rider**: Vehicle entity
- **Query**: Customer request/order
- **RoutePlan**: Planned route for a vehicle
- **TimeWindow**: Time constraints for deliveries
- **Service**: Service time and requirements

## Algorithm Layer (`com.vrplu.algorithm`)

### Exact Algorithms (`algorithm.exact`)
- **ExactAlgorithmSolver**: Optimal solution finder
- **ExactSolution**: Representation of optimal solutions

### Heuristic Algorithms (`algorithm.heuristic`)
- **InsertionHeuristicSolver**: Insertion-based construction heuristic
- **LifoStackSolver**: LIFO-based routing
- **BazelmansBaselineSolver**: Baseline comparison algorithm
- **FoodMatchSolver**: Specialized matching algorithm

## Solver Layer (`com.vrplu.solver`)

- **Solver**: Base solver interface
- **SolverFactory**: Factory pattern for solver creation
- **SolverType**: Enumeration of available solvers

## Utility Layer (`com.vrplu.util`)

Helper classes for:
- **Function**: Mathematical functions
- **Point**: Geometric calculations
- **BreakPoint**: Piecewise function handling
- **Priority**: Priority management
- **Event**: Event handling
- **Ordering**: Sorting and ordering utilities

## I/O Layer (`com.vrplu.io`)

- **DataLoader**: Load problem instances from files
- **ResultWriter**: Write solutions to files
- **GraphGenerator**: Generate test graphs

## Design Patterns Used

1. **Factory Pattern**: SolverFactory for creating solver instances
2. **Strategy Pattern**: Different algorithm implementations
3. **Builder Pattern**: Complex object construction
4. **Singleton Pattern**: Configuration management
5. **Template Method**: Base algorithm structure

## Data Flow

```
Input Files → DataLoader → Graph Construction → 
Solver Selection → Algorithm Execution → 
Solution Generation → ResultWriter → Output Files
```

## Extensibility

To add a new algorithm:
1. Implement the `Solver` interface
2. Add to `SolverType` enumeration
3. Update `SolverFactory`
4. Add tests in `src/test/java/com/vrplu/algorithm`

## Testing Strategy

- Unit tests for individual components
- Integration tests for algorithm workflows
- Performance tests for large instances
- Regression tests for solution quality
