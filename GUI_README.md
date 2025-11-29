# OptLoad GUI - World-Class Interface

A stunning, professional-grade JavaFX GUI for the OptLoad Vehicle Routing Problem with Loading/Unloading optimization suite.

## 🎨 Features

### Visual Design
- **Modern Dark & Light Themes** - Seamlessly toggle between beautifully crafted themes
- **Smooth Animations** - Fluid transitions and interactive feedback throughout the interface
- **Professional Typography** - Carefully chosen fonts and sizing for optimal readability
- **Responsive Layout** - Adapts beautifully to different screen sizes

### Interactive Visualization
- **Real-time Graph Rendering** - High-performance canvas-based visualization of routes and nodes
- **Pan & Zoom** - Intuitive mouse/trackpad controls for navigation
- **Animated Routes** - Watch vehicles traverse optimized paths in real-time
- **Grid Overlay** - Customizable grid for better spatial reference
- **Node Highlighting** - Hover tooltips with detailed information

### Comprehensive Controls
- **Multi-Solver Support** - Choose from 6 different optimization algorithms:
  - Default Clustering (Fast Heuristic)
  - Insertion Heuristic
  - Bazelmans Baseline
  - LIFO Stack Solver
  - FoodMatch Solver
  - Exact Algorithm (Optimal)
  
- **Configurable Parameters**:
  - Vehicle capacity settings
  - Cluster size adjustment
  - Time window configuration
  - Animation controls
  - Verbose logging options

### Results Dashboard
- **Performance Metrics** - Animated cards displaying:
  - Total cost
  - Total distance
  - Total time
  - Vehicles used
  - Execution time
  
- **Interactive Charts**:
  - Route comparison bar charts
  - Vehicle utilization pie charts
  - Convergence line graphs
  
- **Route Details** - Comprehensive table view with export capabilities
- **Progress Tracking** - Real-time progress bar with status updates

## 🚀 Running the GUI

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Quick Start

```bash
# Run with Maven
mvn clean javafx:run

# Or build and run the JAR
mvn clean package
java -jar target/vrplu-optload-1.0.0.jar
```

### Using the GUI

1. **Set Working Directory**
   - Click "File > Open Directory" or use `Ctrl+O`
   - Select the directory containing your graph and query files

2. **Configure Solver**
   - Choose solver algorithm from the dropdown
   - Adjust parameters (capacity, cluster size, time windows)
   - Enable/disable animation and logging

3. **Load Query**
   - Click "Load Query File" or use "File > Load Query File"
   - Query details will appear in the preview panel

4. **Run Optimization**
   - Click "Run Solver" or press `F5`
   - Watch real-time progress and visualization
   - View results in the dashboard

5. **Analyze Results**
   - Explore routes in the table view
   - Examine performance charts
   - Export results for reporting

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+O` | Open Directory |
| `Ctrl+T` | Toggle Theme |
| `F5` | Run Solver |
| `F11` | Toggle Fullscreen |
| `Ctrl+Q` | Exit Application |

## 🎨 Themes

### Dark Theme
- Optimized for low-light environments
- Reduced eye strain during extended use
- Professional dark color palette with cyan accents

### Light Theme
- Perfect for bright environments
- Clean and crisp appearance
- Material Design inspired color scheme

Toggle between themes using the moon/sun icon or `Ctrl+T`

## 📊 Visualization Features

### Graph Panel
- **Pan**: Click and drag to move the view
- **Zoom**: Scroll wheel to zoom in/out
- **Reset View**: Button or keyboard shortcut
- **Fit to View**: Automatically frame all nodes
- **Grid Toggle**: Show/hide background grid
- **Route Animation**: Play/pause route traversal

### Node Types
- **Depot** (Red): Starting/ending point with special styling
- **Pickup** (Cyan): Customer locations with positive demand
- **Delivery** (Orange): Customer locations with negative demand

### Route Visualization
- **Color-coded routes**: Each vehicle has a distinct color
- **Arrow indicators**: Direction of travel clearly marked
- **Animated vehicles**: Real-time position tracking
- **Route metrics**: Distance, cost, and time per route

## 🏗️ Architecture

```
src/gui/
├── OptLoadGUI.java           # Main application entry point
├── GraphVisualizationPanel.java  # Interactive graph canvas
├── ControlPanel.java         # Configuration controls
├── ResultsDashboard.java     # Results and metrics display
└── StatusBar.java            # Status and notifications

src/resources/styles/
├── dark-theme.css            # Dark theme styling
└── light-theme.css           # Light theme styling
```

## 🎯 Design Principles

1. **User-Centric**: Intuitive interface requiring minimal learning curve
2. **Performance**: Optimized rendering for large graphs (1000+ nodes)
3. **Accessibility**: Clear typography, high contrast, tooltips throughout
4. **Feedback**: Immediate visual response to all user actions
5. **Professionalism**: Enterprise-grade polish and attention to detail

## 🔧 Customization

### Adding Custom Themes
Create new CSS files in `src/resources/styles/` following the pattern of existing themes.

### Extending Visualizations
Modify `GraphVisualizationPanel.java` to add custom rendering logic.

### Adding Solvers
Update `ControlPanel.java` solver dropdown to include new algorithms.

## 📝 Technical Details

### Technologies
- **JavaFX 20** - Modern UI framework
- **ControlsFX** - Enhanced UI components
- **FontAwesome** - Icon library
- **AnimateFX** - Animation library
- **Java Canvas** - High-performance graphics rendering

### Performance Optimizations
- Canvas-based rendering for smooth 60fps
- Background thread execution for solvers
- Efficient data structures for graph manipulation
- Lazy loading of chart data

## 🤝 Contributing

When adding new features to the GUI:
1. Follow existing design patterns
2. Maintain theme compatibility (dark/light)
3. Add appropriate animations and transitions
4. Include tooltips for new controls
5. Update keyboard shortcuts if applicable

## 📄 License

Part of the OptLoad VRP optimization suite.

---

**Enjoy the world-class OptLoad GUI experience!** 🚀✨
