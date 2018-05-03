/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author garyy
 */
public class ConfigScreenTest {
    
    public ConfigScreenTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getIterationCount method, of class ConfigScreen.
     */
    @Test
    public void testGetIterationCount() {
        System.out.println("getIterationCount");
        ConfigScreen instance = new ConfigScreen();
        //Tests if a valid input for iteration count is stored properly
        instance.processUserInputs("10", "5", true);
        int expResult = 10;
        int result = instance.getIterationCount();
        assertEquals(expResult, result);
        //Tests if letters on the alphabet will set iteration count to 1
        instance.processUserInputs("asd", "5", true);
        expResult = 1;
        result = instance.getIterationCount();
        assertEquals(expResult, result);
        //Tests if negative values will set iteration count to 1
        instance.processUserInputs("-40", "5", true);
        result = instance.getIterationCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUpdateInterval method, of class ConfigScreen.
     */
    @Test
    public void testGetUpdateInterval() {
        System.out.println("getUpdateInterval");
        ConfigScreen instance = new ConfigScreen();
        //Tests if a valid input for update interval is stored properly
        instance.processUserInputs("10", "5", true);
        int expResult = 5;
        int result = instance.getUpdateInterval();
        assertEquals(expResult, result);
        //Tests if letters on the alphabet will set iteration count to 1
        instance.processUserInputs("10", "asd", true);
        expResult = 1;
        result = instance.getUpdateInterval();
        assertEquals(expResult, result);
        //Tests if negative values will set iteration count to 1
        instance.processUserInputs("10", "-50", true);
        result = instance.getUpdateInterval();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContinueState method, of class ConfigScreen.
     */
    @Test
    public void testGetContinueState() {
        System.out.println("getContinueState");
        ConfigScreen instance = new ConfigScreen();
        //Tests that not wanting the program to be continuous is saved
        instance.processUserInputs("10", "5", false);
        boolean expResult = false;
        boolean result = instance.getContinueState();
        assertEquals(expResult, result);
        //Tests that wanting the program to be continuous is saved
        instance.processUserInputs("10", "5", true);
        expResult = true;
        result = instance.getContinueState();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getLabelCount method, of class ConfigScreen.
     */
    @Test
    public void testGetLabelCount() {
        System.out.println("getLabelCount");
        ConfigScreen instance = new ConfigScreen();
        //Tests if a valid input for label count is stored properly
        instance.processUserInputs("3", "10", "5", true);
        int expResult = 3;
        int result = instance.getLabelCount();
        assertEquals(expResult, result);
        //Tests if letters on the alphabet will set label count to 2
        instance.processUserInputs("asd", "10", "5", true);
        expResult = 2;
        result = instance.getLabelCount();
        assertEquals(expResult, result);
        //Tests if values less than 2 will set label count to 2
        instance.processUserInputs("1", "10", "5", true);
        result = instance.getLabelCount();
        assertEquals(expResult, result);
        //Tests if values greater than 4 will set the label count to 4
        instance.processUserInputs("5", "10", "5", true);
        expResult = 4;
        result = instance.getLabelCount();
        assertEquals(expResult, result);
    }

    
}
