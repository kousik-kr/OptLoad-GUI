# Contributing to VRPLU-OptLoad

## Getting Started

### Prerequisites
- JDK 11 or higher
- Maven 3.6+
- Git

### Setup Development Environment

1. Clone the repository
```bash
git clone https://github.com/kousik-kr/VRPLU-OptLoad.git
cd VRPLU-OptLoad
```

2. Build the project
```bash
mvn clean install
```

3. Run tests
```bash
mvn test
```

## Code Style Guidelines

### Java Conventions
- Follow standard Java naming conventions
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Always use braces for control structures
- Add JavaDoc for public methods and classes

### Example:

```java
/**
 * Calculates the shortest path between two nodes.
 * 
 * @param from the starting node
 * @param to the destination node
 * @return the shortest path, or null if no path exists
 * @throws IllegalArgumentException if nodes are null
 */
public Path calculateShortestPath(Node from, Node to) {
    if (from == null || to == null) {
        throw new IllegalArgumentException("Nodes cannot be null");
    }
    // Implementation
}
```

## Project Structure

Follow the established package structure:
- `domain/` - Domain models only
- `algorithm/` - Algorithm implementations
- `solver/` - Solver orchestration
- `util/` - Utility classes
- `io/` - Input/output operations

## Adding New Features

### Adding a New Algorithm

1. Create the algorithm class in the appropriate package:
   - Exact algorithms: `com.vrplu.algorithm.exact`
   - Heuristics: `com.vrplu.algorithm.heuristic`

2. Implement the `Solver` interface

3. Add the algorithm to `SolverType` enum

4. Update `SolverFactory` to support the new algorithm

5. Write unit tests in `src/test/java/com/vrplu/algorithm`

6. Update documentation in `docs/API.md`

### Testing Requirements

- All new code must have unit tests
- Test coverage should be > 80%
- Include edge cases and error scenarios
- Use JUnit 5 for testing

Example test:

```java
@Test
void testShortestPath() {
    Graph graph = new Graph();
    Node n1 = new Node(1, 0, 0);
    Node n2 = new Node(2, 10, 10);
    graph.addNode(n1);
    graph.addNode(n2);
    
    Path path = graph.getShortestPath(n1, n2);
    assertNotNull(path);
    assertEquals(14.14, path.getDistance(), 0.01);
}
```

## Pull Request Process

1. Create a feature branch from `main`
```bash
git checkout -b feature/your-feature-name
```

2. Make your changes following the code style guidelines

3. Add/update tests

4. Ensure all tests pass
```bash
mvn clean test
```

5. Update documentation if needed

6. Commit with clear, descriptive messages
```bash
git commit -m "Add insertion heuristic algorithm"
```

7. Push to your fork and create a pull request

8. Address review feedback

## Commit Message Guidelines

- Use present tense ("Add feature" not "Added feature")
- Use imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit first line to 72 characters
- Reference issues and pull requests when applicable

Examples:
```
Add distance calculation utility
Fix bug in time window validation
Refactor solver factory pattern
Update API documentation for Graph class
```

## Questions?

If you have questions, please:
1. Check existing documentation
2. Search existing issues
3. Open a new issue with the `question` label
