package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

/**
 * Status bar for displaying application status and notifications
 */
public class StatusBar extends HBox {
    
    private Label statusLabel;
    private Label timeLabel;
    private FontAwesomeIconView statusIcon;
    private Timeline pulseAnimation;
    
    public enum StatusType {
        INFO("#4ECDC4", FontAwesomeIcon.INFO_CIRCLE),
        SUCCESS("#5FBB5F", FontAwesomeIcon.CHECK_CIRCLE),
        WARNING("#FFA07A", FontAwesomeIcon.EXCLAMATION_TRIANGLE),
        ERROR("#FF6B6B", FontAwesomeIcon.EXCLAMATION_CIRCLE),
        RUNNING("#45B7D1", FontAwesomeIcon.SPINNER);
        
        final String color;
        final FontAwesomeIcon icon;
        
        StatusType(String color, FontAwesomeIcon icon) {
            this.color = color;
            this.icon = icon;
        }
    }
    
    public StatusBar() {
        setPadding(new Insets(8, 15, 8, 15));
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);
        getStyleClass().add("status-bar");
        
        // Status icon
        statusIcon = new FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE);
        statusIcon.setSize("14");
        
        // Status message
        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Time/version label
        timeLabel = new Label("OptLoad v1.0.0");
        timeLabel.getStyleClass().add("time-label");
        
        getChildren().addAll(statusIcon, statusLabel, spacer, timeLabel);
        
        // Start time update
        startTimeUpdate();
    }
    
    public void updateStatus(String message, StatusType type) {
        statusLabel.setText(message);
        statusIcon.setIcon(type.icon);
        statusIcon.setStyle("-fx-fill: " + type.color + ";");
        
        // Stop any existing animation
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
        
        // Add pulse effect for running status
        if (type == StatusType.RUNNING) {
            pulseAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(statusIcon.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(1), 
                    new KeyValue(statusIcon.rotateProperty(), 360))
            );
            pulseAnimation.setCycleCount(Timeline.INDEFINITE);
            pulseAnimation.play();
        }
        
        // Flash animation
        FadeTransition ft = new FadeTransition(Duration.millis(200), statusLabel);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.play();
    }
    
    private void startTimeUpdate() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                java.time.LocalTime now = java.time.LocalTime.now();
                timeLabel.setText(String.format("OptLoad v1.0.0  |  %02d:%02d:%02d", 
                    now.getHour(), now.getMinute(), now.getSecond()));
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
