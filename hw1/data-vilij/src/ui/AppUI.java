//Gary Yeung
package ui;

import actions.AppActions;
import dataprocessors.TSDProcessor;
import static java.io.File.separator;
import java.io.IOException; 
import java.util.ArrayList; 
import java.util.logging.Level; 
import java.util.logging.Logger;                                        
import javafx.scene.Cursor; 
import javafx.scene.chart.LineChart; 
import javafx.scene.chart.NumberAxis; 
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button; 
import javafx.scene.control.CheckBox; 
import javafx.scene.control.Label; 
import javafx.scene.control.TextArea; 
import javafx.scene.control.Tooltip; 
import javafx.scene.paint.Color; 
import javafx.stage.Stage; 
import settings.AppPropertyTypes;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton; // toolbar button to take a screenshot of the data
    private CheckBox readOnlyMode;
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button displayButton;  // workspace button to display data on the chart
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display
    private String newText = "";

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public TextArea getText() {
        return textArea;
    }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = "/" + String.join(separator,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath = String.join(separator,
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnshotButton = setToolbarButton(scrnshoticonPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),
                true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException ex) {
                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        chart.getData().clear();
        textArea.clear();
        scrnshotButton.setDisable(true);
    }

    private void layout() {

        Label dFile = new Label("Data File");
        appPane.getChildren().add(dFile);
        textArea = new TextArea();
        appPane.getChildren().add(textArea);
        textArea.setMaxHeight(220);
        displayButton = new Button("Display");
        appPane.getChildren().add(displayButton);
        readOnlyMode = new CheckBox("Read-Only Mode");
        appPane.getChildren().add(readOnlyMode);
        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        chart = new LineChart<Number, Number>(xAxis, yAxis);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.getStylesheets().add(AppUI.class.getResource("ChartUI.css").toExternalForm());
        chart.setTitle("Data Visualization");
        appPane.getChildren().add(chart);

    }

    private void setWorkspaceActions() {
        textArea.setOnKeyTyped(e -> handleTextRequest());
        displayButton.setOnAction(e -> handleDisplayRequest());
        readOnlyMode.setOnAction(e -> handleReadOnlyRequest());
    }

    public void handleReadOnlyRequest() {
        if (readOnlyMode.isSelected()) {
            textArea.setDisable(true);
        } else {
            textArea.setDisable(false);
        }
    }

    public void handleTextRequest() {
        if (textArea.getText().isEmpty()) {
            saveButton.setDisable(true);
            newButton.setDisable(true);
        }
        if (!textArea.getText().isEmpty()) {
            saveButton.setDisable(false);
            newButton.setDisable(false);
        }
    }

    private void addAverageLine(TSDProcessor tsdProcessor) {
        ArrayList<Double> xCoords = tsdProcessor.getXCoords();
        ArrayList<Double> yCoords = tsdProcessor.getYCoords();
        double smallestX = xCoords.get(0);
        double largestX = xCoords.get(0);
        double avgY = yCoords.get(0);
        for (int i = 0; i < xCoords.size(); ++i) {
            double currVal = xCoords.get(i);
            if (smallestX > currVal) {
                smallestX = currVal;
            }
            if (largestX < currVal) {
                largestX = currVal;
            }
        }
        for (int i = 1; i < yCoords.size(); ++i) {
            avgY += yCoords.get(i);
        }
        avgY = avgY / yCoords.size();
        XYChart.Series avg = new XYChart.Series<>();
        XYChart.Data<Number, Number> firstPoint = new XYChart.Data<>(smallestX, avgY);
        XYChart.Data<Number, Number> secondPoint = new XYChart.Data<>(largestX, avgY);
        avg.getData().add(firstPoint);
        avg.getData().add(secondPoint);
        avg.setName("Average Y Values");
        chart.getData().add(avg);
        avg.getNode().getStyleClass().add("avg");
        firstPoint.getNode().setVisible(false);
        secondPoint.getNode().setVisible(false);
    }

    private void addNodeListeners() {
        TSDProcessor tsdP = new TSDProcessor();
        try {
            tsdP.processString(textArea.getText());
            ArrayList<String> nameList = tsdP.getNameList();
            for (XYChart.Series<Number, Number> series : chart.getData()) {
                for (XYChart.Data<Number, Number> point : series.getData()) {
                    Tooltip tt = new Tooltip(nameList.get(0));
                    nameList.remove(0);
                    Tooltip.install(point.getNode(), tt);
                    point.getNode().setOnMouseEntered(e -> appPane.setCursor(Cursor.HAND));
                    point.getNode().setOnMouseExited(e -> appPane.setCursor(Cursor.DEFAULT));

                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void handleDisplayRequest() {
        scrnshotButton.setDisable(false);
        String userInput = textArea.getText();
        if (newText.compareTo(userInput) == 0) {
            hasNewText = false;
        } else {
            hasNewText = true;
        }
        if (!hasNewText) {
            ErrorDialog.getDialog().show("Error: Repetitive Data", "You already inputted this data");

        } else {
            newText = userInput;
            chart.getData().clear();
            TSDProcessor tsdProcessor = new TSDProcessor();
            try {
                tsdProcessor.processString(userInput);
                tsdProcessor.toChartData(chart);
                this.addNodeListeners();
                this.addAverageLine(tsdProcessor);
            } catch (Exception e) {
                e.printStackTrace();
                saveButton.setDisable(true);
                chart.getData().clear();
                ErrorDialog.getDialog().show("Error: Data Inputed Was in Incorrect Format", e.getLocalizedMessage());
            }
        }
    }

    public void disableSave() {
        saveButton.setDisable(true);
    }
}
