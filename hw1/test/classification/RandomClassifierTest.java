/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

import algorithm.DataSet;
import java.util.ArrayList;
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
public class RandomClassifierTest {
    
    public RandomClassifierTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getMaxIterations method, of class RandomClassifier.
     */
    @Test
    public void testGetMaxIterations() {
        System.out.println("getMaxIterations");
        Map<String,String> labels = new LinkedHashMap<>();
        Map<String,Point2D> points = new LinkedHashMap<>();
        DataSet dataset = new DataSet(labels,points);
        //Tests if valid values of max iterations is used
        RandomClassifier instance = new RandomClassifier(dataset,10,5,true);
        int expResult = 10;
        int result = instance.getMaxIterations();
        assertEquals(expResult, result);
        //Tests if negative values will set iteration count to 1
        instance = new RandomClassifier(dataset, -40, 5, true);
        expResult = 1;
        result = instance.getMaxIterations();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUpdateInterval method, of class RandomClassifier.
     */
    @Test
    public void testGetUpdateInterval() {
        System.out.println("getUpdateInterval");
        Map<String,String> labels = new LinkedHashMap<>();
        Map<String,Point2D> points = new LinkedHashMap<>();
        DataSet dataset = new DataSet(labels,points);
        //Tests if valid values of update interval is used
        RandomClassifier instance = new RandomClassifier(dataset,10,5,true);
        int expResult = 5;
        int result = instance.getUpdateInterval();
        assertEquals(expResult, result);
        //Tests if negative values will set iteration count to 1
        instance = new RandomClassifier(dataset, 10, -5, true);
        expResult = 1;
        result = instance.getUpdateInterval();
        assertEquals(expResult, result);
    }

    /**
     * Test of tocontinue method, of class RandomClassifier.
     */
    @Test
    public void testTocontinue() {
        System.out.println("tocontinue");
        Map<String,String> labels = new LinkedHashMap<>();
        Map<String,Point2D> points = new LinkedHashMap<>();
        DataSet dataset = new DataSet(labels,points);
        //Tests if setting continueous to true is saved 
        RandomClassifier instance = new RandomClassifier(dataset,10,5,true);
        boolean expResult = true;
        boolean result = instance.tocontinue();
        assertEquals(expResult, result);
        instance = new RandomClassifier(dataset, 10, 5, false);
        expResult = false;
        result = instance.tocontinue();
        assertEquals(expResult, result);
    }

    
}
