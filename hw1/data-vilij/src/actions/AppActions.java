package actions;

import dataprocessors.TSDProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.components.ConfirmationDialog.Option;
import static vilij.components.ConfirmationDialog.Option.CANCEL;
import static vilij.components.ConfirmationDialog.Option.YES;
import vilij.components.ErrorDialog;
import vilij.templates.UITemplate;

/**
 * This is the concrete implementation of the action handlers required by the
 * application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;

    /**
     * Path to the data file currently active.
     */
    public boolean saveState;
    private StringBuffer loadedData;
    private int lineNumber;
    private File sameFile;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    private void setSaveState(boolean prop) {
        saveState = prop;
    }

    @Override
    public void handleNewRequest() {
        AppUI appUI = (AppUI) (UITemplate) applicationTemplate.getUIComponent();
        appUI.clearLeftSide();
        appUI.enableUIOnNew();
        if (appUI.getText().getText().isEmpty()) {
            return;
        } else {
            try {
                if (saveState) {
                    applicationTemplate.getUIComponent().clear();
                    appUI.disableSave();
                    sameFile = null;
                    this.setSaveState(false);
                } else {
                    try {
                        if (promptToSave()) {

                            applicationTemplate.getUIComponent().clear();
                            appUI.disableSave();
                            sameFile = null;
                            this.setSaveState(false);
                        }
                    } catch (Exception e) {
                        ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void handleSaveRequest() {
        AppUI appUI = (AppUI) (UITemplate) applicationTemplate.getUIComponent();
        try {
            this.promptToSave();
        } catch (Exception e) {
            appUI.disableSave();
            ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
        }
    }

    @Override
    public void handleLoadRequest() {
        TSDProcessor tsdprocessor = new TSDProcessor();
        lineNumber = 0;
        loadedData = new StringBuffer();
        AppUI appUI = (AppUI) (UITemplate) applicationTemplate.getUIComponent();
        appUI.clearLeftSide();
        appUI.enableUIOnLoad();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(applicationTemplate.manager.getPropertyValue("DATA_RESOURCE_PATH")));
        File chosenFile = fc.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        try {
            if (chosenFile != null) {

                BufferedReader br = new BufferedReader(new FileReader(chosenFile));
                String line;
                while ((line = br.readLine()) != null) {
                    ++lineNumber;
                    loadedData.append(line + "\n");
                }
                tsdprocessor.processString(loadedData.toString());
                appUI.getText().setText(loadedData.toString());
                appUI.updateDataInfo(lineNumber, tsdprocessor.getUniqueNames().size(), chosenFile.getPath(), tsdprocessor.getUniqueNames());

            }
        } catch (Exception e) {
            ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
        }
        lineNumber = 0;
        loadedData.delete(0, loadedData.length());
    }

    @Override
    public void handleExitRequest() {
        AppUI appUI = (AppUI) (UITemplate) applicationTemplate.getUIComponent();
        try {
            if (saveState) {
                
            } else {
                try {
                    if (promptToSave()) {
                        appUI.disableSave();
                        sameFile = null;
                        this.setSaveState(false);
                    }
                } catch (Exception e) {
                    ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (appUI.getAlgState()) {
            ConfirmationDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("EXIT_WHILE_RUNNING_WARNING_TITLE"), applicationTemplate.manager.getPropertyValue("EXIT_WHILE_RUNNING_WARNING"));
            Option userResponse = ConfirmationDialog.getDialog().getSelectedOption();
            if (userResponse == YES) {
                System.exit(0);
            } else {
                return;
            }
        }
        System.exit(0);
    }

    @Override
    public void handlePrintRequest() {
    }

    public void handleScreenshotRequest() throws IOException {

        AppUI appUI = (AppUI) (UITemplate) applicationTemplate.getUIComponent();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(applicationTemplate.manager.getPropertyValue("DATA_RESOURCE_PATH")));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("png", ".png"));
        File image = fc.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        if (image != null) {
            try {
                WritableImage pic = new WritableImage((int) appUI.getChart().getWidth(), (int) appUI.getChart().getHeight());
                appUI.getChart().snapshot(null, pic);
                ImageIO.write(SwingFXUtils.fromFXImage(pic, null), "png", image);
            } catch (Exception e) {
                e.getMessage();
            }
        }

    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. The user will be presented
     * with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and
     * continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the
     * action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to
     * continue with the action, but also does not want to save the work at this
     * point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and
     * <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        AppUI appUI = (AppUI) (UITemplate) applicationTemplate.getUIComponent();
        if (saveState) {
            FileWriter fw = new FileWriter(sameFile);
            fw.write(appUI.getText().getText());
            fw.close();
            appUI.disableSave();
        } else {
            ConfirmationDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("SAVE_UNSAVED_WORK_TITLE"), applicationTemplate.manager.getPropertyValue("SAVE_UNSAVED_WORK"));
            Option userResponse = ConfirmationDialog.getDialog().getSelectedOption();
            if (userResponse == CANCEL) {
                this.setSaveState(false);
                return false;
            }
            if (userResponse == YES) {
                TSDProcessor tsdp = new TSDProcessor();
                try {
                    tsdp.processString(appUI.getText().getText());
                } catch (Exception e) {
                    appUI.disableSave();
                    ErrorDialog.getDialog().show(applicationTemplate.manager.getPropertyValue("DATA_INC_FORMAT_TITLE"), e.getLocalizedMessage());
                    this.setSaveState(false);
                    return false;
                }
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(applicationTemplate.manager.getPropertyValue("DATA_RESOURCE_PATH")));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue("DATA_FILE_EXT_DESC"), applicationTemplate.manager.getPropertyValue("DATA_FILE_EXT")));
                File savedData = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (savedData != null) {
                    try {
                        FileWriter fw = new FileWriter(savedData);
                        fw.write(appUI.getText().getText());
                        fw.close();
                        sameFile = savedData;
                        appUI.disableSave();
                        this.setSaveState(true);

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                return true;

            }
            this.setSaveState(false);
            return true;
        }
        return true;
    }
}
