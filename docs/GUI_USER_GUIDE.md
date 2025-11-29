# OptLoad GUI - Complete User Guide

## Table of Contents
1. [Getting Started](#getting-started)
2. [Interface Overview](#interface-overview)
3. [Workflow Guide](#workflow-guide)
4. [Advanced Features](#advanced-features)
5. [Troubleshooting](#troubleshooting)
6. [Tips & Best Practices](#tips--best-practices)

---

## Getting Started

### System Requirements
- **Java**: Version 11 or higher
- **Maven**: Version 3.6 or higher
- **RAM**: Minimum 4GB (8GB recommended for large instances)
- **Display**: 1280x720 minimum resolution (1920x1080 recommended)

### Installation & Launch

**Option 1: Using the launcher script (Recommended)**
```bash
./launch-gui.sh
```

**Option 2: Using Maven directly**
```bash
mvn javafx:run
```

**Option 3: Building and running JAR**
```bash
mvn clean package
java -jar target/vrplu-optload-1.0.0.jar
```

### First Launch
When you first launch OptLoad GUI:
1. The application opens in **Dark Theme** by default
2. Sample graph data is displayed for demonstration
3. Working directory is set to current directory
4. All controls are in default state

---

## Interface Overview

### Layout Structure

```
┌─────────────────────────────────────────────────────────┐
│  Menu Bar | Title Bar with Theme Toggle & Actions       │
├──────────┬────────────────────────┬─────────────────────┤
│          │                        │                     │
│ Control  │  Graph Visualization   │  Results Dashboard  │
│  Panel   │      (Center)          │    (Right Panel)    │
│  (Left)  │                        │                     │
│          │                        │                     │
├──────────┴────────────────────────┴─────────────────────┤
│               Status Bar (Bottom)                       │
└─────────────────────────────────────────────────────────┘
```

### Menu Bar

**File Menu**
- `Open Directory...` - Select working directory for data files
- `Load Query File...` - Import query definition
- `Export Results...` - Save optimization results
- `Exit` - Close application

**Solver Menu**
- `Run Solver` - Start optimization
- `Stop Solver` - Cancel running optimization
- `Compare All Solvers` - Run all algorithms and compare

**View Menu**
- `Reset View` - Reset graph zoom/pan
- `Zoom In` - Increase zoom level
- `Zoom Out` - Decrease zoom level
- `Show Grid` - Toggle grid overlay

**Help Menu**
- `Documentation` - Open user guide
- `About` - Application information

### Control Panel (Left)

#### Working Directory Section
- **Directory Field**: Shows current working directory
- **Browse Button**: Open directory chooser dialog
- **Info**: Explains where files are loaded from

#### Solver Configuration Section
- **Solver Dropdown**: Select optimization algorithm
  - Default Clustering (Fast, good quality)
  - Insertion Heuristic (Fast, medium quality)
  - Bazelmans Baseline (Benchmark algorithm)
  - LIFO Stack (Specialized for loading constraints)
  - FoodMatch Solver (Food delivery optimization)
  - Exact Algorithm (Slow, optimal solution)
- **Algorithm Description**: Dynamic text explaining selected solver

#### Query Parameters Section
- **Vehicle Capacity**: Maximum load per vehicle (1-1000)
- **Max Cluster Size**: For clustering algorithms (1-10)
- **Working Hours**: Depot time window in minutes
  - Start Time: Beginning of working day (default: 540 = 9:00 AM)
  - End Time: End of working day (default: 1140 = 7:00 PM)
- **Time Conversion**: Minutes from midnight
  - 0 = 00:00 (midnight)
  - 540 = 09:00 (9 AM)
  - 720 = 12:00 (noon)
  - 1140 = 19:00 (7 PM)

#### Options Section
- **Enable Route Animation**: Animate vehicles on routes
- **Verbose Logging**: Detailed console output

#### Query Preview Section
- **Text Area**: Shows loaded query details
- **Load Query Button**: Import query file

#### Action Buttons
- **Run Solver**: Execute optimization (Green, shortcut: F5)
- **Stop**: Cancel running optimization (Red)
- **Reset**: Reset all controls to defaults

### Graph Visualization Panel (Center)

#### Toolbar
- **Reset View**: Return to default zoom/pan
- **Zoom In**: Increase magnification
- **Zoom Out**: Decrease magnification
- **Fit to View**: Auto-scale to show all nodes
- **Play**: Start route animation
- **Stop**: Stop route animation
- **Grid Checkbox**: Toggle grid visibility
- **Zoom Percentage**: Current zoom level display

#### Canvas Controls
- **Pan**: Click and drag to move view
- **Zoom**: Scroll wheel to zoom in/out
- **Hover**: Mouse over nodes for tooltips
- **Click**: Select nodes (future feature)

#### Visual Elements
- **Depot Node** (Large Red Circle)
  - Starting and ending point for all routes
  - Labeled "D"
  - Special border styling
  
- **Customer Nodes** (Cyan Circles)
  - Regular size, numbered
  - Demand shown below node (+pickup, -delivery)
  
- **Edges** (Gray Lines)
  - Show possible connections between nodes
  - Thickness indicates distance
  
- **Routes** (Colored Arrows)
  - Each route has unique color
  - Arrows show direction
  - Animated vehicles when playing

- **Grid** (Optional)
  - Helps spatial reference
  - 50-unit spacing
  - Toggle on/off

### Results Dashboard (Right)

#### Progress Section
- **Status Label**: Current operation status
- **Progress Bar**: Visual progress indicator (0-100%)

#### Metrics Grid
Five animated metric cards:
1. **Total Cost** (Red) - Overall solution cost
2. **Distance** (Cyan) - Total km traveled
3. **Time** (Blue) - Total minutes required
4. **Vehicles** (Orange) - Number of routes/vehicles
5. **Exec Time** (Teal) - Solver execution time

#### Tabbed Results

**Routes Tab**
- Table showing all routes
- Columns: Route, Path, Cost, Distance
- Export button for CSV/Excel
- Select rows to highlight on graph

**Charts Tab**
- **Route Comparison**: Bar chart of costs per route
- **Vehicle Utilization**: Pie chart of capacity usage

**Convergence Tab**
- Line chart showing solution improvement
- X-axis: Iteration number
- Y-axis: Best cost found
- Useful for understanding solver behavior

### Status Bar (Bottom)

- **Status Icon**: Visual indicator (info/success/warning/error)
- **Status Message**: Current operation or last action
- **Clock**: Real-time application clock
- **Version Info**: Application version number

---

## Workflow Guide

### Basic Workflow

#### 1. Setup
```
Open GUI → Set Directory → Load Query → Configure Solver
```

**Steps:**
1. Launch application via `./launch-gui.sh`
2. Click "File > Open Directory" or Ctrl+O
3. Navigate to your data folder
4. Click "Load Query File" or File > Load Query File
5. Select your `.txt` query file
6. Query preview updates automatically

#### 2. Configuration
```
Choose Solver → Set Parameters → Enable Options
```

**Steps:**
1. Select solver from dropdown (e.g., "Default Clustering")
2. Set vehicle capacity (e.g., 100)
3. Adjust cluster size if using clustering (e.g., 3)
4. Set working hours (e.g., 540-1140)
5. Enable animation if desired
6. Enable verbose logging for debugging

#### 3. Execution
```
Run Solver → Watch Progress → View Results
```

**Steps:**
1. Click "Run Solver" or press F5
2. Progress bar shows completion (0-100%)
3. Watch graph visualization update
4. Status bar shows current phase
5. Wait for "Optimization Complete" message

#### 4. Analysis
```
Examine Metrics → Study Routes → Analyze Charts
```

**Steps:**
1. Check metric cards for key statistics
2. Go to "Routes" tab to see detailed paths
3. Switch to "Charts" tab for visualizations
4. Study "Convergence" to see improvement
5. Click "Play" to animate route execution

#### 5. Export
```
Review Results → Export Data → Save for Reporting
```

**Steps:**
1. Click "Export Results" in Routes tab
2. Choose format (CSV recommended)
3. Select save location
4. Results saved with timestamp

### Advanced Workflow

#### Solver Comparison
```
Run Solver 1 → Note Results → Run Solver 2 → Compare
```

**Or use automated comparison:**
1. Solver > Compare All Solvers
2. Wait for all executions
3. Review comparison table
4. Export comparison data

#### Parameter Tuning
```
Run with Params A → Note Quality → Adjust → Run Again → Compare
```

**Systematic approach:**
1. Start with default parameters
2. Note solution quality (cost, time)
3. Adjust one parameter at a time
4. Run again and record results
5. Find optimal configuration

#### Large Instance Handling
```
Load Large Graph → Use Heuristic → Enable Clustering → Monitor
```

**Best practices:**
1. Use "Default Clustering" for 100+ nodes
2. Avoid "Exact Algorithm" for >30 nodes
3. Increase cluster size for very large instances
4. Disable animation for smoother performance
5. Monitor memory usage in terminal

---

## Advanced Features

### Keyboard Shortcuts

| Category | Shortcut | Action |
|----------|----------|--------|
| **File** | Ctrl+O | Open Directory |
| | Ctrl+S | Save Results |
| | Ctrl+Q | Quit Application |
| **Solver** | F5 | Run Solver |
| | Esc | Stop Solver |
| **View** | Ctrl+T | Toggle Theme |
| | F11 | Toggle Fullscreen |
| | Ctrl+0 | Reset View |
| | Ctrl++ | Zoom In |
| | Ctrl+- | Zoom Out |
| **Help** | F1 | Show Help |

### Theme Customization

**Dark Theme Features:**
- Low-light optimized
- Reduced eye strain
- Cyan accent colors
- Professional appearance

**Light Theme Features:**
- Bright environment optimized
- Clean Material Design
- Blue accent colors
- High contrast

**Toggle Methods:**
1. Click moon/sun icon in title bar
2. Press Ctrl+T
3. Settings > Appearance > Theme

### Graph Interaction

**Mouse Gestures:**
- **Single Click**: Select node (future)
- **Double Click**: Center on node
- **Click + Drag**: Pan view
- **Scroll**: Zoom in/out
- **Hover**: Show tooltip

**Zoom Levels:**
- Minimum: 10% (0.1x)
- Default: 100% (1.0x)
- Maximum: 500% (5.0x)

**Performance Tips:**
- Disable grid for large graphs
- Reduce zoom for better overview
- Use "Fit to View" after loading

### Animation Controls

**Route Animation Features:**
- Color-coded vehicles per route
- Smooth interpolation
- Looping playback
- Adjustable speed

**Controls:**
- Play button: Start animation
- Stop button: Pause/reset
- Speed controlled in code (can be customized)

**Best For:**
- Presentations
- Understanding route sequence
- Identifying conflicts
- Visual verification

---

## Troubleshooting

### Common Issues

#### "Maven not found"
**Problem:** Maven is not installed or not in PATH

**Solutions:**
1. Install Maven: `sudo apt install maven` (Linux) or download from maven.apache.org
2. Add to PATH: `export PATH=$PATH:/path/to/maven/bin`
3. Verify: `mvn -version`

#### "Java version too old"
**Problem:** Java 11+ required

**Solutions:**
1. Check version: `java -version`
2. Install Java 11+: `sudo apt install openjdk-11-jdk`
3. Set JAVA_HOME: `export JAVA_HOME=/usr/lib/jvm/java-11-openjdk`

#### "Cannot find query file"
**Problem:** Working directory doesn't contain query files

**Solutions:**
1. Verify directory contains `Query_*.txt` files
2. Check file naming convention
3. Generate query: `python scripts/query-generator.py`

#### "Solver takes too long"
**Problem:** Exact algorithm on large instance

**Solutions:**
1. Use heuristic solver instead
2. Reduce problem size
3. Increase cluster size
4. Cancel with Stop button

#### "GUI doesn't start"
**Problem:** JavaFX dependencies missing

**Solutions:**
1. Clean rebuild: `mvn clean compile`
2. Check pom.xml has JavaFX dependencies
3. Try: `mvn javafx:run`

#### "Animation stutters"
**Problem:** Performance issue with large graphs

**Solutions:**
1. Disable grid
2. Reduce zoom level
3. Stop animation during solving
4. Close other applications

### Error Messages

**"OutOfMemoryError"**
- Increase heap size: `export MAVEN_OPTS="-Xmx4g"`
- Or run with: `mvn javafx:run -DargLine="-Xmx4g"`

**"ClassNotFoundException: javafx..."**
- JavaFX not loaded properly
- Run: `mvn clean compile javafx:run`

**"Cannot write to file"**
- Check file permissions
- Ensure directory exists
- Close file if open elsewhere

---

## Tips & Best Practices

### Performance Optimization

**For Large Instances (100+ nodes):**
1. Use "Default Clustering" or "Insertion Heuristic"
2. Increase cluster size to 5-7
3. Disable route animation
4. Hide grid overlay
5. Close unnecessary applications

**For Small Instances (<30 nodes):**
1. Use "Exact Algorithm" for optimal solution
2. Enable animation for visualization
3. Use lower zoom for better view

### Solver Selection Guide

| Solver | Best For | Speed | Quality |
|--------|----------|-------|---------|
| Default Clustering | General use, large instances | Fast | Good |
| Insertion Heuristic | Quick solutions | Very Fast | Medium |
| Bazelmans Baseline | Comparison baseline | Medium | Medium |
| LIFO Stack | Loading constraints | Medium | Good |
| FoodMatch | Food delivery | Medium | Good |
| Exact Algorithm | Optimal solutions, small instances | Slow | Optimal |

### Effective Analysis

**Metric Interpretation:**
- **Total Cost**: Lower is better
- **Distance**: Check realism (too high = suboptimal)
- **Time**: Must fit within working hours
- **Vehicles**: Fewer vehicles = better utilization
- **Exec Time**: Consider for production use

**Route Quality Checks:**
1. All customers served exactly once?
2. Capacity never exceeded?
3. Time windows respected?
4. Routes start and end at depot?
5. Total cost reasonable?

### Presentation Mode

**For Demonstrations:**
1. Use fullscreen (F11)
2. Enable light theme (better for projectors)
3. Increase zoom for visibility
4. Enable route animation
5. Use "Fit to View" after loading
6. Prepare multiple solver comparisons

---

## Appendix

### File Formats

**Query File Format:**
```
D <depot_node_id>
C <capacity>
P <pickup_node_id> <demand> <ready_time> <due_time> <service_time>
D <delivery_node_id> <demand> <ready_time> <due_time> <service_time>
```

**Graph File Format:**
- Generated by `GenerateTDGraph` class
- Time-dependent edge costs
- Loaded automatically from working directory

### Color Scheme

**Dark Theme Palette:**
- Background: #1e1e1e
- Accent: #4ECDC4 (Cyan)
- Success: #5FBB5F (Green)
- Warning: #FFA07A (Orange)
- Error: #FF6B6B (Red)

**Light Theme Palette:**
- Background: #f5f5f5
- Accent: #2196F3 (Blue)
- Success: #4CAF50 (Green)
- Warning: #FF9800 (Orange)
- Error: #F44336 (Red)

### Support

For issues or questions:
1. Check this documentation
2. Review console output for errors
3. Check GitHub issues
4. Contact development team

---

**Version:** 1.0.0  
**Last Updated:** November 2025  
**Maintained By:** OptLoad Development Team
