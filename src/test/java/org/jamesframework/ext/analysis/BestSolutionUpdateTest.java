/*
 * Copyright 2015 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jamesframework.ext.analysis;

import java.util.Arrays;
import java.util.HashSet;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test best solution update.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class BestSolutionUpdateTest {

    private BestSolutionUpdate<SubsetSolution> u;
    private long time;
    private double value;
    private SubsetSolution sol;
    
    @Before
    public void setup(){
        time = 12345;
        value = 123.45;
        sol = new SubsetSolution(new HashSet<>(Arrays.asList(1,2,3,4,5)), new HashSet<>(Arrays.asList(2,5)));
        u = new BestSolutionUpdate<>(time, value, sol);
    }
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing BestSolutionUpdate ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing BestSolutionUpdate!");
    }

    /**
     * Test of getTime method, of class BestSolutionUpdate.
     */
    @Test
    public void testGetTime() {
        
        System.out.println(" - test getTime");
        
        assertEquals(time, u.getTime());
        
    }

    /**
     * Test of getValue method, of class BestSolutionUpdate.
     */
    @Test
    public void testGetValue() {
        
        System.out.println(" - test getValue");
        
        assertEquals(value, u.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of getSolution method, of class BestSolutionUpdate.
     */
    @Test
    public void testGetSolution() {
        
        System.out.println(" - test getSolution");
        
        assertEquals(sol, u.getSolution());
        
    }

}