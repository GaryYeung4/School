package ui;

import actions.AppActions;
import dataprocessors.TSDProcessor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import vilij.components.ErrorDialog;
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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button displayButton;  // workspace button to display data on the chart
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display

    public ScatterChart<Number, Number> getChart() {
        return chart;
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
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
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
    }

    private void layout() {

        Label dFile = new Label("Data File");
        appPane.getChildren().add(dFile);
        textArea = new TextArea();
        appPane.getChildren().add(textArea);
        displayButton = new Button("Display");
        appPane.getChildren().add(displayButton);
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new ScatterChart<Number, Number>(xAxis, yAxis);
        chart.setTitle("Data Visualization");
        appPane.getChildren().add(chart);

    }

    private void setWorkspaceActions() {
        textArea.setOnKeyTyped(e -> handleTextRequest());
        displayButton.setOnAction(e -> handleDisplayRequest());

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

    private void handleDisplayRequest() {
        String userInput = textArea.getText();
        chart.getData().clear();
        TSDProcessor tsdProcessor = new TSDProcessor();
        try {
            tsdProcessor.processString(userInput);
        } catch (Exception e) {
            ErrorDialog.getDialog().show("Error: Data Inputed Was in Incorrect Format", "Your data wasn't entered in the correct format. Please enter data in the correct format");
        }
        tsdProcessor.toChartData(chart);

    }
}
