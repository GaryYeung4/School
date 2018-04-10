package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {
    
    //Algorithm Selection Text
    ALGORITHM_SELECTION_PROMPT,

    //line name
    AVG_LINE_NAME,

    /* resource files and folders */
    DATA_RESOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    REP_DATA_TITLE,
    DATA_INC_FORMAT_TITLE,
    DATA_TOO_MANY_LINES_TITLE,
    DATA_TOO_MANY_LINES_MESSAGE,
    REP_DATA_MESSAGE,
    

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE
    
}
