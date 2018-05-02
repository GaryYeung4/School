/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cluster;

import algorithm.Clusterer;
import algorithm.DataSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.geometry.Point2D;

/**
 *
 * @author garyy
 */
public class RandomClusterer extends Clusterer{
    
    private DataSet       dataset;
    private List<Point2D> centroids;
    private List<String> labels;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;




    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, boolean cont, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(cont);
    }
    
    @Override
    public int getMaxIterations() {
        return this.maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return this.tocontinue.get();
    }

    @Override
    public void run() {
        Random rand = new Random();
        Map<String, String> userLabels = dataset.getLabels();
        Set<String> keys = userLabels.keySet();
        for(String key: keys){
            dataset.updateLabel(key, Integer.toString((rand.nextInt(super.getNumberOfClusters()))));
        }
        
        
    }
    public Map<String, String> getLabels(){
        return dataset.getLabels();
    }
    
}
