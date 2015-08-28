/*
 * Copyright 2015 <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>.
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
package org.jamesframework.ext.problems.objectives.evaluations;

import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * Test normalized evaluation.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class NormalizedEvaluationTest {
    
    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing NormalizedEvaluation ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing NormalizedEvaluation!");
    }

    /**
     * Test of getValue method, of class NormalizedEvaluation.
     */
    @Test
    public void testGetValue() {
        
        System.out.println(" - test getValue");
        
        double min, max, val = 7.5;
        
        Evaluation orig = () -> val;
        NormalizedEvaluation normalized;
        
        min = val;
        max = 2*val;
        normalized = new NormalizedEvaluation(orig, min, max);
        assertEquals(0, normalized.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-1;
        max = val;
        normalized = new NormalizedEvaluation(orig, min, max);
        assertEquals(1, normalized.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-2;
        max = val+2;
        normalized = new NormalizedEvaluation(orig, min, max);
        assertEquals(0.5, normalized.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of getUnnormalizedEvaluation method, of class NormalizedEvaluation.
     */
    @Test
    public void testGetUnnormalizedEvaluation() {
        
        System.out.println(" - test getUnnormalizedEvaluation");
        
        double min = 5, max = 8, val = 7.5;
        
        Evaluation orig = () -> val;
        NormalizedEvaluation normalized = new NormalizedEvaluation(orig, min, max);
        
        assertEquals(orig, normalized.getUnnormalizedEvaluation());
        
    }
    
    @Test
    public void testToString(){
        
        System.out.println(" - test toString");
        
        double min = 5, max = 8, val = 7.5;
        
        Evaluation orig = () -> val;
        NormalizedEvaluation normalized = new NormalizedEvaluation(orig, min, max);
        
        assertEquals("0.8333333333333334 (unnormalized: 7.5)", normalized.toString());
        
    }
    
}
