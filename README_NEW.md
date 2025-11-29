# VRPLU-OptLoad

Vehicle Routing Problem with Loading and Unloading Optimization

## Overview

VRPLU-OptLoad is a Java-based solver for the Vehicle Routing Problem (VRP) that considers loading and unloading constraints. The system implements multiple algorithms including exact methods and heuristics to find optimal or near-optimal solutions for complex routing scenarios.

## Features

- 🚚 **Multiple Algorithm Support**: Exact algorithms, insertion heuristics, and specialized solvers
- ⚡ **Efficient Graph Processing**: Optimized data structures for large-scale problems
- 🎯 **Time Window Constraints**: Support for delivery time windows
- 📊 **Configurable Solutions**: Flexible configuration through properties files
- 🧪 **Comprehensive Testing**: Unit and integration tests with sample datasets

## Project Structure

```
VRPLU-OptLoad/
├── src/
│   ├── main/
│   │   ├── java/com/vrplu/
│   │   │   ├── domain/          # Core business entities
│   │   │   ├── algorithm/       # Algorithm implementations
│   │   │   │   ├── exact/       # Exact algorithms
│   │   │   │   └── heuristic/   # Heuristic algorithms
│   │   │   ├── solver/          # Solver infrastructure
│   │   │   ├── util/            # Utility classes
│   │   │   └── io/              # Input/Output operations
│   │   └── resources/           # Application resources
│   └── test/                    # Unit and integration tests
├── data/
│   ├── raw/                     # Input datasets
│   ├── processed/               # Processed data
│   └── sample/                  # Sample data for testing
├── config/                      # Configuration files
├── docs/                        # Documentation
├── scripts/                     # Build and utility scripts
└── pom.xml                      # Maven configuration
```

## Quick Start

### Prerequisites

- **Java**: JDK 11 or higher
- **Maven**: 3.6 or higher
- **Git**: For version control

### Installation

1. Clone the repository:
```bash
git clone https://github.com/kousik-kr/VRPLU-OptLoad.git
cd VRPLU-OptLoad
```

2. Build the project:
```bash
mvn clean install
```

Or use the provided build script:
```bash
./scripts/build.sh
```

### Running the Application

```bash
java -jar target/vrplu-optload-1.0.0-jar-with-dependencies.jar
```

### Configuration

Edit `config/application.properties` to customize:

```properties
# Algorithm Settings
algorithm.default=insertion_heuristic
algorithm.max_iterations=1000

# Input/Output Settings
io.dataset_directory=./data/raw
io.output_directory=./data/processed
```

## Usage Examples

### Basic Usage

```java
import com.vrplu.domain.Graph;
import com.vrplu.solver.Solver;
import com.vrplu.solver.SolverFactory;
import com.vrplu.solver.SolverType;

// Load graph from files
Graph graph = DataLoader.loadGraph("data/raw/nodes.txt", "data/raw/edges.txt");

// Create solver
Solver solver = SolverFactory.createSolver(SolverType.INSERTION_HEURISTIC);

// Solve problem
RoutePlan solution = solver.solve(graph, queries);
```

### Available Algorithms

- **Exact Algorithm**: Finds optimal solutions for small to medium instances
- **Insertion Heuristic**: Fast construction heuristic for large instances
- **LIFO Stack Solver**: Stack-based routing strategy
- **Bazemans Baseline**: Baseline algorithm for comparison
- **Food Match Solver**: Specialized matching algorithm

## Development

### Project Migration

If you're migrating from the old structure, use the migration script:

```bash
./scripts/migrate-code.sh
```

This will:
1. Move Java files to new package structure
2. Add appropriate package declarations
3. Preserve original files for verification

### Running Tests

```bash
mvn test
```

### Code Style

This project follows standard Java conventions:
- 4 spaces for indentation
- Maximum line length: 120 characters
- JavaDoc for public methods and classes

See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for detailed guidelines.

## Documentation

- **[Architecture](docs/ARCHITECTURE.md)**: System design and package structure
- **[API Documentation](docs/API.md)**: API reference and usage examples
- **[Contributing Guide](docs/CONTRIBUTING.md)**: How to contribute to the project

## Dataset Format

### Nodes File
```
nodeId x y capacity
1 0.0 0.0 100
2 10.5 20.3 50
```

### Edges File
```
fromNode toNode distance travelTime
1 2 15.2 10
2 3 8.5 5
```

### Queries File
```
queryId pickupNode deliveryNode demand timeWindow
1 2 5 20 0-100
2 3 7 30 50-150
```

## Performance

The system has been tested on instances with:
- Up to 285,050 edges
- Up to 21,048 nodes
- Complex time window constraints

## Architecture Highlights

### Design Patterns
- **Factory Pattern**: Solver creation and management
- **Strategy Pattern**: Interchangeable algorithms
- **Builder Pattern**: Complex object construction
- **Template Method**: Algorithm structure

### Key Principles
- **Separation of Concerns**: Clear package boundaries
- **Single Responsibility**: Each class has one purpose
- **Open/Closed**: Easy to extend with new algorithms
- **Dependency Injection**: Flexible configuration

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](docs/CONTRIBUTING.md) for:
- Code style guidelines
- Testing requirements
- Pull request process
- Development setup

## License

[Add your license information here]

## Authors

- Kousik Kumar ([@kousik-kr](https://github.com/kousik-kr))

## Acknowledgments

- Research papers and algorithms referenced in the implementation
- Contributors and testers

## Contact

For questions or support:
- Open an issue on GitHub
- Email: [Add contact email]

## Roadmap

- [ ] Web-based visualization dashboard
- [ ] REST API for remote solving
- [ ] Additional metaheuristic algorithms
- [ ] Parallel solving capabilities
- [ ] Cloud deployment support

---

**Note**: This project has been restructured following software engineering best practices. The new structure provides better organization, testability, and maintainability.
