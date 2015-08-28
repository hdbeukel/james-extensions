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
package org.jamesframework.ext.problems.objectives;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.sol.Solution;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.AdditionMove;
import org.jamesframework.test.fakes.SumOfIDsFakeSubsetObjective;
import org.jamesframework.test.stubs.FixedEvaluationObjectiveStub;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * Test normalized objective.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class NormalizedObjectiveTest {
    
    /**
     * Print message before running tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing NormalizedObjective ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing NormalizedObjective!");
    }
    
    /**
     * Test of getUnnormalizedObjective method, of class NormalizedObjective.
     */
    @Test
    public void testGetUnnormalizedObjective() {
        
        System.out.println(" - test getUnnormalizedObjective");
        
        double min = 5, max = 8, val = 7.5;
        
        FixedEvaluationObjectiveStub orig = new FixedEvaluationObjectiveStub(val);
        NormalizedObjective<Solution, Object> normalized = new NormalizedObjective<>(orig, min, max);
        
        assertEquals(orig, normalized.getUnnormalizedObjective());
        
    }

    /**
     * Test of getMin method, of class NormalizedObjective.
     */
    @Test
    public void testGetMin() {
       
        System.out.println(" - test getMin");
        
        double min = 5, max = 8, val = 7.5;
        
        FixedEvaluationObjectiveStub orig = new FixedEvaluationObjectiveStub(val);
        NormalizedObjective<Solution, Object> normalized = new NormalizedObjective<>(orig, min, max);
        
        assertEquals(min, normalized.getMin(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of getMax method, of class NormalizedObjective.
     */
    @Test
    public void testGetMax() {
        
        System.out.println(" - test getMax");
        
        double min = 5, max = 8, val = 7.5;
        
        FixedEvaluationObjectiveStub orig = new FixedEvaluationObjectiveStub(val);
        NormalizedObjective<Solution, Object> normalized = new NormalizedObjective<>(orig, min, max);
        
        assertEquals(max, normalized.getMax(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of evaluate method, of class NormalizedObjective.
     */
    @Test
    public void testEvaluate() {
        
        System.out.println(" - test evaluate");
        
        double min, max, val = 7.5;
        
        NormalizedObjective<SubsetSolution, Object> normalized;

        SubsetSolution sol = new SubsetSolution(
                                IntStream.rangeClosed(1, 10)
                                         .boxed()
                                         .collect(Collectors.toSet())
        );

        FixedEvaluationObjectiveStub orig1 = new FixedEvaluationObjectiveStub(val);
        
        min = val;
        max = 2*val;
        normalized = new NormalizedObjective<>(orig1, min, max);
        assertEquals(0, normalized.evaluate(sol, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-1;
        max = val;
        normalized = new NormalizedObjective<>(orig1, min, max);
        assertEquals(1, normalized.evaluate(sol, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-2;
        max = val+2;
        normalized = new NormalizedObjective<>(orig1, min, max);
        assertEquals(0.5, normalized.evaluate(sol, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        SumOfIDsFakeSubsetObjective orig2 = new SumOfIDsFakeSubsetObjective();
        sol.select(3);
        sol.select(5);
        val = 8;
        
        min = val;
        max = 2*val;
        normalized = new NormalizedObjective<>(orig2, min, max);
        assertEquals(0, normalized.evaluate(sol, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-1;
        max = val;
        normalized = new NormalizedObjective<>(orig2, min, max);
        assertEquals(1, normalized.evaluate(sol, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-2;
        max = val+2;
        normalized = new NormalizedObjective<>(orig2, min, max);
        assertEquals(0.5, normalized.evaluate(sol, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
                
    }

    /**
     * Test of evaluate method, of class NormalizedObjective.
     */
    @Test
    public void testEvaluateMove() {
        
        System.out.println(" - test evaluate move");
        
        double min, max, val = 7.5;
        
        NormalizedObjective<SubsetSolution, Object> normalized;

        SubsetSolution sol = new SubsetSolution(
                                IntStream.rangeClosed(1, 10)
                                         .boxed()
                                         .collect(Collectors.toSet())
        );
        AdditionMove move = new AdditionMove(4);

        FixedEvaluationObjectiveStub orig1 = new FixedEvaluationObjectiveStub(val);
        Evaluation curEval;
        
        min = val;
        max = 2*val;
        normalized = new NormalizedObjective<>(orig1, min, max);
        curEval = normalized.evaluate(sol, null);
        assertEquals(0, normalized.evaluate(move, sol, curEval, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-1;
        max = val;
        normalized = new NormalizedObjective<>(orig1, min, max);
        curEval = normalized.evaluate(sol, null);
        assertEquals(1, normalized.evaluate(move, sol, curEval, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-2;
        max = val+2;
        normalized = new NormalizedObjective<>(orig1, min, max);
        curEval = normalized.evaluate(sol, null);
        assertEquals(0.5, normalized.evaluate(move, sol, curEval, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        SumOfIDsFakeSubsetObjective orig2 = new SumOfIDsFakeSubsetObjective();
        sol.select(3);
        sol.select(5);
        val = 8;
        
        min = val;
        max = 2*val;
        normalized = new NormalizedObjective<>(orig2, min, max);
        curEval = normalized.evaluate(sol, null);
        assertEquals(0.5, normalized.evaluate(move, sol, curEval, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-1;
        max = val;
        normalized = new NormalizedObjective<>(orig2, min, max);
        curEval = normalized.evaluate(sol, null);
        assertEquals(5.0, normalized.evaluate(move, sol, curEval, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        min = val-2;
        max = val+2;
        normalized = new NormalizedObjective<>(orig2, min, max);
        curEval = normalized.evaluate(sol, null);
        assertEquals(1.5, normalized.evaluate(move, sol, curEval, null).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }

    /**
     * Test of isMinimizing method, of class NormalizedObjective.
     */
    @Test
    public void testIsMinimizing() {
        
        System.out.println(" - test isMinimizing");
                
        double min = 5, max = 8, val = 7.5;
        
        FixedEvaluationObjectiveStub orig = new FixedEvaluationObjectiveStub(val);
        NormalizedObjective<Solution, Object> normalized = new NormalizedObjective<>(orig, min, max);
        
        orig.setMaximizing();
        assertEquals(false, normalized.isMinimizing());
        
        orig.setMinimizing();
        assertEquals(true, normalized.isMinimizing());
        
    }
    
}
