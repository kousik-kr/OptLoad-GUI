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
