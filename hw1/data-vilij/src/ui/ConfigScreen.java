/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author garyy
 */
public class ConfigScreen {

    private int iterationCount;
    private int updateInterval;
    private boolean continuous;
    private int labelCount;
    private Stage settings;
    private VBox window;

    public int getIterationCount() {
        return iterationCount;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public boolean getContinueState() {
        return continuous;
    }

    public int getLabelCount() {
        return labelCount;
    }

    public void showClassSettings() {
        settings = new Stage();
        settings.setTitle("Classification Configurations Window");
        window = new VBox();
        StackPane title = new StackPane();
        Label titleLabel = new Label("Algorithm Run Configuration");
        title.getChildren().add(titleLabel);
        window.getChildren().add(title);

        HBox itCount = new HBox();
        Label itCountDesc = new Label("Max. Iterations");
        TextArea itCountResp = new TextArea();
        try {
            itCountResp.setText(Integer.toString(this.iterationCount));
        } catch (Exception e) {
            e.getMessage();
        }
        itCountResp.setMaxHeight(50);
        itCountResp.setMaxWidth(150);
        itCount.getChildren().addAll(itCountDesc, itCountResp);
        window.getChildren().add(itCount);

        HBox updateInt = new HBox();
        Label updateIntDesc = new Label("Update Interval");
        TextArea updateIntResp = new TextArea();
        updateIntResp.setMaxHeight(50);
        updateIntResp.setMaxWidth(150);
        try {
            updateIntResp.setText(Integer.toString(this.updateInterval));
        } catch (Exception e) {
            e.getMessage();
        }
        updateInt.getChildren().addAll(updateIntDesc, updateIntResp);
        window.getChildren().add(updateInt);

        HBox contFlag = new HBox();
        Label contDesc = new Label("Continuous Run?");
        CheckBox contCheck = new CheckBox();
        if (this.continuous) {
            contCheck.setSelected(true);
        }
        contFlag.getChildren().addAll(contDesc, contCheck);
        window.getChildren().add(contFlag);

        Button set = new Button("Set");
        set.setOnAction(e -> {
            try {
                this.processUserInputs(itCountResp.getText(), updateIntResp.getText(), contCheck.isSelected());
                settings.hide();
            } catch (Exception f) {
            }
        });
        window.getChildren().add(set);

        Scene scene = new Scene(window);
        settings.setScene(scene);
        settings.setOnCloseRequest(e -> {
            this.processUserInputs(itCountResp.getText(), updateIntResp.getText(), contCheck.isSelected());
        });
        settings.show();
    }

    public void showClusSettings() {
        settings = new Stage();
        window = new VBox();
        settings.setTitle("Clustering Configurations Window");
        StackPane title = new StackPane();
        Label titleLabel = new Label("Algorithm Run Configuration");
        title.getChildren().add(titleLabel);
        window.getChildren().add(title);

        HBox itCount = new HBox();
        Label itCountDesc = new Label("Max. Iterations");
        TextArea itCountResp = new TextArea();
        itCountResp.setMaxHeight(50);
        itCountResp.setMaxWidth(150);
        try {
            itCountResp.setText(Integer.toString(this.iterationCount));
        } catch (Exception e) {
            e.getMessage();
        }
        itCount.getChildren().addAll(itCountDesc, itCountResp);
        window.getChildren().add(itCount);

        HBox updateInt = new HBox();
        Label updateIntDesc = new Label("Update Interval");
        TextArea updateIntResp = new TextArea();
        updateIntResp.setMaxHeight(50);
        updateIntResp.setMaxWidth(150);
        try {
            updateIntResp.setText(Integer.toString(this.updateInterval));
        } catch (Exception e) {
            e.getMessage();
        }
        updateInt.getChildren().addAll(updateIntDesc, updateIntResp);
        window.getChildren().add(updateInt);

        HBox labelNum = new HBox();
        Label labelNumDesc = new Label("Number of Labels");
        TextArea labelNumResp = new TextArea();
        labelNumResp.setMaxHeight(50);
        labelNumResp.setMaxWidth(150);
        try {
            labelNumResp.setText(Integer.toString(this.labelCount));
        } catch (Exception e) {
            e.getMessage();
        }
        labelNum.getChildren().addAll(labelNumDesc, labelNumResp);
        window.getChildren().add(labelNum);

        HBox contFlag = new HBox();
        Label contDesc = new Label("Continuous Run?");
        CheckBox contCheck = new CheckBox();
        contFlag.getChildren().addAll(contDesc, contCheck);
        if (this.continuous) {
            contCheck.setSelected(true);
        }
        window.getChildren().add(contFlag);

        Button set = new Button("Set");

        set.setOnAction(e -> {
            this.processUserInputs(labelNumResp.getText(), itCountResp.getText(), updateIntResp.getText(), contCheck.isSelected());
            settings.hide();
        });
        window.getChildren().add(set);

        Scene scene = new Scene(window);
        settings.setScene(scene);
        settings.setOnCloseRequest(e -> {
            this.processUserInputs(labelNumResp.getText(), itCountResp.getText(), updateIntResp.getText(), contCheck.isSelected());
        });
        settings.show();

    }

    protected void processUserInputs(String itCount, String updateInt, boolean cont) {
        try {
            this.iterationCount = Integer.parseInt(itCount);
            if (Integer.parseInt(itCount) <= 0) {
                this.iterationCount = 1;
            }
        } catch (Exception e) {
            this.iterationCount = 1;
        }
        try {
            this.updateInterval = Integer.parseInt(updateInt);
            if (Integer.parseInt(updateInt) <= 0) {
                this.updateInterval = 1;
            }
        } catch (Exception e) {
            this.updateInterval = 1;
        }
        this.continuous = cont;
    }

    protected void processUserInputs(String labelNum, String itCount, String updateInt, boolean cont) {
        try {
            this.labelCount = Integer.parseInt(labelNum);
            if (Integer.parseInt(labelNum) <= 1) {
                this.labelCount = 2;
            }
            if (Integer.parseInt(labelNum) >= 5) {
                System.out.println(Integer.parseInt(labelNum) + " was the label count");
                System.out.println("changing label count to 4");
                this.labelCount = 4;
            }
        } catch (Exception e) {
            this.labelCount = 2;
        }
        processUserInputs(itCount, updateInt, cont);
    }

}
