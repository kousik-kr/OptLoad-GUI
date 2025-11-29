# 🚀 OptLoad GUI - Quick Start Guide

## Launch in 3 Steps

### Step 1: Prerequisites Check
```bash
# Check Java version (needs 11+)
java -version

# Check Maven (needs 3.6+)
mvn -version
```

### Step 2: Launch GUI
```bash
# Make launcher executable (first time only)
chmod +x launch-gui.sh

# Run the GUI
./launch-gui.sh
```

### Step 3: Start Optimizing!
1. **Set Directory**: Click the folder icon or File > Open Directory
2. **Choose Solver**: Select from dropdown (try "Default Clustering")
3. **Run**: Click green "Run Solver" button or press F5
4. **View Results**: Check metrics, routes, and charts!

---

## 🎨 Features at a Glance

### Visual Excellence
- ✨ **Stunning Dark/Light Themes** - Toggle with Ctrl+T
- 🎬 **Smooth Animations** - Watch routes come to life
- 🖱️ **Interactive Graph** - Pan, zoom, and explore
- 📊 **Live Charts** - Real-time metrics and visualizations

### Powerful Optimization
- 🧮 **6 Solver Algorithms** - From fast heuristics to exact solutions
- ⚡ **Real-time Progress** - Track optimization as it runs
- 📈 **Performance Metrics** - Cost, distance, time, and more
- 🎯 **Route Animation** - See vehicles traverse optimized paths

### Professional Interface
- 🎛️ **Comprehensive Controls** - Configure every parameter
- 📊 **Results Dashboard** - Metrics, charts, and tables
- ⌨️ **Keyboard Shortcuts** - Work efficiently
- 💾 **Export Capabilities** - Save results for reporting

---

## ⌨️ Essential Shortcuts

| Action | Shortcut |
|--------|----------|
| Run Solver | `F5` |
| Toggle Theme | `Ctrl+T` |
| Open Directory | `Ctrl+O` |
| Fullscreen | `F11` |
| Quit | `Ctrl+Q` |

---

## 🎯 Common Tasks

### Run Your First Optimization
```
1. File > Open Directory → Select your data folder
2. Choose solver: "Default Clustering" (recommended)
3. Set capacity: 100 (or your vehicle capacity)
4. Press F5 or click "Run Solver"
5. Watch the magic happen! ✨
```

### Compare Solvers
```
1. Solver > Compare All Solvers
2. Wait for all algorithms to complete
3. Review comparison in results table
4. Export comparison data
```

### Visualize Results
```
1. After solving, go to Results panel (right side)
2. Click "Routes" tab to see detailed paths
3. Click "Charts" tab for visualizations
4. Click "Play" button to animate routes
```

### Export Results
```
1. Go to Routes tab in Results panel
2. Click "Export Routes" button
3. Choose save location
4. Results saved as CSV file
```

---

## 🎨 Theme Preview

### Dark Theme (Default)
Perfect for:
- Late night optimization sessions
- Reduced eye strain
- Professional presentations
- Modern aesthetic

### Light Theme
Perfect for:
- Bright environments
- Projector presentations
- Print-friendly reports
- Classic look

**Toggle anytime with the moon/sun icon or Ctrl+T**

---

## 🔧 Solver Quick Reference

| Solver | Use When | Speed | Quality |
|--------|----------|-------|---------|
| **Default Clustering** | General use, 20-500 nodes | ⚡⚡⚡ Fast | ⭐⭐⭐ Good |
| **Insertion Heuristic** | Need quick results | ⚡⚡⚡⚡ Very Fast | ⭐⭐ Medium |
| **Exact Algorithm** | <30 nodes, need optimal | ⚡ Slow | ⭐⭐⭐⭐ Optimal |
| **LIFO Stack** | Loading constraints | ⚡⚡ Medium | ⭐⭐⭐ Good |
| **FoodMatch** | Food delivery | ⚡⚡ Medium | ⭐⭐⭐ Good |
| **Bazelmans** | Comparison baseline | ⚡⚡ Medium | ⭐⭐ Medium |

---

## 📱 Interface Layout

```
┌────────────────────────────────────────────────┐
│  🚛 OptLoad - VRP Optimization Suite          │
├──────────┬──────────────────┬─────────────────┤
│          │                  │                 │
│ Controls │  Graph Canvas    │    Results      │
│  Panel   │   (Interactive)  │   Dashboard     │
│          │                  │                 │
│  • Dir   │   • Pan/Zoom     │  • Metrics      │
│  • Solver│   • Animation    │  • Charts       │
│  • Params│   • Tooltips     │  • Routes       │
│  • Run   │   • Grid         │  • Export       │
│          │                  │                 │
└──────────┴──────────────────┴─────────────────┘
│          Status: Ready • 100% • 14:23:45       │
└────────────────────────────────────────────────┘
```

---

## 🆘 Quick Troubleshooting

### GUI Won't Start
```bash
# Clean rebuild
mvn clean compile

# Run again
mvn javafx:run
```

### Solver Too Slow
- Switch to "Default Clustering" or "Insertion Heuristic"
- Increase cluster size (try 5-7 for large instances)
- Consider reducing problem size

### Can't Find Files
- Check working directory is correct
- Ensure query files named `Query_*.txt`
- Verify graph files exist
- Check file permissions

### Animation Stuttering
- Disable grid overlay
- Reduce zoom level
- Stop animation during solving
- Close other applications

---

## 📚 Learn More

- **Full User Guide**: `docs/GUI_USER_GUIDE.md`
- **GUI Features**: `GUI_README.md`
- **Project Docs**: `README.md`
- **Architecture**: `docs/ARCHITECTURE.md`

---

## 🎉 Tips for Best Experience

1. **Start with Dark Theme** - Easier on the eyes
2. **Use Fullscreen** (F11) - More screen real estate
3. **Enable Animation** - Helps understand routes
4. **Try Different Solvers** - Compare results
5. **Export Your Results** - Save for later analysis

---

## 💡 Did You Know?

- **Zoom**: Scroll wheel to zoom in/out on graph
- **Pan**: Click and drag to move view around
- **Tooltips**: Hover over nodes for details
- **Reset View**: Lost your position? Click reset button
- **Fit to View**: Auto-scale to see all nodes at once

---

## ⭐ Ready to Go!

You're all set! Launch the GUI and start optimizing your vehicle routes with style! 🚀

```bash
./launch-gui.sh
```

**Enjoy the world-class OptLoad experience!** ✨

---

*For detailed documentation, see `docs/GUI_USER_GUIDE.md`*
