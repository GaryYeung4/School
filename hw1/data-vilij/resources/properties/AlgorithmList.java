/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package properties;

import java.util.ArrayList;

/**
 *
 * @author garyy
 */
public class AlgorithmList {
    private final ArrayList<String> classificationAlgorithms = new ArrayList<>();
    private final ArrayList<String> clusteringAlgorithms = new ArrayList<>();
    
    public AlgorithmList(){
        classificationAlgorithms.add("Random Classifier");
        clusteringAlgorithms.add("Random Clusterer");
        clusteringAlgorithms.add("KMeans Clusterer");
    }

}
