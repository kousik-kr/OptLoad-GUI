package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.ArcType;
import javafx.animation.*;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.util.*;

/**
 * Interactive graph visualization panel with advanced features:
 * - Pan and zoom with mouse/trackpad
 * - Real-time route animation
 * - Node highlighting and tooltips
 * - Grid overlay with customizable spacing
 * - Performance optimized rendering
 */
public class GraphVisualizationPanel extends BorderPane {
    
    private static final double CANVAS_WIDTH = 1000;
    private static final double CANVAS_HEIGHT = 800;
    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 5.0;
    
    private Canvas canvas;
    private GraphicsContext gc;
    
    // View transformation
    private double zoom = 1.0;
    private double translateX = 0;
    private double translateY = 0;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isPanning = false;
    
    // Grid settings
    private boolean gridVisible = true;
    private double gridSpacing = 50;
    
    // Graph data
    private List<GraphNode> nodes = new ArrayList<>();
    private List<GraphEdge> edges = new ArrayList<>();
    private List<Route> routes = new ArrayList<>();
    private GraphNode depotNode;
    
    // Animation
    private AnimationTimer animationTimer;
    private double animationProgress = 0;
    private boolean isAnimating = false;
    
    // Toolbar
    private ToolBar toolbar;
    
    public GraphVisualizationPanel() {
        getStyleClass().add("graph-panel");
        
        // Initialize canvas
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        // Canvas container with scroll pane
        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.getStyleClass().add("canvas-container");
        canvasContainer.setPadding(new Insets(10));
        
        ScrollPane scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setPannable(false);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        // Setup mouse interactions
        setupMouseHandlers();
        
        // Create toolbar
        toolbar = createToolbar();
        
        setTop(toolbar);
        setCenter(scrollPane);
        
        // Initial render
        render();
        
        // Generate sample data
        generateSampleGraph();
    }
    
    private ToolBar createToolbar() {
        ToolBar tb = new ToolBar();
        tb.getStyleClass().add("graph-toolbar");
        
        Button resetBtn = createToolbarButton(FontAwesomeIcon.CROSSHAIRS, "Reset View");
        resetBtn.setOnAction(e -> resetView());
        
        Button zoomInBtn = createToolbarButton(FontAwesomeIcon.SEARCH_PLUS, "Zoom In");
        zoomInBtn.setOnAction(e -> zoomIn());
        
        Button zoomOutBtn = createToolbarButton(FontAwesomeIcon.SEARCH_MINUS, "Zoom Out");
        zoomOutBtn.setOnAction(e -> zoomOut());
        
        Button fitBtn = createToolbarButton(FontAwesomeIcon.EXPAND, "Fit to View");
        fitBtn.setOnAction(e -> fitToView());
        
        Separator sep1 = new Separator();
        
        Button playBtn = createToolbarButton(FontAwesomeIcon.PLAY, "Animate Routes");
        playBtn.setOnAction(e -> animateRoutes());
        
        Button stopBtn = createToolbarButton(FontAwesomeIcon.STOP, "Stop Animation");
        stopBtn.setOnAction(e -> stopAnimation());
        
        Separator sep2 = new Separator();
        
        CheckBox gridCheck = new CheckBox("Grid");
        gridCheck.setSelected(gridVisible);
        gridCheck.setOnAction(e -> {
            gridVisible = gridCheck.isSelected();
            render();
        });
        
        Label zoomLabel = new Label("%.0f%%".formatted(zoom * 100));
        zoomLabel.getStyleClass().add("zoom-label");
        zoomLabel.setMinWidth(60);
        zoomLabel.setAlignment(Pos.CENTER);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        tb.getItems().addAll(resetBtn, zoomInBtn, zoomOutBtn, fitBtn, sep1, 
                            playBtn, stopBtn, sep2, gridCheck, spacer, zoomLabel);
        
        return tb;
    }
    
    private Button createToolbarButton(FontAwesomeIcon icon, String tooltip) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("16");
        
        Button btn = new Button();
        btn.setGraphic(iconView);
        btn.setTooltip(new Tooltip(tooltip));
        btn.getStyleClass().add("toolbar-button");
        
        return btn;
    }
    
    private void setupMouseHandlers() {
        // Pan with drag
        canvas.setOnMousePressed(e -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            isPanning = true;
            canvas.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });
        
        canvas.setOnMouseDragged(e -> {
            if (isPanning) {
                double dx = e.getX() - lastMouseX;
                double dy = e.getY() - lastMouseY;
                translateX += dx;
                translateY += dy;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                render();
            }
        });
        
        canvas.setOnMouseReleased(e -> {
            isPanning = false;
            canvas.setCursor(javafx.scene.Cursor.DEFAULT);
        });
        
        // Zoom with scroll
        canvas.setOnScroll(e -> {
            double delta = e.getDeltaY() > 0 ? 1.1 : 0.9;
            double oldZoom = zoom;
            zoom *= delta;
            zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
            
            // Zoom towards mouse position
            double mouseX = e.getX();
            double mouseY = e.getY();
            translateX = mouseX - (mouseX - translateX) * (zoom / oldZoom);
            translateY = mouseY - (mouseY - translateY) * (zoom / oldZoom);
            
            updateZoomLabel();
            render();
        });
        
        // Tooltip on hover
        canvas.setOnMouseMoved(this::handleMouseMove);
    }
    
    private void handleMouseMove(MouseEvent e) {
        double x = (e.getX() - translateX) / zoom;
        double y = (e.getY() - translateY) / zoom;
        
        // Check if hovering over a node
        for (GraphNode node : nodes) {
            double dx = x - node.x;
            double dy = y - node.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance < 15) {
                Tooltip tooltip = new Tooltip(node.label + "\n" + node.info);
                Tooltip.install(canvas, tooltip);
                return;
            }
        }
        
        Tooltip.uninstall(canvas, null);
    }
    
    public void render() {
        // Clear canvas
        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Save state
        gc.save();
        
        // Apply transformations
        gc.translate(translateX, translateY);
        gc.scale(zoom, zoom);
        
        // Draw grid
        if (gridVisible) {
            drawGrid();
        }
        
        // Draw edges
        drawEdges();
        
        // Draw routes
        drawRoutes();
        
        // Draw nodes
        drawNodes();
        
        // Restore state
        gc.restore();
    }
    
    private void drawGrid() {
        gc.setStroke(Color.web("#2a2a2a"));
        gc.setLineWidth(0.5);
        
        double width = canvas.getWidth() / zoom;
        double height = canvas.getHeight() / zoom;
        double offsetX = -translateX / zoom;
        double offsetY = -translateY / zoom;
        
        // Vertical lines
        for (double x = 0; x < width + offsetX; x += gridSpacing) {
            gc.strokeLine(x, offsetY, x, height + offsetY);
        }
        
        // Horizontal lines
        for (double y = 0; y < height + offsetY; y += gridSpacing) {
            gc.strokeLine(offsetX, y, width + offsetX, y);
        }
    }
    
    private void drawEdges() {
        gc.setStroke(Color.web("#3a3a3a"));
        gc.setLineWidth(1);
        
        for (GraphEdge edge : edges) {
            gc.strokeLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
        }
    }
    
    private void drawRoutes() {
        if (routes.isEmpty()) return;
        
        Color[] routeColors = {
            Color.web("#FF6B6B"), Color.web("#4ECDC4"), Color.web("#45B7D1"),
            Color.web("#FFA07A"), Color.web("#98D8C8"), Color.web("#F7DC6F")
        };
        
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            Color routeColor = routeColors[i % routeColors.length];
            
            gc.setStroke(routeColor);
            gc.setLineWidth(3);
            
            List<GraphNode> path = route.nodes;
            for (int j = 0; j < path.size() - 1; j++) {
                GraphNode n1 = path.get(j);
                GraphNode n2 = path.get(j + 1);
                
                // Draw line with arrow
                drawArrow(n1.x, n1.y, n2.x, n2.y, routeColor);
            }
            
            // Animate if needed
            if (isAnimating && animationProgress > i * 0.1) {
                double routeProgress = Math.min(1.0, (animationProgress - i * 0.1) * 1.5);
                drawRouteProgress(route, routeProgress, routeColor);
            }
        }
    }
    
    private void drawArrow(double x1, double y1, double x2, double y2, Color color) {
        gc.setStroke(color);
        gc.strokeLine(x1, y1, x2, y2);
        
        // Arrow head
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double arrowLength = 10;
        double arrowAngle = Math.PI / 6;
        
        double x3 = x2 - arrowLength * Math.cos(angle - arrowAngle);
        double y3 = y2 - arrowLength * Math.sin(angle - arrowAngle);
        double x4 = x2 - arrowLength * Math.cos(angle + arrowAngle);
        double y4 = y2 - arrowLength * Math.sin(angle + arrowAngle);
        
        gc.strokeLine(x2, y2, x3, y3);
        gc.strokeLine(x2, y2, x4, y4);
    }
    
    private void drawRouteProgress(Route route, double progress, Color color) {
        // Draw animated vehicle along route
        List<GraphNode> path = route.nodes;
        int segmentCount = path.size() - 1;
        double totalProgress = progress * segmentCount;
        int currentSegment = (int) totalProgress;
        double segmentProgress = totalProgress - currentSegment;
        
        if (currentSegment < segmentCount) {
            GraphNode from = path.get(currentSegment);
            GraphNode to = path.get(currentSegment + 1);
            
            double x = from.x + (to.x - from.x) * segmentProgress;
            double y = from.y + (to.y - from.y) * segmentProgress;
            
            // Draw vehicle icon
            gc.setFill(color);
            gc.fillOval(x - 8, y - 8, 16, 16);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeOval(x - 8, y - 8, 16, 16);
        }
    }
    
    private void drawNodes() {
        for (GraphNode node : nodes) {
            // Node circle
            if (node.isDepot) {
                // Depot - special styling
                gc.setFill(Color.web("#FF6B6B"));
                gc.fillOval(node.x - 20, node.y - 20, 40, 40);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(3);
                gc.strokeOval(node.x - 20, node.y - 20, 40, 40);
            } else {
                // Regular node
                gc.setFill(Color.web("#4ECDC4"));
                gc.fillOval(node.x - 12, node.y - 12, 24, 24);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeOval(node.x - 12, node.y - 12, 24, 24);
            }
            
            // Label
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 12));
            gc.fillText(node.label, node.x - 8, node.y + 5);
            
            // Demand indicator
            if (node.demand > 0) {
                gc.setFill(Color.web("#FFA07A"));
                gc.fillText("+" + node.demand, node.x - 15, node.y + 25);
            } else if (node.demand < 0) {
                gc.setFill(Color.web("#98D8C8"));
                gc.fillText(String.valueOf(node.demand), node.x - 15, node.y + 25);
            }
        }
    }
    
    public void resetView() {
        zoom = 1.0;
        translateX = 0;
        translateY = 0;
        updateZoomLabel();
        render();
    }
    
    public void zoomIn() {
        zoom = Math.min(MAX_ZOOM, zoom * 1.2);
        updateZoomLabel();
        render();
    }
    
    public void zoomOut() {
        zoom = Math.max(MIN_ZOOM, zoom / 1.2);
        updateZoomLabel();
        render();
    }
    
    public void fitToView() {
        if (nodes.isEmpty()) return;
        
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        
        for (GraphNode node : nodes) {
            minX = Math.min(minX, node.x);
            minY = Math.min(minY, node.y);
            maxX = Math.max(maxX, node.x);
            maxY = Math.max(maxY, node.y);
        }
        
        double graphWidth = maxX - minX + 100;
        double graphHeight = maxY - minY + 100;
        
        zoom = Math.min(canvas.getWidth() / graphWidth, canvas.getHeight() / graphHeight);
        translateX = (canvas.getWidth() - (maxX + minX) * zoom) / 2;
        translateY = (canvas.getHeight() - (maxY + minY) * zoom) / 2;
        
        updateZoomLabel();
        render();
    }
    
    public void setGridVisible(boolean visible) {
        this.gridVisible = visible;
        render();
    }
    
    private void animateRoutes() {
        if (routes.isEmpty()) {
            return;
        }
        
        isAnimating = true;
        animationProgress = 0;
        
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                
                animationProgress += deltaTime * 0.3; // Speed factor
                
                if (animationProgress >= 1.0) {
                    animationProgress = 0; // Loop animation
                }
                
                render();
            }
        };
        animationTimer.start();
    }
    
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            isAnimating = false;
            animationProgress = 0;
            render();
        }
    }
    
    private void updateZoomLabel() {
        Label zoomLabel = (Label) toolbar.getItems().get(toolbar.getItems().size() - 1);
        zoomLabel.setText("%.0f%%".formatted(zoom * 100));
    }
    
    public void setGraphData(List<GraphNode> nodes, List<GraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        
        // Find depot
        for (GraphNode node : nodes) {
            if (node.isDepot) {
                depotNode = node;
                break;
            }
        }
        
        fitToView();
        render();
    }
    
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
        render();
    }
    
    private void generateSampleGraph() {
        // Create sample nodes in a circular pattern
        Random rand = new Random(42);
        int nodeCount = 20;
        double centerX = 400;
        double centerY = 300;
        double radius = 200;
        
        // Depot at center
        GraphNode depot = new GraphNode(0, centerX, centerY, "D", true);
        depot.info = "Depot\nCapacity: 100";
        nodes.add(depot);
        
        // Customer nodes
        for (int i = 1; i <= nodeCount; i++) {
            double angle = 2 * Math.PI * i / nodeCount + rand.nextDouble() * 0.3;
            double r = radius + rand.nextDouble() * 100 - 50;
            double x = centerX + r * Math.cos(angle);
            double y = centerY + r * Math.sin(angle);
            
            GraphNode node = new GraphNode(i, x, y, String.valueOf(i), false);
            node.demand = rand.nextInt(20) - 10;
            node.info = "Customer " + i + "\nDemand: " + node.demand;
            nodes.add(node);
        }
        
        // Create edges (complete graph for simplicity)
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                edges.add(new GraphEdge(nodes.get(i), nodes.get(j)));
            }
        }
        
        // Create sample routes
        Route route1 = new Route();
        route1.nodes.add(nodes.getFirst()); // Depot
        for (int i = 1; i <= 5; i++) {
            route1.nodes.add(nodes.get(i));
        }
        route1.nodes.add(nodes.getFirst()); // Back to depot
        
        Route route2 = new Route();
        route2.nodes.add(nodes.getFirst());
        for (int i = 6; i <= 10; i++) {
            route2.nodes.add(nodes.get(i));
        }
        route2.nodes.add(nodes.getFirst());
        
        routes.add(route1);
        routes.add(route2);
        
        fitToView();
    }
}

// Helper classes
class GraphNode {
    int id;
    double x, y;
    String label;
    boolean isDepot;
    int demand = 0;
    String info = "";
    
    GraphNode(int id, double x, double y, String label, boolean isDepot) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.label = label;
        this.isDepot = isDepot;
    }
}

class GraphEdge {
    GraphNode from, to;
    double weight;
    
    GraphEdge(GraphNode from, GraphNode to) {
        this.from = from;
        this.to = to;
        this.weight = Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
    }
}

class Route {
    List<GraphNode> nodes = new ArrayList<>();
    double totalCost = 0;
    double totalDistance = 0;
    double totalTime = 0;
}
