/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cluster;

import algorithm.DataSet;
import classification.RandomClassifier;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.geometry.Point2D;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author garyy
 */
public class KMeansClustererTest {

    public KMeansClustererTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getMaxIterations method, of class KMeansClusterer.
     */
    @Test
    public void testGetMaxIterations() {
        System.out.println("getMaxIterations");
        Map<String, String> labels = new LinkedHashMap<>();
        Map<String, Point2D> points = new LinkedHashMap<>();
        DataSet dataset = new DataSet(labels, points);
        //Tests if valid values of max iterations is used
        KMeansClusterer instance = new KMeansClusterer(dataset, 10, 5, true, 3);
        int expResult = 10;
        int result = instance.getMaxIterations();
        assertEquals(expResult, result);
        //Tests if negative values will set iteration count to 1
        instance = new KMeansClusterer(dataset, -40, 5, true, 3);
        expResult = 1;
        result = instance.getMaxIterations();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUpdateInterval method, of class KMeansClusterer.
     */
    @Test
    public void testGetUpdateInterval() {
        System.out.println("getUpdateInterval");
        Map<String, String> labels = new LinkedHashMap<>();
        Map<String, Point2D> points = new LinkedHashMap<>();
        DataSet dataset = new DataSet(labels, points);
        //Tests if valid values of update interval is used
        KMeansClusterer instance = new KMeansClusterer(dataset, 10, 5, true, 3);
        int expResult = 5;
        int result = instance.getUpdateInterval();
        assertEquals(expResult, result);
        //Tests if negative values will set iteration count to 1
        instance = new KMeansClusterer(dataset, 10, -5, true, 3);
        expResult = 1;
        result = instance.getUpdateInterval();
        assertEquals(expResult, result);
    }

    /**
     * Test of tocontinue method, of class KMeansClusterer.
     */
    @Test
    public void testTocontinue() {
        System.out.println("tocontinue");
        Map<String, String> labels = new LinkedHashMap<>();
        Map<String, Point2D> points = new LinkedHashMap<>();
        DataSet dataset = new DataSet(labels, points);
        //Tests if setting continueous to true is saved 
        KMeansClusterer instance = new KMeansClusterer(dataset, 10, 5, true, 3);
        boolean expResult = true;
        boolean result = instance.tocontinue();
        assertEquals(expResult, result);
        //Tests is setting continuous to false is saved
        instance = new KMeansClusterer(dataset, 10, 5, false, 3);
        expResult = false;
        result = instance.tocontinue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDataLabels method, of class KMeansClusterer.
     */
    @Test
    public void testGetDataLabels() {
        System.out.println("getDataLabels");
        Map<String, String> labels = new LinkedHashMap<>();
        Map<String, Point2D> points = new LinkedHashMap<>();
        DataSet dataset = new DataSet(labels, points);
        //Tests if valid values of data labels is saved 
        KMeansClusterer instance = new KMeansClusterer(dataset, 10, 5, true, 3);
        int expResult = 3;
        int result = instance.getNumberOfClusters();
        assertEquals(expResult, result);
        //Tests if label numbers less than 2 are set to 2
        instance = new KMeansClusterer(dataset, 10, 5, false, 1);
        expResult = 2;
        result = instance.getNumberOfClusters();
        assertEquals(expResult, result);    
        //Tests if label numbers more than 4 are set to 4
        instance = new KMeansClusterer(dataset, 10, 5, false, 5);
        expResult = 4;
        result = instance.getNumberOfClusters();
        assertEquals(expResult, result);    
    }

}
