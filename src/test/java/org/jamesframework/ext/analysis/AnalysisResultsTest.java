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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mjson.Json;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.test.util.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * Test analysis results.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class AnalysisResultsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private AnalysisResults<SubsetSolution> results;
    private Set<Integer> ids;
    
    @Before
    public void setup(){
        results = new AnalysisResults<>();
        // create set of all ids
        ids = new HashSet<>();
        for(int i=0; i<25; i++){
            ids.add(i);
        }
        List<BestSolutionUpdate<SubsetSolution>> updates;
        // create results of problem 0 - search 0 - run 0
        updates = new ArrayList<>();
        updates.add(new BestSolutionUpdate<>(
                            12,
                            0.334,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(3,6,1,7,19)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            333,
                            0.356,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,6,1,7,19)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            425,
                            0.398,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,6,1,2,19)))
                   ));
        // register in results
        results.registerSearchRun("problem-0", "search-0", updates);
        // create results of problem 0 - search 0 - run 1
        updates = new ArrayList<>();
        updates.add(new BestSolutionUpdate<>(
                            10,
                            0.312,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(8,6,7,3,19)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            246,
                            0.377,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,6,7,3,19)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            366,
                            0.396,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,6,7,2,19)))
                   ));
        // register in results
        results.registerSearchRun("problem-0", "search-0", updates);
        // create results of problem 0 - search 1 - run 0
        updates = new ArrayList<>();
        updates.add(new BestSolutionUpdate<>(
                            56,
                            0.333,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(3,12,2,22,16)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            523,
                            0.425,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,12,2,22,16)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            866,
                            0.553,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,12,1,22,16)))
                   ));
        // register in results
        results.registerSearchRun("problem-0", "search-1", updates);
        // create results of problem 1 - search 0 - run 0
        updates = new ArrayList<>();
        updates.add(new BestSolutionUpdate<>(
                            1,
                            0.1,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(1,2,3,4,5,6)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            2,
                            0.2,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(2,3,4,5,6,7)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            3,
                            0.3,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(3,4,5,6,7,8)))
                   ));
        // register in results
        results.registerSearchRun("problem-1", "search-0", updates);
    }
    
    /**
     * Print message when starting tests.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("# Testing AnalysisResults ...");
    }

    /**
     * Print message when tests are complete.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("# Done testing AnalysisResults!");
    }

    @Test
    public void testMerge(){
        
        System.out.println(" - test merge");
        
        // create other results object
        AnalysisResults<SubsetSolution> results2 = new AnalysisResults<>();
        // create results of a run of problem 0 - search 0
        List<BestSolutionUpdate<SubsetSolution>> updates = new ArrayList<>();
        updates.add(new BestSolutionUpdate<>(
                            8,
                            0.302,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(8,5,7,3,19)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            201,
                            0.307,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,5,7,3,19)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            306,
                            0.356,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,5,7,2,19)))
                   ));
        // register in new results object
        results2.registerSearchRun("problem-0", "search-0", updates);
        
        // merge into existing results which already has two runs of this search for this problem
        results.merge(results2);
        
        // verify merge
        assertEquals(2, results.getNumSearches("problem-0"));
        assertEquals(3, results.getNumRuns("problem-0", "search-0"));
        assertEquals(8, results.getRun("problem-0", "search-0", 2).get(0).getTime());
        assertEquals(201, results.getRun("problem-0", "search-0", 2).get(1).getTime());
        assertEquals(306, results.getRun("problem-0", "search-0", 2).get(2).getTime());
        assertEquals(0.302, results.getRun("problem-0", "search-0", 2).get(0).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(0.307, results.getRun("problem-0", "search-0", 2).get(1).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(0.356, results.getRun("problem-0", "search-0", 2).get(2).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // create results of a newly applied search for problem-1
        AnalysisResults<SubsetSolution> results3 = new AnalysisResults<>();
        updates = new ArrayList<>();
        updates.add(new BestSolutionUpdate<>(
                            2,
                            0.11,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(1,2,3,5,6)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            3,
                            0.22,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(2,3,5,6,7)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            4,
                            0.33,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(3,5,6,7,8)))
                   ));
        // register in new results object
        results3.registerSearchRun("problem-1", "search-1", updates);
        
        // merge into existing results which has one run of search-0 only for this problem
        results.merge(results3);
        
        // verify merge
        assertEquals(2, results.getNumSearches("problem-1"));
        assertEquals(1, results.getNumRuns("problem-1", "search-1"));
        assertEquals(2, results.getRun("problem-1", "search-1", 0).get(0).getTime());
        assertEquals(3, results.getRun("problem-1", "search-1", 0).get(1).getTime());
        assertEquals(4, results.getRun("problem-1", "search-1", 0).get(2).getTime());
        assertEquals(0.11, results.getRun("problem-1", "search-1", 0).get(0).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(0.22, results.getRun("problem-1", "search-1", 0).get(1).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(0.33, results.getRun("problem-1", "search-1", 0).get(2).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
        // create results for a new problem
        AnalysisResults<SubsetSolution> results4 = new AnalysisResults<>();
        updates = new ArrayList<>();
        updates.add(new BestSolutionUpdate<>(
                            10,
                            0.123,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(1,2,3)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            20,
                            0.234,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(2,3)))
                   ));
        updates.add(new BestSolutionUpdate<>(
                            30,
                            0.345,
                            new SubsetSolution(ids, new HashSet<>(Arrays.asList(1)))
                   ));
        // register in new results object
        results4.registerSearchRun("problem-x", "search-y", updates);
        
        // merge with existing results
        results.merge(results4);
        
        // verify merge
        assertEquals(3, results.getNumProblems());
        assertEquals(1, results.getNumSearches("problem-x"));
        assertEquals(1, results.getNumRuns("problem-x", "search-y"));
        assertEquals(10, results.getRun("problem-x", "search-y", 0).get(0).getTime());
        assertEquals(20, results.getRun("problem-x", "search-y", 0).get(1).getTime());
        assertEquals(30, results.getRun("problem-x", "search-y", 0).get(2).getTime());
        assertEquals(0.123, results.getRun("problem-x", "search-y", 0).get(0).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(0.234, results.getRun("problem-x", "search-y", 0).get(1).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(0.345, results.getRun("problem-x", "search-y", 0).get(2).getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        
    }
    
    /**
     * Test of getNumProblems method, of class AnalysisResults.
     */
    @Test
    public void testGetNumProblems() {
        
        System.out.println(" - test getNumProblems");
        
        assertEquals(2, results.getNumProblems());
        
    }

    /**
     * Test of getProblemIDs method, of class AnalysisResults.
     */
    @Test
    public void testGetProblemIDs() {
        
        System.out.println(" - test getProblemIDs");
        
        assertEquals(new HashSet<>(Arrays.asList("problem-0", "problem-1")), results.getProblemIDs());
        
    }

    /**
     * Test of getNumSearches method, of class AnalysisResults.
     */
    @Test
    public void testGetNumSearches() {
        
        System.out.println(" - test getNumSearches");
        
        assertEquals(2, results.getNumSearches("problem-0"));
        assertEquals(1, results.getNumSearches("problem-1"));
        
        boolean thrown = false;
        try{
            results.getNumSearches("i-do-not-exist");
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getSearchIDs method, of class AnalysisResults.
     */
    @Test
    public void testGetSearchIDs() {
        
        System.out.println(" - test getSearchIDs");
        
        assertEquals(new HashSet<>(Arrays.asList("search-0", "search-1")), results.getSearchIDs("problem-0"));
        assertEquals(new HashSet<>(Arrays.asList("search-0")), results.getSearchIDs("problem-1"));
        
        boolean thrown = false;
        try{
            results.getSearchIDs("i-do-not-exist");
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getNumRuns method, of class AnalysisResults.
     */
    @Test
    public void testGetNumRuns() {
        
        System.out.println(" - test getNumRuns");
        
        assertEquals(2, results.getNumRuns("problem-0", "search-0"));
        assertEquals(1, results.getNumRuns("problem-0", "search-1"));
        assertEquals(1, results.getNumRuns("problem-1", "search-0"));
        
        boolean thrown = false;
        try{
            results.getNumRuns("i-do-not-exist", "...");
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            results.getNumRuns("problem-0", "i-do-not-exist");
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            results.getNumRuns("problem-1", "search-1");
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
    }

    /**
     * Test of getRun method, of class AnalysisResults.
     */
    @Test
    public void testGetRun() {
        
        System.out.println(" - test getRun");
        
        List<BestSolutionUpdate<SubsetSolution>> run;
        BestSolutionUpdate<SubsetSolution> u1, u2, u3;
        
        // problem 0 - search 0 - run 0
        run = results.getRun("problem-0", "search-0", 0);
        assertEquals(3, run.size());
        u1 = run.get(0);
        u2 = run.get(1);
        u3 = run.get(2);
        assertEquals(12, u1.getTime());
        assertEquals(0.334, u1.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(new SubsetSolution(ids, new HashSet<>(Arrays.asList(3,6,1,7,19))), u1.getSolution());
        assertEquals(333, u2.getTime());
        assertEquals(0.356, u2.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,6,1,7,19))), u2.getSolution());
        assertEquals(425, u3.getTime());
        assertEquals(0.398, u3.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(new SubsetSolution(ids, new HashSet<>(Arrays.asList(4,6,1,2,19))), u3.getSolution());
        
        // problem 0 - search 0 - run 0
        run = results.getRun("problem-1", "search-0", 0);
        assertEquals(3, run.size());
        u1 = run.get(0);
        u2 = run.get(1);
        u3 = run.get(2);
        assertEquals(1, u1.getTime());
        assertEquals(0.1, u1.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(new SubsetSolution(ids, new HashSet<>(Arrays.asList(1,2,3,4,5,6))), u1.getSolution());
        assertEquals(2, u2.getTime());
        assertEquals(0.2, u2.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(new SubsetSolution(ids, new HashSet<>(Arrays.asList(2,3,4,5,6,7))), u2.getSolution());
        assertEquals(3, u3.getTime());
        assertEquals(0.3, u3.getValue(), TestConstants.DOUBLE_COMPARISON_PRECISION);
        assertEquals(new SubsetSolution(ids, new HashSet<>(Arrays.asList(3,4,5,6,7,8))), u3.getSolution());
        
        boolean thrown = false;
        try{
            results.getRun("i-do-not-exist", "...", 0);
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            results.getRun("problem-0", "i-do-not-exist", 0);
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            results.getRun("problem-1", "search-1", 0);
        } catch (UnknownIDException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            results.getRun("problem-0", "search-0", 2);
        } catch (IndexOutOfBoundsException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            results.getRun("problem-0", "search-1", 1);
        } catch (IndexOutOfBoundsException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        thrown = false;
        try{
            results.getRun("problem-1", "search-0", 1);
        } catch (IndexOutOfBoundsException ex) {
            thrown = true;
        }
        assertTrue(thrown);
        
        
    }

    /**
     * Test of writeJSON method, of class AnalysisResults.
     */
    @Test
    public void testWriteJSON_String() throws Exception {
        
        System.out.println(" - test writeJSON");
        
        // write to JSON file
        String filePath = folder.newFile().getPath();
        results.writeJSON(filePath);
        
        // read file
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        assertEquals(1, lines.size());
        
        // convert back to JSON object
        Json json = Json.read(lines.get(0));
        
        // check structure
        
        assertTrue(json.has("problem-0"));
        assertTrue(json.has("problem-1"));
        
        Json problem0 = json.at("problem-0");
        assertTrue(problem0.has("search-0"));
        assertTrue(problem0.has("search-1"));
        
        Json problem0search0 = problem0.at("search-0");
        assertTrue(problem0search0.isArray());
        assertEquals(2, problem0search0.asList().size());
        
        Json problem0search0run0 = problem0search0.at(0);
        assertTrue(problem0search0run0.isObject());
        assertEquals(Arrays.asList(12, 333, 425),
                    problem0search0run0.at("times").asJsonList()
                                                   .stream()
                                                   .map(e -> e.asInteger())
                                                   .collect(Collectors.toList()));
        assertEquals(Arrays.asList(0.334, 0.356, 0.398),
                     problem0search0run0.at("values").asJsonList()
                                                     .stream()
                                                     .map(e -> e.asDouble())
                                                     .collect(Collectors.toList()));
        assertFalse(problem0search0run0.has("best.solution"));
        
        Json problem0search0run1 = problem0search0.at(1);
        assertTrue(problem0search0run1.isObject());
        assertEquals(Arrays.asList(10, 246, 366),
                    problem0search0run1.at("times").asJsonList()
                                                   .stream()
                                                   .map(e -> e.asInteger())
                                                   .collect(Collectors.toList()));
        assertEquals(Arrays.asList(0.312, 0.377, 0.396),
                     problem0search0run1.at("values").asJsonList()
                                                     .stream()
                                                     .map(e -> e.asDouble())
                                                     .collect(Collectors.toList()));
        assertFalse(problem0search0run1.has("best.solution"));
        
        Json problem0search1 = problem0.at("search-1");
        assertTrue(problem0search1.isArray());
        assertEquals(1, problem0search1.asList().size());
        
        Json problem0search1run0 = problem0search1.at(0);
        assertTrue(problem0search1run0.isObject());
        assertEquals(Arrays.asList(56, 523, 866),
                    problem0search1run0.at("times").asJsonList()
                                                   .stream()
                                                   .map(e -> e.asInteger())
                                                   .collect(Collectors.toList()));
        assertEquals(Arrays.asList(0.333, 0.425, 0.553),
                     problem0search1run0.at("values").asJsonList()
                                                     .stream()
                                                     .map(e -> e.asDouble())
                                                     .collect(Collectors.toList()));
        assertFalse(problem0search1run0.has("best.solution"));
        
        Json problem1 = json.at("problem-1");
        assertTrue(problem1.has("search-0"));
        
        Json problem1search0 = problem1.at("search-0");
        assertTrue(problem1search0.isArray());
        assertEquals(1, problem1search0.asList().size());
        
        Json problem1search0run0 = problem1search0.at(0);
        assertTrue(problem1search0run0.isObject());
        assertEquals(Arrays.asList(1, 2, 3),
                    problem1search0run0.at("times").asJsonList()
                                                   .stream()
                                                   .map(e -> e.asInteger())
                                                   .collect(Collectors.toList()));
        assertEquals(Arrays.asList(0.1, 0.2, 0.3),
                     problem1search0run0.at("values").asJsonList()
                                                     .stream()
                                                     .map(e -> e.asDouble())
                                                     .collect(Collectors.toList()));
        assertFalse(problem1search0run0.has("best.solution"));
        
    }

    /**
     * Test of writeJSON method, of class AnalysisResults.
     */
    @Test
    public void testWriteJSON_String_JsonConverter() throws Exception {
        
        System.out.println(" - test writeJSON including solutions");
                
        // write to JSON file (including solutions)
        String filePath = folder.newFile().getPath();
        results.writeJSON(filePath, sol -> Json.array(sol.getSelectedIDs().toArray()));
        
        // read file
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        assertEquals(1, lines.size());
        
        // convert back to JSON object
        Json json = Json.read(lines.get(0));
        
        // check structure
        assertTrue(json.has("problem-0"));
        assertTrue(json.has("problem-1"));
        
        Json problem0 = json.at("problem-0");
        assertTrue(problem0.has("search-0"));
        assertTrue(problem0.has("search-1"));
        
        Json problem0search0 = problem0.at("search-0");
        assertTrue(problem0search0.isArray());
        assertEquals(2, problem0search0.asList().size());
        
        Json problem0search0run0 = problem0search0.at(0);
        assertTrue(problem0search0run0.isObject());
        assertTrue(problem0search0run0.has("best.solution"));
        assertEquals(new HashSet<>(Arrays.asList(4,6,1,2,19)),
                     problem0search0run0.at("best.solution").asJsonList()
                                                            .stream()
                                                            .map(e -> e.asInteger())
                                                            .collect(Collectors.toSet()));
        
        Json problem0search0run1 = problem0search0.at(1);
        assertTrue(problem0search0run1.isObject());
        assertTrue(problem0search0run1.has("best.solution"));
        assertEquals(new HashSet<>(Arrays.asList(4,6,7,2,19)),
                     problem0search0run1.at("best.solution").asJsonList()
                                                            .stream()
                                                            .map(e -> e.asInteger())
                                                            .collect(Collectors.toSet()));
        
        Json problem0search1 = problem0.at("search-1");
        assertTrue(problem0search1.isArray());
        assertEquals(1, problem0search1.asList().size());
        
        Json problem0search1run0 = problem0search1.at(0);
        assertTrue(problem0search1run0.isObject());
        assertTrue(problem0search1run0.has("best.solution"));
        assertEquals(new HashSet<>(Arrays.asList(4,12,1,22,16)),
                     problem0search1run0.at("best.solution").asJsonList()
                                                            .stream()
                                                            .map(e -> e.asInteger())
                                                            .collect(Collectors.toSet()));
        
        Json problem1 = json.at("problem-1");
        assertTrue(problem1.has("search-0"));
        
        Json problem1search0 = problem1.at("search-0");
        assertTrue(problem1search0.isArray());
        assertEquals(1, problem1search0.asList().size());
        
        Json problem1search0run0 = problem1search0.at(0);
        assertTrue(problem1search0run0.isObject());
        assertTrue(problem1search0run0.has("best.solution"));
        assertEquals(new HashSet<>(Arrays.asList(3,4,5,6,7,8)),
                     problem1search0run0.at("best.solution").asJsonList()
                                                            .stream()
                                                            .map(e -> e.asInteger())
                                                            .collect(Collectors.toSet()));
        
    }

}