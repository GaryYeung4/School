//Gary Yeung
package ui;

import actions.AppActions;
import dataprocessors.TSDProcessor;
import static java.io.File.separator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javax.swing.ButtonGroup;
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
    private Button doneButton;
    private Button editButton;
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button runButton;  // workspace button to display data on the chart
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display
    private String newText = "";
    private Label dataInfo;
    private RadioButton classButton;
    private RadioButton clussButton;
    private ToggleGroup algTypes;

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
        newButton.setDisable(false);
        //data
        VBox leftSide = new VBox();
        textArea = new TextArea();
        textArea.setVisible(false);
        textArea.setDisable(true);
        leftSide.getChildren().add(textArea);
        textArea.setMinHeight(220);
        doneButton = new Button("Done");
        doneButton.setVisible(false);
        doneButton.setManaged(false);
        doneButton.setDisable(true);
        leftSide.getChildren().add(doneButton);
        editButton = new Button("Edit");
        editButton.setVisible(false);
        editButton.setManaged(false);
        editButton.setDisable(true);
        leftSide.getChildren().add(editButton);
        dataInfo = new Label();
        dataInfo.setVisible(false);
        leftSide.getChildren().add(dataInfo);
        //algorithm section
        classButton = new RadioButton();
        classButton.setText("Classification");
        classButton.setVisible(false);
        classButton.setDisable(true);
        classButton.setManaged(false);
        clussButton = new RadioButton();
        clussButton.setText("Clustering");
        clussButton.setVisible(false);
        clussButton.setDisable(true);
        clussButton.setManaged(false);
        algTypes = new ToggleGroup();
        classButton.setToggleGroup(algTypes);
        clussButton.setToggleGroup(algTypes);
        leftSide.getChildren().addAll(classButton, clussButton);
        //run button
        runButton = new Button("Run");
        runButton.setVisible(false);
        runButton.setDisable(true);
        leftSide.getChildren().add(runButton);
        //chart
        VBox rightSide = new VBox();
        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        chart = new LineChart<Number, Number>(xAxis, yAxis);
        chart.setTitle("Data Visualization");
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.getStylesheets().add(AppUI.class.getResource("ChartUI.css").toExternalForm());
        rightSide.getChildren().add(chart);
        HBox layout = new HBox();
        layout.getChildren().addAll(leftSide, rightSide);
        appPane.getChildren().add(layout);

    }

    private void setWorkspaceActions() {
        textArea.setOnKeyTyped(e -> handleTextRequest());
        runButton.setOnAction(e -> handleDisplayRequest());
        doneButton.setOnAction(e -> handleDoneRequest());
        editButton.setOnAction(e -> handleEditRequest());
        classButton.setOnAction(e -> handleClassAlgButton());
        clussButton.setOnAction(e -> handleClussAlgButton());
    }
    
    public void handleClassAlgButton(){
        System.out.println("you pressed classification");
    }
    
    public void handleClussAlgButton(){
        System.out.println("you pressed clusstering ");
    }
    
    public void updateDataInfo(int instances, int labels, String fileName, ArrayList<String> list) {
        StringBuffer labelText = new StringBuffer();
        labelText.append(instances + " instances with " + labels + " labels loaded from " + fileName + ". The labels are:" + "\n");
        for (String label : list) {
            labelText.append("- " + label + "\n");
        }
        dataInfo.setVisible(true);
        dataInfo.setAlignment(Pos.TOP_LEFT);
        dataInfo.setText(labelText.toString());
        dataInfo.setPrefHeight(windowHeight * .4);
        classButton.setVisible(true);
        classButton.setDisable(false);
        classButton.setManaged(true);
        clussButton.setVisible(true);
        clussButton.setDisable(false);
        clussButton.setManaged(true);
        if (labels != 2) {
            classButton.setDisable(true);
        } else if (labels == 2) {
            classButton.setDisable(false);
        }

    }

    public void enableUIOnLoad() {
        dataInfo.setVisible(true);
        textArea.setVisible(true);
        doneButton.setVisible(false);
        doneButton.setManaged(false);
        doneButton.setDisable(true);
        editButton.setVisible(false);
        editButton.setManaged(false);
        textArea.setDisable(true);
    }

    public void enableUIOnNew() {
        this.enableUIOnLoad();
        doneButton.setVisible(true);
        doneButton.setManaged(true);
        doneButton.setDisable(false);
        editButton.setVisible(true);
        editButton.setManaged(true);
        textArea.setDisable(false);
    }

    private void handleDoneRequest() {
        TSDProcessor tsdProcessor = new TSDProcessor();
        textArea.setDisable(true);
        doneButton.setDisable(true);
        editButton.setDisable(false);
        try {
            tsdProcessor.processString(textArea.getText());
        } catch (Exception e) {
            e.getMessage();
        }
        this.updateDataInfo(tsdProcessor.getNameList().size(), tsdProcessor.getUniqueNames().size(), "text area", tsdProcessor.getUniqueNames());
    }

    private void handleEditRequest() {
        textArea.setDisable(false);
        editButton.setDisable(true);
        doneButton.setDisable(false);
    }

    private void handleTextRequest() {
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
        avg.setName(applicationTemplate.manager.getPropertyValue("AVG_LINE_NAME"));
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
            ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("REP_DATA_TITLE"), applicationTemplate.manager.getPropertyValue("REP_DATA_MESSAGE"));

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
                ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
            }
        }
    }

    public void disableSave() {
        saveButton.setDisable(true);
    }
}
