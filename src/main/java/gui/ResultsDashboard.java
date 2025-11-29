package gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;

/**
 * Results dashboard displaying optimization results with charts and metrics
 */
public class ResultsDashboard extends VBox {
    
    private static final double PANEL_WIDTH = 400;
    
    // Metrics cards
    private MetricCard totalCostCard;
    private MetricCard totalDistanceCard;
    private MetricCard totalTimeCard;
    private MetricCard vehiclesUsedCard;
    private MetricCard executionTimeCard;
    
    // Charts
    private BarChart<String, Number> routeComparisonChart;
    private PieChart vehicleUtilizationChart;
    private LineChart<Number, Number> convergenceChart;
    
    // Route details
    private TableView<RouteDetail> routeTable;
    
    // Progress
    private ProgressBar progressBar;
    private Label progressLabel;
    
    // Tabs for different views
    private TabPane tabPane;
    
    public ResultsDashboard() {
        setPrefWidth(PANEL_WIDTH);
        setMinWidth(PANEL_WIDTH);
        setMaxWidth(PANEL_WIDTH);
        setPadding(new Insets(15));
        setSpacing(10);
        getStyleClass().add("results-panel");
        
        buildUI();
    }
    
    private void buildUI() {
        // Title
        Label title = new Label("Results");
        title.getStyleClass().add("panel-title");
        FontAwesomeIconView titleIcon = new FontAwesomeIconView(FontAwesomeIcon.BAR_CHART);
        titleIcon.setSize("20");
        title.setGraphic(titleIcon);
        
        Separator sep1 = new Separator();
        
        // Progress section
        VBox progressSection = createProgressSection();
        
        Separator sep2 = new Separator();
        
        // Metrics overview
        GridPane metricsGrid = createMetricsGrid();
        
        Separator sep3 = new Separator();
        
        // Tabbed interface for detailed results
        tabPane = createResultsTabs();
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        getChildren().addAll(title, sep1, progressSection, sep2, metricsGrid, sep3, tabPane);
    }
    
    private VBox createProgressSection() {
        VBox box = new VBox(8);
        box.getStyleClass().add("progress-section");
        
        progressLabel = new Label("Ready");
        progressLabel.getStyleClass().add("progress-label");
        
        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(8);
        
        box.getChildren().addAll(progressLabel, progressBar);
        return box;
    }
    
    private GridPane createMetricsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        // Create metric cards
        totalCostCard = new MetricCard("Total Cost", "0.00", FontAwesomeIcon.DOLLAR, "#FF6B6B");
        totalDistanceCard = new MetricCard("Distance", "0.0 km", FontAwesomeIcon.ROAD, "#4ECDC4");
        totalTimeCard = new MetricCard("Time", "0.0 min", FontAwesomeIcon.CLOCK_ALT, "#45B7D1");
        vehiclesUsedCard = new MetricCard("Vehicles", "0", FontAwesomeIcon.TRUCK, "#FFA07A");
        executionTimeCard = new MetricCard("Exec Time", "0.0 s", FontAwesomeIcon.HOURGLASS_HALF, "#98D8C8");
        
        // Layout in 2 columns
        grid.add(totalCostCard, 0, 0);
        grid.add(totalDistanceCard, 1, 0);
        grid.add(totalTimeCard, 0, 1);
        grid.add(vehiclesUsedCard, 1, 1);
        grid.add(executionTimeCard, 0, 2, 2, 1);
        
        // Make cards expand to fill space
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);
        
        return grid;
    }
    
    private TabPane createResultsTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Routes tab
        Tab routesTab = new Tab("Routes");
        FontAwesomeIconView routeIcon = new FontAwesomeIconView(FontAwesomeIcon.LIST);
        routeIcon.setSize("14");
        routesTab.setGraphic(routeIcon);
        routesTab.setContent(createRoutesView());
        
        // Charts tab
        Tab chartsTab = new Tab("Charts");
        FontAwesomeIconView chartIcon = new FontAwesomeIconView(FontAwesomeIcon.PIE_CHART);
        chartIcon.setSize("14");
        chartsTab.setGraphic(chartIcon);
        chartsTab.setContent(createChartsView());
        
        // Convergence tab
        Tab convergenceTab = new Tab("Convergence");
        FontAwesomeIconView convIcon = new FontAwesomeIconView(FontAwesomeIcon.LINE_CHART);
        convIcon.setSize("14");
        convergenceTab.setGraphic(convIcon);
        convergenceTab.setContent(createConvergenceView());
        
        tabs.getTabs().addAll(routesTab, chartsTab, convergenceTab);
        return tabs;
    }
    
    private ScrollPane createRoutesView() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        
        // Route table
        routeTable = new TableView<>();
        routeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<RouteDetail, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().routeName));
        routeCol.setPrefWidth(80);
        
        TableColumn<RouteDetail, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().path));
        pathCol.setPrefWidth(150);
        
        TableColumn<RouteDetail, String> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.2f", data.getValue().cost)));
        costCol.setPrefWidth(60);
        
        TableColumn<RouteDetail, String> distCol = new TableColumn<>("Dist");
        distCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.1f", data.getValue().distance)));
        distCol.setPrefWidth(60);
        
        routeTable.getColumns().addAll(routeCol, pathCol, costCol, distCol);
        
        // Export button
        Button exportBtn = new Button("Export Routes");
        FontAwesomeIconView exportIcon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
        exportIcon.setSize("14");
        exportBtn.setGraphic(exportIcon);
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        
        container.getChildren().addAll(routeTable, exportBtn);
        VBox.setVgrow(routeTable, Priority.ALWAYS);
        
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        return scroll;
    }
    
    private ScrollPane createChartsView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(10));
        
        // Route comparison bar chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        routeComparisonChart = new BarChart<>(xAxis, yAxis);
        routeComparisonChart.setTitle("Route Comparison");
        routeComparisonChart.setLegendVisible(false);
        routeComparisonChart.setPrefHeight(200);
        
        // Vehicle utilization pie chart
        vehicleUtilizationChart = new PieChart();
        vehicleUtilizationChart.setTitle("Vehicle Utilization");
        vehicleUtilizationChart.setPrefHeight(200);
        vehicleUtilizationChart.setLegendVisible(true);
        
        container.getChildren().addAll(routeComparisonChart, vehicleUtilizationChart);
        
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        return scroll;
    }
    
    private ScrollPane createConvergenceView() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        
        // Convergence line chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Iteration");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Best Cost");
        
        convergenceChart = new LineChart<>(xAxis, yAxis);
        convergenceChart.setTitle("Solution Convergence");
        convergenceChart.setCreateSymbols(false);
        convergenceChart.setPrefHeight(300);
        
        Label infoLabel = new Label("Shows how the solution quality improves over iterations");
        infoLabel.getStyleClass().add("info-label");
        infoLabel.setWrapText(true);
        
        container.getChildren().addAll(convergenceChart, infoLabel);
        VBox.setVgrow(convergenceChart, Priority.ALWAYS);
        
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        return scroll;
    }
    
    public void updateProgress(double progress) {
        progressBar.setProgress(progress);
        progressLabel.setText(String.format("Progress: %.0f%%", progress * 100));
    }
    
    public void showResults(Map<String, Object> results) {
        // Animate metrics update
        animateMetricUpdate(totalCostCard, (Double) results.get("totalCost"));
        animateMetricUpdate(totalDistanceCard, (Double) results.get("totalDistance"));
        animateMetricUpdate(totalTimeCard, (Double) results.get("totalTime"));
        vehiclesUsedCard.setValue(results.get("vehiclesUsed").toString());
        executionTimeCard.setValue(String.format("%.2f s", (Double) results.get("executionTime")));
        
        // Update charts
        updateCharts(results);
        
        // Update route table
        updateRouteTable();
        
        progressLabel.setText("Optimization Complete");
        progressBar.setProgress(1.0);
    }
    
    private void animateMetricUpdate(MetricCard card, double targetValue) {
        String unit = "";
        if (card == totalDistanceCard) unit = " km";
        else if (card == totalTimeCard) unit = " min";
        
        final String finalUnit = unit;
        javafx.beans.property.DoubleProperty animValue = new javafx.beans.property.SimpleDoubleProperty(0);
        
        animValue.addListener((obs, oldVal, newVal) -> {
            double current = newVal.doubleValue();
            if (finalUnit.isEmpty()) {
                card.setValue(String.format("%.2f", current));
            } else {
                card.setValue(String.format("%.1f%s", current, finalUnit));
            }
        });
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(animValue, 0)),
            new KeyFrame(Duration.millis(1000), new KeyValue(animValue, targetValue, Interpolator.EASE_OUT))
        );
        
        timeline.play();
    }
    
    private void updateCharts(Map<String, Object> results) {
        // Bar chart - route costs
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Route Cost");
        series.getData().add(new XYChart.Data<>("Route 1", 420.5));
        series.getData().add(new XYChart.Data<>("Route 2", 385.2));
        series.getData().add(new XYChart.Data<>("Route 3", 444.8));
        routeComparisonChart.getData().clear();
        routeComparisonChart.getData().add(series);
        
        // Pie chart - vehicle utilization
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data("Vehicle 1 (85%)", 85),
            new PieChart.Data("Vehicle 2 (92%)", 92),
            new PieChart.Data("Vehicle 3 (78%)", 78)
        );
        vehicleUtilizationChart.setData(pieData);
        
        // Convergence chart
        XYChart.Series<Number, Number> convergenceSeries = new XYChart.Series<>();
        convergenceSeries.setName("Best Solution");
        
        // Simulate convergence data
        Random rand = new Random(42);
        double cost = 2000;
        for (int i = 0; i <= 50; i++) {
            cost = cost * 0.95 + rand.nextDouble() * 20;
            convergenceSeries.getData().add(new XYChart.Data<>(i, cost));
        }
        
        convergenceChart.getData().clear();
        convergenceChart.getData().add(convergenceSeries);
    }
    
    private void updateRouteTable() {
        ObservableList<RouteDetail> routes = FXCollections.observableArrayList(
            new RouteDetail("Route 1", "D → 1 → 2 → 3 → 4 → 5 → D", 420.5, 42.1),
            new RouteDetail("Route 2", "D → 6 → 7 → 8 → 9 → 10 → D", 385.2, 38.5),
            new RouteDetail("Route 3", "D → 11 → 12 → 13 → 14 → D", 444.8, 44.5)
        );
        
        routeTable.setItems(routes);
    }
}

/**
 * Metric card component for displaying key performance indicators
 */
class MetricCard extends VBox {
    private Label valueLabel;
    
    public MetricCard(String title, String initialValue, FontAwesomeIcon icon, String color) {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(12));
        getStyleClass().add("metric-card");
        setStyle("-fx-border-color: " + color + ";");
        
        // Icon
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("24");
        iconView.setStyle("-fx-fill: " + color + ";");
        
        // Value
        valueLabel = new Label(initialValue);
        valueLabel.getStyleClass().add("metric-value");
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        
        getChildren().addAll(iconView, valueLabel, titleLabel);
    }
    
    public void setValue(String value) {
        valueLabel.setText(value);
    }
}

/**
 * Route detail data model for table view
 */
class RouteDetail {
    String routeName;
    String path;
    double cost;
    double distance;
    
    public RouteDetail(String routeName, String path, double cost, double distance) {
        this.routeName = routeName;
        this.path = path;
        this.cost = cost;
        this.distance = distance;
    }
}
