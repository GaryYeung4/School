package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;

import static vilij.settings.InitializationParams.*;


/**
 * The main class from which the application is run. The various components used here must be concrete implementations
 * of types defined in {@link vilij.components}.
 *
 * @author Ritwik Banerjee
 */
public final class DataVisualizer extends ApplicationTemplate {

    @Override
    public void start(Stage primaryStage) {
        /**GridPane pane = new GridPane();
        pane.setHgap(15);
        pane.setVgap(5);
        pane.add(new Label("Data File"),0,0);
        TextField dataInput = new TextField();
        dataInput.setMinSize(100, 100);
        pane.add(dataInput,0,1);
        pane.add(new Button("Display"), 0, 2);
        pane.add(new Label("Data Visualization"), 1, 0);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Display");
        primaryStage.show();
        **/
        
        dialogsAudit(primaryStage);
        if (propertyAudit()){
            userInterfaceAudit(primaryStage);
        }
    }

    @Override
    protected boolean propertyAudit() {
        boolean failed = manager == null || !(loadProperties(PROPERTIES_XML) && loadProperties(WORKSPACE_PROPERTIES_XML));
        if (failed)
            errorDialog.show(LOAD_ERROR_TITLE.getParameterName(), PROPERTIES_LOAD_ERROR_MESSAGE.getParameterName());
        return !failed;
    }

    @Override
    protected void userInterfaceAudit(Stage primaryStage) {
        setUIComponent(new AppUI(primaryStage, this));
        setActionComponent(new AppActions(this));
        setDataComponent(new AppData(this));

        uiComponent.initialize();
    }
    
    public static void main(String[] args) {
        launch();
    }

}
