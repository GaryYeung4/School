/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

/**
 *
 * @author Gary Yeung and Ritwik Banerjee
 */
import algorithm.Classifier;
import algorithm.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;
    private Class AppUI;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
            int maxIterations,
            int updateInterval,
            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }

    public ArrayList<Integer> getDataOutput() {
        return (ArrayList<Integer>) output;
    }

    @Override
    public void run() {
        int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
        int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
        int constantVal = new Double(RAND.nextDouble() * 100).intValue();
        // this is the real output of the classifier

        output = (List<Integer>) new ArrayList<Integer>();
        Integer xCoeff = new Integer(xCoefficient);
        Integer yCoeff = new Integer(yCoefficient);
        Integer constantValue = new Integer(constantVal);
        output.add(xCoeff);
        output.add(yCoeff);
        output.add(constantValue);
        //output = Arrays.asList(xCoefficient, yCoefficient, constant);
        // everything below is just for internal viewing of how the output is changing
        // in the final project, such changes will be dynamically visible in the UI
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /**
     * A placeholder main method to just make sure this code runs smoothly
     */
    public static void main(String... args) throws IOException {
        DataSet dataset = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }
}
