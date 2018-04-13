/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cluster;

import algorithm.Cluster;
import algorithm.DataSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author garyy
 */
public class RandomClustering extends Cluster {
    private static final Random rand = new Random();
    private DataSet data;
    private final int labelNum;
    private final int maxIterations;
    private final int updateInt;
    private final AtomicBoolean toContinue;
    
    @Override
    public int getLabelNum() {
        return labelNum;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInt;
    }

    @Override
    public boolean tocontinue() {
        return toContinue.get();
    }
    public RandomClustering(DataSet data, int label, int maxIt, int updInt, boolean cont){
        this.data = data;
        this.labelNum = label;
        this.maxIterations = maxIt;
        this.updateInt = updInt;
        this.toContinue = new AtomicBoolean(cont);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
