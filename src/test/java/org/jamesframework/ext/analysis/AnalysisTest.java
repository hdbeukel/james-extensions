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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import mjson.Json;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.algo.RandomSearch;
import org.jamesframework.core.search.algo.exh.ExhaustiveSearch;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.algo.exh.SubsetSolutionIterator;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.test.fakes.ScoredFakeSubsetData;
import org.jamesframework.test.fakes.SumOfScoresFakeSubsetObjective;
import org.jamesframework.test.util.DoubleComparatorWithPrecision;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test analysis.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class AnalysisTest {
    
    private Analysis<SubsetSolution> analysis;
    
    @Before
    public void setup(){
        analysis = new Analysis<>();
    }
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing Analysis ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing Analysis!");
    }

    @Test
    public void testGetNumRuns() {
        
        System.out.println(" - test getNumRuns");
        
        assertEquals(10, analysis.getNumRuns());
        
    }
    
    @Test
    public void testSetNumRuns() {
        
        System.out.println(" - test setNumRuns");
        
        boolean thrown = false;
        try {
            analysis.setNumRuns(-1);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            analysis.setNumRuns(0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        analysis.setNumRuns(123);
        assertEquals(123, analysis.getNumRuns());
        
    }
    
    @Test
    public void testGetNumRunsForSearch() {
        
        System.out.println(" - test getNumRuns for specific search");
        
        boolean thrown = false;
        try {
            analysis.getNumRuns("i-do-not-exist");
        } catch (UnknownIDException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // add a search
        analysis.addSearch("abc", p -> new RandomSearch<>(p));
        
        // verify: automatically adopts global run count
        assertEquals(10, analysis.getNumRuns("abc"));
        
    }
    
    @Test
    public void testSetNumRunsForSearch() {
        
        System.out.println(" - test setNumRuns for specific search");
        
        boolean thrown = false;
        try {
            analysis.setNumRuns("i-do-not-exist", 100);
        } catch (UnknownIDException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // add a search
        analysis.addSearch("abc", p -> new ExhaustiveSearch<>(p, null));
        
        // try invalid run counts
        thrown = false;
        try {
            analysis.setNumRuns("abc", -1);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            analysis.setNumRuns("abc", 0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // set specific run count
        analysis.setNumRuns("abc", 1);
        
        // verify
        assertEquals(1, analysis.getNumRuns("abc"));
        
        // check global setting not affected
        assertEquals(10, analysis.getNumRuns());
        
        // add second search
        analysis.addSearch("xyz", p -> new RandomSearch<>(p));
        // check: adopts global run count
        assertEquals(10, analysis.getNumRuns("xyz"));
        
    }
    
    @Test
    public void testGetNumBurnIn() {
        
        System.out.println(" - test getNumBurnIn");
        
        assertEquals(1, analysis.getNumBurnIn());
        
    }
    
    @Test
    public void testSetNumBurnIn() {
        
        System.out.println(" - test setNumBurnIn");
        
        boolean thrown = false;
        try {
            analysis.setNumBurnIn(-1);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            analysis.setNumBurnIn(0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        analysis.setNumBurnIn(123);
        assertEquals(123, analysis.getNumBurnIn());
        
    }
    
    @Test
    public void testGetNumBurnInForSearch() {
        
        System.out.println(" - test getNumBurnIn for specific search");
        
        boolean thrown = false;
        try {
            analysis.getNumBurnIn("i-do-not-exist");
        } catch (UnknownIDException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // add a search
        analysis.addSearch("abc", p -> new RandomSearch<>(p));
        
        // verify: automatically adopts global run count
        assertEquals(1, analysis.getNumBurnIn("abc"));
        
    }
    
    @Test
    public void testSetNumBurnInForSearch() {
        
        System.out.println(" - test setNumBurnIn for specific search");
        
        boolean thrown = false;
        try {
            analysis.setNumBurnIn("i-do-not-exist", 100);
        } catch (UnknownIDException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // add a search
        analysis.addSearch("abc", p -> new ExhaustiveSearch<>(p, null));
        
        // try invalid run counts
        thrown = false;
        try {
            analysis.setNumBurnIn("abc", -1);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try {
            analysis.setNumBurnIn("abc", 0);
        } catch (IllegalArgumentException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // set specific run count
        analysis.setNumBurnIn("abc", 5);
        
        // verify
        assertEquals(5, analysis.getNumBurnIn("abc"));
        
        // check global setting not affected
        assertEquals(1, analysis.getNumBurnIn());
        
        // add second search
        analysis.addSearch("xyz", p -> new RandomSearch<>(p));
        // check: adopts global run count
        assertEquals(1, analysis.getNumBurnIn("xyz"));
        
    }
    
    @Test
    public void testAddProblem() {
        
        System.out.println(" - test addProblem");
        
        // try to add null problem
        boolean thrown = false;
        try {
            analysis.addProblem("problem", null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // create subset problem
        SumOfScoresFakeSubsetObjective obj = new SumOfScoresFakeSubsetObjective();
        ScoredFakeSubsetData data = new ScoredFakeSubsetData(new double[]{0.1,0.2,0.3,0.4,0.5});
        SubsetProblem<ScoredFakeSubsetData> problem = new SubsetProblem<>(obj, data, 3);
        
        // add problem to analysis
        analysis.addProblem("problem", problem);
        
        // try to add second problem with same ID
        thrown = false;
        try {
            analysis.addProblem("problem", problem);
        } catch (DuplicateIDException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    @Test
    public void testAddSearch() {
        
        System.out.println(" - test addSearch");
        
        // try to add null search factory
        boolean thrown = false;
        try {
            analysis.addSearch("search", null);
        } catch (NullPointerException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
        // add search
        analysis.addSearch("search", p -> new RandomSearch<>(p));
        
        // try to add second search with same ID
        thrown = false;
        try {
            analysis.addSearch("search", p -> new RandomSearch<>(p));
        } catch (DuplicateIDException ex){
            thrown = true;
        }
        assertTrue(thrown);
        
    }
    
    @Test
    public void testRun() throws IOException {
        
        System.out.println(" - test run");
        
        // create objective
        SumOfScoresFakeSubsetObjective obj = new SumOfScoresFakeSubsetObjective();
        
        // create subset problems with different data sets
        ScoredFakeSubsetData data1 = new ScoredFakeSubsetData(new double[]{0.1,0.2,0.3,0.4,0.5});
        SubsetProblem<ScoredFakeSubsetData> p1 = new SubsetProblem<>(obj, data1, 3);
        
        ScoredFakeSubsetData data2 = new ScoredFakeSubsetData(new double[]{4.1,1.2,5.3,8.4,2.5,12.2,0.4});
        SubsetProblem<ScoredFakeSubsetData> p2 = new SubsetProblem<>(obj, data2, 4);
        
        // add problems to analysis
        analysis.addProblem("small", p1);
        analysis.addProblem("larger", p2);
                
        // add exhaustive search
        analysis.addSearch("exh", p -> {
            // cast to subset problem
            SubsetProblem<?> sp = (SubsetProblem<?>) p;
            // get IDs and subset size bounds
            Set<Integer> ids = sp.getData().getIDs();
            int minSize = sp.getMinSubsetSize();
            int maxSize = sp.getMaxSubsetSize();
            // create solution iterator
            SubsetSolutionIterator it = new SubsetSolutionIterator(ids, minSize, maxSize);
            // create and return exhaustive search
            return new ExhaustiveSearch<>(p, it);
        });
        // run exhaustive search only once
        analysis.setNumRuns("exh", 1);
        
        // add random descent (default number of runs; i.e. 10 - run for 500 milliseconds each)
        analysis.addSearch("random.descent", p -> {
            Search<SubsetSolution> s = new RandomDescent<>(p, new SingleSwapNeighbourhood());
            s.addStopCriterion(new MaxRuntime(500, TimeUnit.MILLISECONDS));
            return s;
        });
        
        // run the analysis
        AnalysisResults<SubsetSolution> results = analysis.run();
        
        // check results
        
        assertEquals(2, results.getNumProblems());
        assertEquals(new HashSet<>(Arrays.asList("small", "larger")), results.getProblemIDs());
        assertEquals(2, results.getNumSearches("small"));
        assertEquals(2, results.getNumSearches("larger"));
        assertEquals(new HashSet<>(Arrays.asList("exh", "random.descent")), results.getSearchIDs("small"));
        assertEquals(new HashSet<>(Arrays.asList("exh", "random.descent")), results.getSearchIDs("larger"));
        
        assertEquals(1, results.getNumRuns("small", "exh"));
        assertEquals(10, results.getNumRuns("small", "random.descent"));
        assertEquals(1, results.getNumRuns("larger", "exh"));
        assertEquals(10, results.getNumRuns("larger", "random.descent"));
        
        check(results, "small", "exh", 3);
        check(results, "small", "random.descent", 3);
        check(results, "larger", "exh", 4);
        check(results, "larger", "random.descent", 4);
        
        // results.writeJSON("test.json", s -> Json.array(s.getSelectedIDs().toArray()));
        
    }
    
    private void check(AnalysisResults<SubsetSolution> results, String problem, String search, int subsetSize){
        int numRuns = results.getNumRuns(problem, search);
        for(int r=0; r<numRuns; r++){
            List<BestSolutionUpdate<SubsetSolution>> updates = results.getRun(problem, search, r);
            // check
            for(int u=0; u<updates.size(); u++){
                BestSolutionUpdate<SubsetSolution> cur, prev;
                cur = updates.get(u);
                if(u >= 1){
                    prev = updates.get(u-1);
                    assertTrue(cur.getTime() >= prev.getTime());
                    assertTrue(DoubleComparatorWithPrecision.greaterThanOrEqual(
                                    cur.getValue(),
                                    prev.getValue(),
                                    TestConstants.DOUBLE_COMPARISON_PRECISION)
                    );
                }
                assertEquals(subsetSize, cur.getSolution().getNumSelectedIDs());
            }
        }
    }

}