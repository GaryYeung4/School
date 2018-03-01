package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

/**
 * The data files used by this data visualization applications follow a
 * tab-separated format, where each data point is named, labeled, and has a
 * specific location in the 2-dimensional X-Y plane. This class handles the
 * parsing and processing of such data. It also handles exporting the data to a
 * 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's
 * <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private Map<String, String> dataLabels;
    private Map<String, Point2D> dataPoints;
    private int lineNumber;
    private ArrayList<Double> xCoords;
    private ArrayList<Double> yCoords;
    private ArrayList<String> names;

    public TSDProcessor() {
        dataLabels = new LinkedHashMap<>();
        dataPoints = new LinkedHashMap<>();
        xCoords = new ArrayList<>();
        yCoords = new ArrayList<>();
        names = new ArrayList<>();
    }
    public ArrayList<String> getNameList(){
        return names;
    }
    public ArrayList<Double> getXCoords() {
        return xCoords;
    }

    public ArrayList<Double> getYCoords() {
        return yCoords;
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the
     * <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        lineNumber = 0;
        ArrayList<String> nameList = new ArrayList<String>();
        AtomicBoolean hadAnError = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        ++lineNumber;
                        double xVal;
                        double yVal;
                        String name = checkedname(list.get(0));
                        nameList.add(name);
                        names.add(name);
                        String label = list.get(1);
                        String[] pair = list.get(2).split(",");
                        xVal = Double.parseDouble(pair[0]);
                        yVal = Double.parseDouble(pair[1]);
                        Point2D point = new Point2D(xVal, yVal);
                        System.out.print(Double.parseDouble(pair[0]));
                        xCoords.add(xVal);
                        yCoords.add(yVal);
                        for (int i = 0; i < nameList.size(); ++i) {
                            for (int j = i + 1; j < nameList.size() - i; ++j) {
                                if ((nameList.get(i).compareTo(nameList.get(j))) == 0) {
                                    errorMessage.append("You had one or more names duplicated");
                                }
                            }
                        }
                        dataLabels.put(name, label);
                        dataPoints.put(name, point);
                    } catch (Exception e) {
                        errorMessage.setLength(0);
                        errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage()).append(" Error at line " + lineNumber);
                        hadAnError.set(true);
                    }
                });
        lineNumber = 0;
        if (errorMessage.length() > 0) {
            throw new Exception(errorMessage.toString());
        }
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    public void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new LinkedHashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
        }
    }


    public void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@")) {
            throw new InvalidDataNameException(name);
        }
        return name;
    }
}
