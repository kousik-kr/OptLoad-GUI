# Dataset Configuration for VRPLU-OptLoad

## Google Drive Dataset Location

**Main Folder**: https://drive.google.com/drive/folders/1amiGMc5Uz92xeuGebwHm2Sj23w_mgN3m

## Required Files

The following files are required for the application to run:

1. **nodes_285050.txt**
   - Contains node coordinates and properties
   - Format: `node_id x_coordinate y_coordinate`
   - Size: ~11MB
   - Lines: 285,050+ nodes

2. **edges_285050.txt**
   - Contains edge connections and time-dependent costs
   - Format: First line is time series, then edge data
   - Time series: space-separated integers (e.g., `0 30 60 90`)
   - Edge format: `source_node destination_node cost1,cost2,cost3,cost4`
   - Costs are comma-separated for each time period

3. **Query_285050.txt** (optional but recommended)
   - Contains routing queries/requests
   - Located in project root directory

## Automatic Download

### Using Scripts

The project includes automatic dataset management:

```bash
# Download datasets automatically
./scripts/download-dataset.sh

# Or just run the project (downloads automatically)
./run.sh
```

### Prerequisites for Automatic Download

The download script requires one of:
- `gdown` (Python package): `pip install gdown`
- `curl` (usually pre-installed)
- `wget` (usually pre-installed)

### Installation of gdown

```bash
# Using pip
pip install gdown

# Using pip3
pip3 install gdown

# On Ubuntu/Debian with user install
pip3 install --user gdown
```

## Manual Download

If automatic download fails:

1. Visit: https://drive.google.com/drive/folders/1amiGMc5Uz92xeuGebwHm2Sj23w_mgN3m
2. Download required files:
   - `nodes_285050.txt`
   - `edges_285050.txt`
3. Place them in: `dataset/` directory in the project root

```bash
# Expected location
VRPLU-OptLoad/
└── dataset/
    ├── nodes_285050.txt
    └── edges_285050.txt
```

## Dataset Format Details

### Nodes File Format
```
node_id x_coordinate y_coordinate
0 -121.904167 41.974556
1 -121.902153 41.974766
2 -121.896790 41.988075
...
```

### Edges File Format
```
time_series (space-separated integers)
source dest cost1,cost2,cost3,cost4
source dest cost1,cost2,cost3,cost4
...
```

Example:
```
0 30 60 90
0 6 0.005952,0.005952,0.005952,0.005952
1 2 0.014350,0.014350,0.014350,0.014350
...
```

### Query File Format
```
query_id vehicle_capacity
service_id pickup_node delivery_node demand
...
```

## Troubleshooting

### Download Fails

**Issue**: `gdown` not found or download fails

**Solutions**:
1. Install gdown: `pip3 install gdown`
2. Check internet connection
3. Verify Google Drive link is accessible
4. Download manually from the link above

### File Size Issues

**Issue**: Downloaded files are very small or empty

**Solutions**:
1. Check available disk space
2. Re-run download script
3. Delete partial downloads and try again:
   ```bash
   rm dataset/nodes_285050.txt dataset/edges_285050.txt
   ./scripts/download-dataset.sh
   ```

### Permission Issues

**Issue**: Cannot write to dataset directory

**Solutions**:
```bash
# Create directory with proper permissions
mkdir -p dataset
chmod 755 dataset

# Run download script again
./scripts/download-dataset.sh
```

## Verification

After download, verify files:

```bash
# Check file sizes
ls -lh dataset/

# Expected output:
# -rw-rw-r-- 1 user user  11M Nov 29 13:00 nodes_285050.txt
# -rw-rw-r-- 1 user user 539K Nov 29 13:00 edges_285050.txt

# Count lines
wc -l dataset/nodes_285050.txt
wc -l dataset/edges_285050.txt
```

## Alternative Datasets

For testing with smaller datasets, use the California dataset:
- `data/raw/CaliforniaNodes.txt` (21,048 nodes)
- `data/raw/CaliforniaEdges.txt` (21,693 edges)

To use the California dataset:
```bash
cp data/raw/CaliforniaNodes.txt dataset/nodes_285050.txt
awk 'NR==1 {print "0 30 60 90"} NR>1 {cost=$4; print $2, $3, cost","cost","cost","cost}' \
    data/raw/CaliforniaEdges.txt > dataset/edges_285050.txt
```

## Contact

If you have issues accessing the dataset, please contact the repository maintainer.
