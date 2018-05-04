//Gary Yeung
package ui;

import actions.AppActions;
import algorithm.DataSet;
import classification.RandomClassifier;
import cluster.KMeansClusterer;
import cluster.RandomClusterer;
import dataprocessors.TSDProcessor;
import static java.io.File.separator;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import properties.AlgorithmList;
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
    private VBox algList;
    private ConfigScreen randClassConfScrn;
    private RandomClassifier randClass;
    private RandomClusterer randClus;
    private KMeansClusterer kMeansClus;
    private ConfigScreen randClussConfScrn;
    private ConfigScreen kMeansClussConfScrn;
    private int runCount;
    private boolean algRunning;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private String currAlg;

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
        textArea.setMinHeight(windowWidth * .2);
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
        algList = new VBox();
        randClassConfScrn = new ConfigScreen();
        randClussConfScrn = new ConfigScreen();
        kMeansClussConfScrn = new ConfigScreen();
        leftSide.getChildren().addAll(classButton, clussButton, algList);
        leftSide.setMaxWidth(windowWidth * .3);
        algRunning = false;
        //run button
        runButton = new Button("Run");
        runButton.setVisible(false);
        runButton.setDisable(true);
        runButton.setManaged(false);
        leftSide.getChildren().add(runButton);
        //chart
        VBox rightSide = new VBox();
        xAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis = new NumberAxis();
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.forceZeroInRangeProperty().setValue(false);
        yAxis.forceZeroInRangeProperty().setValue(false);
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Data Visualization");
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.getStylesheets().add(AppUI.class.getResource("ChartUI.css").toExternalForm());
        chart.setAnimated(false);
        rightSide.getChildren().add(chart);
        HBox layout = new HBox();
        layout.getChildren().addAll(leftSide, rightSide);
        appPane.getChildren().add(layout);

    }

    private void setWorkspaceActions() {
        textArea.setOnKeyTyped(e -> handleTextRequest());
        runButton.setOnAction(e -> handleRunRequest());
        doneButton.setOnAction(e -> handleDoneRequest());
        editButton.setOnAction(e -> handleEditRequest());
        classButton.setOnAction(e -> handleClassAlgButton());
        clussButton.setOnAction(e -> handleClussAlgButton());
    }

    public void handleClassAlgButton() {
        ArrayList<String> algs = new ArrayList<>();
        Field classificationList = null;
        AlgorithmList algorithmList = new AlgorithmList();
        try {
            classificationList = AlgorithmList.class.getDeclaredField("classificationAlgorithms");
            classificationList.setAccessible(true);
            try {
                algs = (ArrayList<String>) classificationList.get(algorithmList);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        algList.getChildren().remove(0, algList.getChildren().size());
        runButton.setDisable(true);
        runButton.setVisible(false);
        runButton.setManaged(false);
        ToggleGroup classAlgs = new ToggleGroup();
        for (int i = 0; i < algs.size(); ++i) {
            addAlgToUI(algs.get(i), classAlgs);
        }
        algList.setVisible(true);
        algList.setDisable(false);
        algList.setManaged(true);
    }

    public void handleClussAlgButton() {
        ArrayList<String> algs = new ArrayList<>();
        Field classificationList = null;
        AlgorithmList algorithmList = new AlgorithmList();
        try {
            classificationList = AlgorithmList.class.getDeclaredField("clusteringAlgorithms");
            classificationList.setAccessible(true);
            try {
                algs = (ArrayList<String>) classificationList.get(algorithmList);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        algList.getChildren().remove(0, algList.getChildren().size());
        algList.getChildren().remove(0, algList.getChildren().size());
        runButton.setDisable(true);
        runButton.setVisible(false);
        runButton.setManaged(false);
        ToggleGroup clussAlgs = new ToggleGroup();
        for (int i = 0; i < algs.size(); ++i) {
            addAlgToUI(algs.get(i), clussAlgs);
        }
        algList.setVisible(true);
        algList.setDisable(false);
        algList.setManaged(true);
    }

    private void addAlgToUI(String algName, ToggleGroup group) {
        HBox algBox = new HBox();
        RadioButton alg = new RadioButton();
        alg.setText(algName);
        alg.setToggleGroup(group);
        alg.setOnAction(e -> {
            runCount = 0;
            runButton.setVisible(true);
            runButton.setManaged(true);
            runButton.setDisable(true);
            currAlg = algName;
        });
        algBox.getChildren().add(alg);
        Button randClassSettings = new Button("Config");
        algBox.getChildren().add(randClassSettings);
        if (algName.equals("Random Classifier")) {
            randClassSettings.setOnAction(e -> {
                randClassConfScrn.showClassSettings();
                runButton.setDisable(false);
            });
        }
        if (algName.equals("Random Clusterer")) {
            randClassSettings.setOnAction(e -> {
                randClussConfScrn.showClusSettings();
                runButton.setDisable(false);
            });
        } else if (algName.equals("KMeans Clusterer")) {
            randClassSettings.setOnAction(e -> {
                kMeansClussConfScrn.showClusSettings();
                runButton.setDisable(false);
            });
        }
        algList.getChildren().add(algBox);
    }

    public void updateDataInfo(int instances, int labels, String fileName, ArrayList<String> list) {
        classButton.setSelected(false);
        classButton.setVisible(false);
        classButton.setDisable(true);
        classButton.setManaged(false);
        clussButton.setSelected(false);
        clussButton.setVisible(false);
        clussButton.setDisable(true);
        clussButton.setManaged(false);
        algList.setVisible(false);
        algList.setDisable(true);
        algList.setManaged(false);
        runButton.setVisible(false);
        runButton.setDisable(true);
        runButton.setManaged(false);
        StringBuffer labelText = new StringBuffer();
        labelText.append(instances + " instances with " + labels + " labels loaded from:" + "\n" + fileName + "\n The labels are:" + "\n");
        for (String label : list) {
            labelText.append("- " + label + "\n");
        }
        updateDataUI(labelText.toString(), labels);
    }

    private void updateDataUI(String dataInfoInput, int labels) {
        dataInfo.setVisible(true);
        dataInfo.setAlignment(Pos.TOP_LEFT);
        dataInfo.setText(dataInfoInput);
        dataInfo.setWrapText(true);
        dataInfo.setPrefHeight(windowHeight * .35);
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

    public void updateDataInfo(int instances, int labels, ArrayList<String> list) {
        classButton.setSelected(false);
        classButton.setVisible(false);
        classButton.setDisable(true);
        classButton.setManaged(false);
        clussButton.setSelected(false);
        clussButton.setVisible(false);
        clussButton.setDisable(true);
        clussButton.setManaged(false);
        algList.setVisible(false);
        algList.setDisable(true);
        algList.setManaged(false);
        runButton.setVisible(false);
        runButton.setDisable(true);
        runButton.setManaged(false);
        StringBuffer labelText = new StringBuffer();
        labelText.append(instances + " instances with " + labels + " label. The labels are:" + "\n");
        for (String label : list) {
            labelText.append("- " + label + "\n");
        }
        updateDataUI(labelText.toString(), labels);
    }

    public void clearLeftSide() {
        dataInfo.setText("");
        dataInfo.setVisible(false);
        dataInfo.setDisable(true);
        dataInfo.setManaged(false);
        classButton.setSelected(false);
        classButton.setVisible(false);
        classButton.setDisable(true);
        classButton.setManaged(false);
        clussButton.setSelected(false);
        clussButton.setVisible(false);
        clussButton.setDisable(true);
        clussButton.setManaged(false);
        algList.setVisible(false);
        algList.setDisable(true);
        algList.setManaged(false);
        runButton.setVisible(false);
        runButton.setDisable(true);
        runButton.setManaged(false);
    }

    public void enableUIOnLoad() {
        dataInfo.setVisible(true);
        dataInfo.setDisable(false);
        dataInfo.setManaged(true);
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
            ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
            classButton.setSelected(false);
            classButton.setVisible(false);
            classButton.setDisable(true);
            classButton.setManaged(false);
            clussButton.setSelected(false);
            clussButton.setVisible(false);
            clussButton.setDisable(true);
            clussButton.setManaged(false);
            algList.setVisible(false);
            algList.setDisable(true);
            algList.setManaged(false);
            runButton.setVisible(false);
            runButton.setDisable(true);
            runButton.setManaged(false);
            return;
        }
        this.updateDataInfo(tsdProcessor.getNameList().size(), tsdProcessor.getUniqueNames().size(), tsdProcessor.getUniqueNames());
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

    public void addClassifLine(TSDProcessor tsdprocessor, ArrayList<Integer> outputs) {
        ArrayList<Double> xCoords = tsdprocessor.getXCoords();
        ArrayList<Double> yCoords = tsdprocessor.getYCoords();
        double smallestX = xCoords.get(0);
        double largestX = xCoords.get(0);
        double newSmallY = yCoords.get(0);
        double newLargeY = yCoords.get(0);
        for (int i = 0; i < xCoords.size(); ++i) {
            double currVal = xCoords.get(i);
            double currYVal = yCoords.get(i);
            if (smallestX > currVal) {
                smallestX = currVal;
                newSmallY = currYVal;
            }
            if (largestX < currVal) {
                largestX = currVal;
                newLargeY = currYVal;
            }
        }
        newSmallY = ((outputs.get(2)) - (outputs.get(0) * smallestX)) / outputs.get(1);
        newLargeY = ((outputs.get(2)) - (outputs.get(0) * largestX)) / outputs.get(1);
        System.out.println("A value is " + outputs.get(0) + "B value is " + outputs.get(1) + "C value is " + outputs.get(2) + "x and y are " + smallestX + " " + newSmallY);
        System.out.println("Larger side is " + largestX + " " + newLargeY);
        XYChart.Series avg = new XYChart.Series<>();
        XYChart.Data<Number, Number> firstPoint = new XYChart.Data<>(smallestX, newSmallY);
        XYChart.Data<Number, Number> secondPoint = new XYChart.Data<>(largestX, newLargeY);
        avg.getData().add(firstPoint);
        avg.getData().add(secondPoint);
        avg.setName("Classification Line");
        Predicate<XYChart.Series> chartPredicate = (XYChart.Series s) -> s.getName().equals("Classification Line");
        chart.getData().removeIf(chartPredicate);
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

    private void runRandomClassifAlg(TSDProcessor tsdProcessor) {
        if (randClassConfScrn.getContinueState()) {
            runButton.setDisable(true);
            Thread algRunner = new Thread(() -> {
                algRunning = true;
                Random RAND = new Random();
                for (int i = 0; i < randClassConfScrn.getIterationCount(); ++i) {
                    if (i % randClassConfScrn.getUpdateInterval() == 0) {
                        Platform.runLater(() -> {
                            randClass.run();
                            ArrayList<Integer> output = randClass.getDataOutput();
                            addClassifLine(tsdProcessor, output);
                            System.out.println("unscaled Y");
                        });
                    }
                    if (i > randClassConfScrn.getIterationCount() * .6 && RAND.nextDouble() < 0.05) {
                        Platform.runLater(() -> {
                            randClass.run();
                            ArrayList<Integer> output = randClass.getDataOutput();
                            addClassifLine(tsdProcessor, output);
                        });
                        break;
                    }
                    if (i < (randClassConfScrn.getIterationCount()) - 1) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                Platform.runLater(() -> {
                    algorithmFinished();
                });
            });
            algRunner.start();
        } else {
            algRunning = true;
            scrnshotButton.setDisable(true);
            this.runNotContAlgorithm(tsdProcessor);
        }
    }

    private void runRandClusAlg(TSDProcessor tsdp) {
        if (randClussConfScrn.getContinueState()) {
            runButton.setDisable(true);
            Thread algRunner = new Thread(() -> {
                algRunning = true;
                Random RAND = new Random();
                for (int i = 0; i < randClussConfScrn.getIterationCount(); ++i) {
                    if (i % randClussConfScrn.getUpdateInterval() == 0) {
                        Platform.runLater(() -> {
                            chart.getData().clear();
                            randClus.run();
                            Map<String, String> newLabels = randClus.getLabels();
                            tsdp.setDataLabels(newLabels);
                            tsdp.toChartData(chart);

                        });
                    }
                    if (i < (randClussConfScrn.getIterationCount()) - 1) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                Platform.runLater(() -> {
                    algorithmFinished();
                });
            });
            algRunner.start();
        } else {
            algRunning = true;
            scrnshotButton.setDisable(true);
            this.runNotContRandClusAlgorithm(tsdp);
        }

    }

    private void runNotContRandClusAlgorithm(TSDProcessor tsdp) {
        ++runCount;
        Thread algRunner = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    runButton.setText("Continue");
                    chart.getData().clear();
                    randClus.run();
                    Map<String, String> newLabels = randClus.getLabels();
                    tsdp.setDataLabels(newLabels);
                    tsdp.toChartData(chart);
                    scrnshotButton.setDisable(false);
                    if ((runCount * randClussConfScrn.getUpdateInterval()) >= randClussConfScrn.getIterationCount()) {
                        runCount = 0;
                        runButton.setText("Run");
                        Platform.runLater(() -> {
                            algorithmFinished();
                        });
                    }

                });
            }
        });
        algRunner.start();

    }

    private void runKMeansAlg(TSDProcessor tsdp) {
        if (kMeansClussConfScrn.getContinueState()) {
            runButton.setDisable(true);
            Thread algRunner = new Thread(() -> {
                algRunning = true;
                Random RAND = new Random();
                for (int i = 0; i < kMeansClussConfScrn.getIterationCount(); ++i) {
                    if (i % kMeansClussConfScrn.getUpdateInterval() == 0) {
                        Platform.runLater(() -> {
                            chart.getData().clear();
                            kMeansClus.run();
                            Map<String, String> newLabels = kMeansClus.getDataLabels();
                            tsdp.setDataLabels(newLabels);
                            tsdp.toChartData(chart);

                        });
                    }
                    if (i < (kMeansClussConfScrn.getIterationCount()) - 1) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AppUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                Platform.runLater(() -> {
                    algorithmFinished();
                });
            });
            algRunner.start();
        } else {
            algRunning = true;
            scrnshotButton.setDisable(true);
            this.runNotContKMeansClusAlgorithm(tsdp);
        }

    }

    private void runNotContKMeansClusAlgorithm(TSDProcessor tsdp) {
        ++runCount;
        Thread algRunner = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    runButton.setText("Continue");
                    chart.getData().clear();
                    kMeansClus.run();
                    Map<String, String> newLabels = kMeansClus.getDataLabels();
                    tsdp.setDataLabels(newLabels);
                    tsdp.toChartData(chart);
                    scrnshotButton.setDisable(false);
                    if ((runCount * kMeansClussConfScrn.getUpdateInterval()) >= kMeansClussConfScrn.getIterationCount()) {
                        runCount = 0;
                        runButton.setText("Run");
                        Platform.runLater(() -> {
                            algorithmFinished();
                        });
                    }

                });
            }
        });
        algRunner.start();

    }

    private void handleRunRequest() {
        chart.getData().clear();
        yAxis.setAutoRanging(true);
        scrnshotButton.setDisable(true);
        String userInput = textArea.getText();
        if (newText.compareTo(userInput) == 0) {
            hasNewText = false;
        } else {
            hasNewText = true;
        }
        newText = userInput;
        TSDProcessor tsdProcessor = new TSDProcessor();
        try {
            System.out.println("Rescaled Y");
            tsdProcessor.processString(userInput);
            DataSet dataSet = new DataSet(tsdProcessor.getDataLabels(), tsdProcessor.getDataPoints());
            randClass = new RandomClassifier(dataSet, randClassConfScrn.getIterationCount(), randClassConfScrn.getUpdateInterval(), randClassConfScrn.getContinueState());
            randClus = new RandomClusterer(dataSet, randClussConfScrn.getIterationCount(), randClussConfScrn.getUpdateInterval(), randClussConfScrn.getContinueState(), randClussConfScrn.getLabelCount());
            kMeansClus = new KMeansClusterer(dataSet, randClussConfScrn.getIterationCount(), randClussConfScrn.getUpdateInterval(), randClussConfScrn.getContinueState(), randClussConfScrn.getLabelCount());
            if (currAlg.equals("Random Classifier")) {
                tsdProcessor.toChartData(chart);
                System.out.println("Running random classification algorithm");
                this.runRandomClassifAlg(tsdProcessor);
            }
            if (currAlg.equals("KMeans Clusterer")) {
                System.out.println("Running kmeans algorithm");
                this.runKMeansAlg(tsdProcessor);
            }
            if (currAlg.equals("Random Clusterer")) {
                this.runRandClusAlg(tsdProcessor);
                System.out.println("Running random clustering algorithm");
            }
            this.addNodeListeners();
        } catch (Exception e) {
            saveButton.setDisable(false);
            chart.getData().clear();
            ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
        }

    }

    private void runNotContAlgorithm(TSDProcessor tsdp) {
        ++runCount;
        Thread algRunner = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    runButton.setText("Continue");
                    randClass.run();
                    ArrayList<Integer> output = randClass.getDataOutput();
                    addClassifLine(tsdp, output);

                    scrnshotButton.setDisable(false);
                    if ((runCount * randClassConfScrn.getUpdateInterval()) >= randClassConfScrn.getIterationCount()) {
                        runCount = 0;
                        runButton.setText("Run");
                        Platform.runLater(() -> {
                            algorithmFinished();
                        });
                    }

                });
            }
        });
        algRunner.start();
    }

    private void algorithmFinished() {
        algRunning = false;
        scrnshotButton.setDisable(false);
        classButton.setSelected(false);
        clussButton.setSelected(false);
        algList.setVisible(false);
        algList.setDisable(true);
        algList.setManaged(false);
        runButton.setVisible(false);
        runButton.setDisable(true);
        runButton.setManaged(false);
        Alert algDone = new Alert(AlertType.INFORMATION);
        algDone.setTitle(applicationTemplate.manager.getPropertyValue("FINISHED_ALG_TITLE"));
        algDone.setHeaderText(applicationTemplate.manager.getPropertyValue("FINISHED_ALG_HEADER"));
        algDone.setContentText(applicationTemplate.manager.getPropertyValue("FINISHED_ALG_CONTENT"));
        algDone.showAndWait();

    }

    public boolean getAlgState() {
        return algRunning;
    }

    public void disableSave() {
        saveButton.setDisable(true);
    }
}
