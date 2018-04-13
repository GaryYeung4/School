/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.List;

/**
 *
 * @author Gary Yeung
 */
public abstract class Cluster implements Algorithm{
    protected List<Integer> output;
    public List<Integer> getOutput(){
        return output;
    }
    public abstract int getLabelNum();
}
