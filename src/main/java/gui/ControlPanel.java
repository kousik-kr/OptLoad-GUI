package gui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;

import java.io.File;

/**
 * Control panel for configuring and running VRP solvers
 */
public class ControlPanel extends VBox {
    
    private static final double PANEL_WIDTH = 350;
    
    private OptLoadGUI mainApp;
    
    // Controls
    private TextField directoryField;
    private ComboBox<String> solverComboBox;
    private Spinner<Integer> capacitySpinner;
    private Spinner<Integer> clusterSizeSpinner;
    private TextField startTimeField;
    private TextField endTimeField;
    private CheckBox enableAnimationCheck;
    private CheckBox verboseLoggingCheck;
    private Button runButton;
    private Button stopButton;
    private Button resetButton;
    private TextArea queryPreview;
    
    public ControlPanel(OptLoadGUI mainApp) {
        this.mainApp = mainApp;
        
        setPrefWidth(PANEL_WIDTH);
        setMinWidth(PANEL_WIDTH);
        setMaxWidth(PANEL_WIDTH);
        setPadding(new Insets(15));
        setSpacing(10);
        getStyleClass().add("control-panel");
        
        buildUI();
    }
    
    private void buildUI() {
        // Title
        Label title = new Label("Configuration");
        title.getStyleClass().add("panel-title");
        FontAwesomeIconView titleIcon = new FontAwesomeIconView(FontAwesomeIcon.SLIDERS);
        titleIcon.setSize("20");
        title.setGraphic(titleIcon);
        
        Separator sep1 = new Separator();
        
        // Directory Section
        VBox directorySection = createSection("Working Directory", 
            createDirectoryControls());
        
        Separator sep2 = new Separator();
        
        // Solver Section
        VBox solverSection = createSection("Solver Configuration", 
            createSolverControls());
        
        Separator sep3 = new Separator();
        
        // Query Parameters Section
        VBox querySection = createSection("Query Parameters", 
            createQueryControls());
        
        Separator sep4 = new Separator();
        
        // Options Section
        VBox optionsSection = createSection("Options", 
            createOptionsControls());
        
        Separator sep5 = new Separator();
        
        // Query Preview
        VBox previewSection = createSection("Query Preview", 
            createQueryPreview());
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Action Buttons
        HBox actionButtons = createActionButtons();
        
        getChildren().addAll(
            title, sep1,
            directorySection, sep2,
            solverSection, sep3,
            querySection, sep4,
            optionsSection, sep5,
            previewSection,
            spacer,
            actionButtons
        );
    }
    
    private VBox createSection(String title, VBox content) {
        VBox section = new VBox(10);
        section.getStyleClass().add("control-section");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");
        
        section.getChildren().addAll(titleLabel, content);
        return section;
    }
    
    private VBox createDirectoryControls() {
        VBox box = new VBox(8);
        
        HBox dirBox = new HBox(5);
        directoryField = new TextField(System.getProperty("user.dir"));
        directoryField.setPromptText("Select working directory...");
        directoryField.setEditable(false);
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        
        Button browseBtn = new Button();
        FontAwesomeIconView folderIcon = new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN);
        folderIcon.setSize("14");
        browseBtn.setGraphic(folderIcon);
        browseBtn.setTooltip(new Tooltip("Browse..."));
        browseBtn.setOnAction(e -> mainApp.selectDirectory(
            (javafx.stage.Stage) getScene().getWindow()));
        
        dirBox.getChildren().addAll(directoryField, browseBtn);
        
        Label infoLabel = new Label("Graph files and queries will be loaded from this directory");
        infoLabel.getStyleClass().add("info-label");
        infoLabel.setWrapText(true);
        
        box.getChildren().addAll(dirBox, infoLabel);
        return box;
    }
    
    private VBox createSolverControls() {
        VBox box = new VBox(8);
        
        // Solver selection
        Label solverLabel = new Label("Solver Algorithm:");
        solverComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "Default Clustering (Heuristic)",
            "Insertion Heuristic",
            "Bazelmans Baseline",
            "LIFO Stack",
            "FoodMatch Solver",
            "Exact Algorithm (Optimal)"
        ));
        solverComboBox.getSelectionModel().select(0);
        solverComboBox.setMaxWidth(Double.MAX_VALUE);
        
        // Algorithm description
        Label descLabel = new Label();
        descLabel.getStyleClass().add("info-label");
        descLabel.setWrapText(true);
        updateSolverDescription(descLabel);
        
        solverComboBox.setOnAction(e -> updateSolverDescription(descLabel));
        
        box.getChildren().addAll(solverLabel, solverComboBox, descLabel);
        return box;
    }
    
    private void updateSolverDescription(Label label) {
        String selected = solverComboBox.getSelectionModel().getSelectedItem();
        String description = "";
        
        switch (selected) {
            case "Default Clustering (Heuristic)":
                description = "Fast cluster-based heuristic for large instances. Good balance of speed and quality.";
                break;
            case "Insertion Heuristic":
                description = "Greedy insertion algorithm. Fast and efficient for medium-sized problems.";
                break;
            case "Bazelmans Baseline":
                description = "Baseline algorithm from research literature. Good for comparison.";
                break;
            case "LIFO Stack":
                description = "Stack-based approach for loading constraints. Specialized algorithm.";
                break;
            case "FoodMatch Solver":
                description = "Custom solver optimized for food delivery scenarios.";
                break;
            case "Exact Algorithm (Optimal)":
                description = "⚠️ Finds optimal solution but slow for large instances. Best for small problems.";
                break;
        }
        
        label.setText(description);
    }
    
    private VBox createQueryControls() {
        VBox box = new VBox(8);
        
        // Capacity
        HBox capacityBox = new HBox(10);
        capacityBox.setAlignment(Pos.CENTER_LEFT);
        Label capacityLabel = new Label("Vehicle Capacity:");
        capacityLabel.setPrefWidth(130);
        capacitySpinner = new Spinner<>(1, 1000, 100, 10);
        capacitySpinner.setEditable(true);
        capacitySpinner.setPrefWidth(100);
        capacityBox.getChildren().addAll(capacityLabel, capacitySpinner);
        
        // Cluster size
        HBox clusterBox = new HBox(10);
        clusterBox.setAlignment(Pos.CENTER_LEFT);
        Label clusterLabel = new Label("Max Cluster Size:");
        clusterLabel.setPrefWidth(130);
        clusterSizeSpinner = new Spinner<>(1, 10, 3, 1);
        clusterSizeSpinner.setEditable(true);
        clusterSizeSpinner.setPrefWidth(100);
        clusterBox.getChildren().addAll(clusterLabel, clusterSizeSpinner);
        
        // Time window
        Label timeLabel = new Label("Depot Working Hours (minutes):");
        
        HBox timeBox = new HBox(10);
        timeBox.setAlignment(Pos.CENTER_LEFT);
        
        Label startLabel = new Label("Start:");
        startTimeField = new TextField("540");
        startTimeField.setPrefWidth(80);
        startTimeField.setPromptText("540");
        
        Label endLabel = new Label("End:");
        endTimeField = new TextField("1140");
        endTimeField.setPrefWidth(80);
        endTimeField.setPromptText("1140");
        
        timeBox.getChildren().addAll(startLabel, startTimeField, endLabel, endTimeField);
        
        Label timeInfo = new Label("540 = 9:00 AM, 1140 = 7:00 PM");
        timeInfo.getStyleClass().add("info-label");
        
        box.getChildren().addAll(capacityBox, clusterBox, timeLabel, timeBox, timeInfo);
        return box;
    }
    
    private VBox createOptionsControls() {
        VBox box = new VBox(8);
        
        enableAnimationCheck = new CheckBox("Enable Route Animation");
        enableAnimationCheck.setSelected(true);
        
        verboseLoggingCheck = new CheckBox("Verbose Logging");
        verboseLoggingCheck.setSelected(false);
        
        box.getChildren().addAll(enableAnimationCheck, verboseLoggingCheck);
        return box;
    }
    
    private VBox createQueryPreview() {
        VBox box = new VBox(8);
        
        queryPreview = new TextArea();
        queryPreview.setEditable(false);
        queryPreview.setPrefRowCount(6);
        queryPreview.setWrapText(true);
        queryPreview.setPromptText("Query details will appear here...");
        queryPreview.setText("No query loaded.\n\nLoad a query file or generate a new query to see details.");
        
        Button loadQueryBtn = new Button("Load Query File...");
        FontAwesomeIconView fileIcon = new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT);
        fileIcon.setSize("14");
        loadQueryBtn.setGraphic(fileIcon);
        loadQueryBtn.setMaxWidth(Double.MAX_VALUE);
        loadQueryBtn.setOnAction(e -> mainApp.loadQueryFile(
            (javafx.stage.Stage) getScene().getWindow()));
        
        box.getChildren().addAll(queryPreview, loadQueryBtn);
        return box;
    }
    
    private HBox createActionButtons() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 0, 0, 0));
        
        // Run button
        runButton = new Button("Run Solver");
        FontAwesomeIconView playIcon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
        playIcon.setSize("14");
        runButton.setGraphic(playIcon);
        runButton.getStyleClass().add("run-button");
        runButton.setPrefWidth(110);
        runButton.setOnAction(e -> mainApp.runOptimization());
        
        // Stop button
        stopButton = new Button("Stop");
        FontAwesomeIconView stopIcon = new FontAwesomeIconView(FontAwesomeIcon.STOP);
        stopIcon.setSize("14");
        stopButton.setGraphic(stopIcon);
        stopButton.getStyleClass().add("stop-button");
        stopButton.setPrefWidth(70);
        stopButton.setDisable(true);
        stopButton.setOnAction(e -> mainApp.stopOptimization());
        
        // Reset button
        resetButton = new Button("Reset");
        FontAwesomeIconView resetIcon = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        resetIcon.setSize("14");
        resetButton.setGraphic(resetIcon);
        resetButton.setPrefWidth(70);
        resetButton.setOnAction(e -> resetControls());
        
        box.getChildren().addAll(runButton, stopButton, resetButton);
        return box;
    }
    
    public void setDirectory(String directory) {
        directoryField.setText(directory);
    }
    
    public void setRunning(boolean running) {
        runButton.setDisable(running);
        stopButton.setDisable(!running);
        solverComboBox.setDisable(running);
        capacitySpinner.setDisable(running);
        clusterSizeSpinner.setDisable(running);
    }
    
    private void resetControls() {
        capacitySpinner.getValueFactory().setValue(100);
        clusterSizeSpinner.getValueFactory().setValue(3);
        startTimeField.setText("540");
        endTimeField.setText("1140");
        enableAnimationCheck.setSelected(true);
        verboseLoggingCheck.setSelected(false);
        solverComboBox.getSelectionModel().select(0);
    }
    
    public String getSelectedSolver() {
        return solverComboBox.getSelectionModel().getSelectedItem();
    }
    
    public int getCapacity() {
        return capacitySpinner.getValue();
    }
    
    public int getClusterSize() {
        return clusterSizeSpinner.getValue();
    }
    
    public int getStartTime() {
        try {
            return Integer.parseInt(startTimeField.getText());
        } catch (NumberFormatException e) {
            return 540;
        }
    }
    
    public int getEndTime() {
        try {
            return Integer.parseInt(endTimeField.getText());
        } catch (NumberFormatException e) {
            return 1140;
        }
    }
    
    public boolean isAnimationEnabled() {
        return enableAnimationCheck.isSelected();
    }
    
    public boolean isVerboseLogging() {
        return verboseLoggingCheck.isSelected();
    }
}
