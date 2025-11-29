# Getting Started with Restructured VRPLU-OptLoad

## What Changed?

Your project has been restructured following Java/Maven best practices:

### Before:
```
VRPLU-OptLoad/
├── src/              # All Java files in one directory
├── dataset/          # Data files
└── script/           # Python scripts
```

### After:
```
VRPLU-OptLoad/
├── src/
│   ├── main/java/com/vrplu/
│   │   ├── domain/       # Core entities (Node, Edge, Graph)
│   │   ├── algorithm/    # Algorithms organized by type
│   │   ├── solver/       # Solver infrastructure
│   │   ├── util/         # Utilities
│   │   └── io/           # I/O operations
│   └── test/             # Unit tests
├── data/                 # Organized data files
├── config/               # Configuration files
├── docs/                 # Documentation
├── scripts/              # Utility scripts
└── pom.xml              # Maven build config
```

## Quick Start Guide

### Step 1: Migrate Your Code

Run the migration script to move existing Java files to the new structure:

```bash
./scripts/migrate-code.sh
```

This will:
- Copy Java files to appropriate packages
- Add package declarations
- Preserve original files for safety

### Step 2: Build the Project

```bash
./scripts/build.sh
```

Or manually:
```bash
mvn clean install
```

### Step 3: Fix Import Statements

After migration, you'll need to update import statements in your Java files:

**Old:**
```java
import Node;
import Edge;
```

**New:**
```java
import com.vrplu.domain.Node;
import com.vrplu.domain.Edge;
import com.vrplu.solver.Solver;
import com.vrplu.algorithm.heuristic.InsertionHeuristicSolver;
```

### Step 4: Verify Everything Works

```bash
mvn compile  # Check compilation
mvn test     # Run tests
mvn package  # Create JAR file
```

## Key Files to Know

### Build & Configuration
- **pom.xml**: Maven configuration (dependencies, build settings)
- **config/application.properties**: Runtime configuration
- **config/logback.xml**: Logging configuration
- **.gitignore**: Files to exclude from Git

### Documentation
- **docs/ARCHITECTURE.md**: System architecture overview
- **docs/API.md**: API documentation and examples
- **docs/CONTRIBUTING.md**: How to contribute
- **README_NEW.md**: Updated project README
- **RESTRUCTURE_SUMMARY.md**: Complete restructuring details

### Scripts
- **scripts/migrate-code.sh**: Migrate existing code
- **scripts/build.sh**: Build the project
- **scripts/show-structure.sh**: Display project structure

## Package Guidelines

### Where to Put New Code?

| Type of Code | Package |
|-------------|---------|
| Data models, entities | `com.vrplu.domain` |
| Exact algorithms | `com.vrplu.algorithm.exact` |
| Heuristic algorithms | `com.vrplu.algorithm.heuristic` |
| Solver framework | `com.vrplu.solver` |
| Helper/utility classes | `com.vrplu.util` |
| File I/O, data loading | `com.vrplu.io` |
| Main application | `com.vrplu` |

### Test Files

Place test files in `src/test/java/com/vrplu/{package}/` matching the package structure.

Example:
- Source: `src/main/java/com/vrplu/domain/Node.java`
- Test: `src/test/java/com/vrplu/domain/NodeTest.java`

## Common Tasks

### Add a New Algorithm

1. Create class in `src/main/java/com/vrplu/algorithm/{type}/YourAlgorithm.java`
2. Implement the `Solver` interface
3. Add to `SolverType` enum
4. Update `SolverFactory`
5. Create test in `src/test/java/com/vrplu/algorithm/YourAlgorithmTest.java`

### Add a Dependency

Edit `pom.xml` and add dependency in `<dependencies>` section:

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>example-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

Then run: `mvn clean install`

### Run the Application

After building:
```bash
java -jar target/vrplu-optload-1.0.0-jar-with-dependencies.jar
```

### View Logs

Logs are written to:
- Console (stdout)
- `logs/vrplu-optload.log`

Configure in `config/logback.xml`

## IDE Setup

### IntelliJ IDEA
1. Open project: File → Open → select VRPLU-OptLoad folder
2. IDEA will auto-detect Maven and import dependencies
3. Wait for indexing to complete
4. Right-click `pom.xml` → Maven → Reload Project

### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Select VRPLU-OptLoad folder
3. Finish

### VS Code
1. Open folder in VS Code
2. Install Java Extension Pack
3. Maven extension will auto-configure

## Troubleshooting

### "Package does not exist" errors
- Ensure package declarations match directory structure
- Update all import statements
- Run `mvn clean compile`

### "Cannot find symbol" errors
- Check that all dependencies are in `pom.xml`
- Run `mvn clean install -U` to update dependencies

### Tests failing
- Ensure test files are in `src/test/java/`
- Check test dependencies in `pom.xml`
- Run `mvn clean test` to rebuild and test

## Next Steps

1. ✅ **Migration**: Run `./scripts/migrate-code.sh`
2. ✅ **Build**: Run `./scripts/build.sh`
3. 📝 **Review**: Check migrated files for import errors
4. 🧪 **Test**: Write tests for your components
5. 📚 **Document**: Add JavaDoc comments to public APIs
6. 🚀 **Deploy**: Package and distribute your application

## Need Help?

- Read [ARCHITECTURE.md](docs/ARCHITECTURE.md) for design details
- Check [API.md](docs/API.md) for usage examples
- See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for development guidelines
- Review [RESTRUCTURE_SUMMARY.md](RESTRUCTURE_SUMMARY.md) for complete changes

## Benefits You Get

✅ **Better Organization**: Clear separation of concerns  
✅ **Standard Structure**: Familiar to Java developers  
✅ **Easy Testing**: Proper test infrastructure  
✅ **Build Automation**: Maven handles everything  
✅ **Dependency Management**: Easy to add libraries  
✅ **IDE Support**: Works with all major IDEs  
✅ **Scalability**: Easy to grow the project  
✅ **Documentation**: Comprehensive guides included  

---

**Welcome to your restructured project!** 🎉

The new structure follows industry best practices and will make your code easier to maintain, test, and extend.
