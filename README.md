src Folder contain all the source codes

dataset Folder contain all the datasets used.

Query set file is named as Query_<dataset_size>.txt

script Folder contains the scripts to generate query set.

Run instruction: 1) give permission to the run.sh file.
                 2) replace edges_285050.txt file inside dataset folder with the file in this link (https://drive.google.com/file/d/14_XYqgmS3D4GxfZKHe_SRiPwdiwmz_t7/view?usp=sharing).
                 3) ./run.sh

Optional solver flags
---------------------
The default solver uses the clustering-based heuristic. Additional solvers can
be selected by passing flags to `VRPLoadingUnloadingMain`:

* `--exact` – run the exact VRP-LU solver.
* `--foodmatch` – run the FoodMatch-inspired greedy heuristic.
* `--lifostack` – run the new LIFO multi-stack insertion heuristic that favors
  last-in-first-out loading.
OR-Tools VRPTW baseline
-----------------------
An optional OR-Tools baseline is available for comparing against the native heuristic and exact solvers. To use it:

1. Download the OR-Tools Java release from https://developers.google.com/optimization/install/java/ and place the JAR on your classpath along with the native libraries on `LD_LIBRARY_PATH`.
2. Compile the sources with the OR-Tools JAR, for example: `javac -cp <path-to-ortools.jar> src/*.java`.
3. Run the main program with the `--ortools` flag, e.g. `java -cp <path-to-ortools.jar>:src VRPLoadingUnloadingMain <working_dir> --ortools`.

The baseline constructs pickup-and-delivery routes with time windows and capacity limits, adds a small loading/unloading cost to the objective, and reports distance plus estimated LU cost for comparison.
