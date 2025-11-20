# VRP Loading/Unloading Solver

This project implements a set of solvers for the Vehicle Routing Problem with Loading and Unloading (VRP-LU). It includes heuristic, exact, and baseline algorithms for routing pickup–delivery requests with time windows and load constraints, plus helper scripts for generating synthetic query sets.

## Repository layout
- `src/` – Java sources for the VRP-LU solvers and supporting domain classes.
- `dataset/` – Node and edge lists for sample road networks. The solver expects an `edges_<N>.txt` file and a matching `nodes_<N>.txt` file where `<N>` is the vertex count.
- `Query_<N>.txt` – Example query sets for a network with `<N>` vertices (e.g., `Query_285050.txt`).
- `script/` – Python utilities for generating synthetic query files.
- `run.sh` – Convenience script to compile and run the solver.

## Prerequisites
- Java 8 or later installed and available on your `PATH`.
- (Optional) [Google OR-Tools](https://developers.google.com/optimization/install/java/) Java distribution if you want to run the OR-Tools baseline.

## Preparing datasets
1. Ensure the `dataset/` directory contains matching node and edge files for the network size you plan to run (e.g., `nodes_285050.txt` and `edges_285050.txt`).
2. Replace `dataset/edges_285050.txt` with the high-resolution file from the project’s shared link: <https://drive.google.com/file/d/14_XYqgmS3D4GxfZKHe_SRiPwdiwmz_t7/view?usp=sharing>.

## Running the solver
You can run the solver against the provided query sets or any custom query file that follows the expected format.

```bash
chmod +x run.sh
./run.sh [solver-flags]
```

The script compiles all Java sources and runs `VRPLoadingUnloadingMain`, passing the repository root as the working directory. Output files are written next to the query file, using a prefix that reflects the chosen solver (e.g., `Output_285050.txt` or `OutputExact_285050.txt`).

### CLI solver flags
By default, the clustering-based heuristic runs. You can enable alternative solvers with these flags:

- `--exact` – exact VRP-LU solver
- `--foodmatch` – FoodMatch-inspired greedy heuristic
- `--lifostack` – LIFO multi-stack insertion heuristic
- `--insertion` – greedy insertion heuristic
- `--bazelmans` – Bazelmans et al. pickup–delivery baseline with non-crossing loading
- `--ortools` – OR-Tools VRPTW baseline (requires OR-Tools JAR and native libraries)

Example: `./run.sh --lifostack`.

### Custom queries
Place your query file in the repository root and name it `Query_<vertex_count>.txt`, where `<vertex_count>` matches the network size in `dataset/`. Each query block uses this format:

```
D <depot_node_id>
C <vehicle_capacity>
S <pickup_node>,<dropoff_node> <pickup_start>,<pickup_end> <dropoff_start>,<dropoff_end> <amount>
...
```

- Times are expressed in minutes; the solver assumes a working window of 540–1140 minutes for the depot.
- Multiple query blocks can be separated by blank lines.

### Output
For each query, the solver writes one or more routes as lines like:

```
[S1:123,S1:456,Depot:789]\tNumber of Successful Requests:<count>\tL-U Cost:<cost>\tDistance:<distance>
```

A runtime summary (in seconds) is appended after each batch.

## Generating synthetic queries
The `script/` directory contains Python helpers such as `query-generator.py` for creating random query sets that satisfy basic time-window constraints. Run one of these scripts to produce a `generated_queries.txt` file you can rename to the expected `Query_<N>.txt` pattern.

```bash
python script/query-generator.py
```

## Notes on the OR-Tools baseline
To run with `--ortools`, include the OR-Tools JAR on the classpath and set `LD_LIBRARY_PATH` for the native libraries, for example:

```bash
javac -cp <path-to-ortools.jar> src/*.java
java -cp <path-to-ortools.jar>:src VRPLoadingUnloadingMain $(pwd) --ortools
```

This baseline constructs VRPTW routes with capacity limits and adds a small loading/unloading cost to the objective for comparison.
