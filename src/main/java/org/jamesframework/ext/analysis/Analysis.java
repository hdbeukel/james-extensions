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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jamesframework.core.exceptions.JamesRuntimeException;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.listeners.SearchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * <p>
 * This analysis class can be used to set up and run an experiment with different search algorithms applied to
 * several problem instances, for example with different data sets, objectives or problem parameters. It can also
 * be used to perform a parameter sweep for a certain algorithm (e.g. for temperatures in parallel tempering, or the
 * tabu list size in tabu search) and to try out different neighbourhood functions.
 * </p>
 * <p>
 * By default, each algorithm is executed 10 times because most searches are randomized. The number of repeats can
 * be tuned on an overall as well as search specific level. It is advised to perform a sufficient amount of repeats
 * so that stable results are obtained.
 * </p>
 * 
 * @param <SolutionType> solution type of the analyzed problems
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class Analysis<SolutionType extends Solution> {

    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(Analysis.class);
    // log marker
    private static final Marker ANALYSIS_MARKER = MarkerFactory.getMarker("analysis");
    
    // problems to be analyzed
    private final Map<String, Problem<SolutionType>> problems;
    // search factories creating searches to be applied
    private final Map<String, SearchFactory<SolutionType>> searches;
    
    // global number of runs to be applied for each search
    private int numRuns;
    // search specific number of runs, overriding global run count if set
    private final Map<String, Integer> searchNumRuns;
    // global number of burn-in runs
    private int numBurnIn;
    // search specific number of burn-in runs, overriding global burn-in if set
    private final Map<String, Integer> searchNumBurnIn;
    
    /**
     * Create an analysis object. The number of performed runs of each search defaults to 10.
     */
    public Analysis(){
        problems = new HashMap<>();
        searches = new HashMap<>();
        searchNumRuns = new HashMap<>();
        searchNumBurnIn = new HashMap<>();
        numRuns = 10;
        numBurnIn = 1;
    }
    
    /**
     * Get the global number of runs that will be performed for every applied search
     * for which no search specific number of runs has been set. Defaults to 10 and can
     * be changed using {@link #setNumRuns(int)}. To set a search specific numer of runs,
     * use {@link #setNumRuns(String, int)}.
     * 
     * @return number of performed search runs (global setting)
     */
    public int getNumRuns(){
        return numRuns;
    }
    
    /**
     * Set the global number of runs that will be performed for every applied search
     * for which no search specific number of runs is set. Returns a reference to the
     * analysis object on which this method was called so that methods can be chained.
     * 
     * @param n global number of search runs to be performed
     * @return reference to the analysis object on which this method was called
     * @throws IllegalArgumentException if <code>n</code> is not strictly positive
     */
    public Analysis<SolutionType> setNumRuns(int n){
        if(n <= 0){
            throw new IllegalArgumentException("Number of runs should be strictly positive.");
        }
        numRuns = n;
        return this;
    }
    
    /**
     * <p>
     * Get the global number of additional burn-in runs that will be performed for every applied
     * search for which no search specific number of burn-in runs has been set. Burn-in runs are
     * executed before the actual search runs and not registered in the results, to "warm up" the
     * analysis in order to reduce the influence of just in time compilation (JIT) and on the fly
     * optimizations performed by the JVM.
     * </p>
     * <p>
     * Defaults to 1 and can be changed using {@link #setNumBurnIn(int)}. To set a search specific
     * number of additional burn-in runs, use {@link #setNumBurnIn(String, int)}.
     * </p>
     * 
     * @return number of performed burn-in runs (global setting)
     */
    public int getNumBurnIn(){
        return numBurnIn;
    }
    
    /**
     * <p>
     * Set the global number of additional burn-in runs that will be performed for every applied
     * search for which no search specific number of burn-in runs is set. Burn-in runs are
     * executed before the actual search runs and not registered in the results, to "warm up" the
     * analysis in order to reduce the influence of just in time compilation (JIT) and on the fly
     * optimizations performed by the JVM.
     * </p>
     * <p>
     * To set a search specific number of additional burn-in runs, use {@link #setNumBurnIn(String, int)}.
     * Returns a reference to the analysis object on which this method was called so that methods can be chained.
     * </p>
     * 
     * @param n global number of additional burn-in runs to be performed
     * @return reference to the analysis object on which this method was called
     * @throws IllegalArgumentException if <code>n</code> is not strictly positive
     */
    public Analysis<SolutionType> setNumBurnIn(int n){
        if(n <= 0){
            throw new IllegalArgumentException("Number of burn-in runs should be strictly positive.");
        }
        numBurnIn = n;
        return this;
    }
    
    /**
     * Get the number of runs that will be performed for the search with the given ID. If no specific
     * number of runs has been set for this search using {@link #setNumRuns(String, int)}, the global
     * value obtained from {@link #getNumRuns()} is returned.
     * 
     * @param searchID ID of the search
     * @return number of runs that will be performed for this search
     * @throws UnknownIDException if no search with this ID has been added
     */
    public int getNumRuns(String searchID){
        if(!searches.containsKey(searchID)){
            throw new UnknownIDException("No search with ID " + searchID + " has been added.");
        }
        return searchNumRuns.getOrDefault(searchID, getNumRuns());
    }
    
    /**
     * Set the number of runs to be performed for the given search. This does not affect the number
     * of runs of the other searches. Returns a reference to the analysis object on which this method
     * was called so that methods can be chained.
     * 
     * @param searchID ID of the search
     * @param n number of runs to be performed for this specific search
     * @return reference to the analysis object on which this method was called
     * @throws UnknownIDException if no search with this ID has been added
     * @throws IllegalArgumentException if <code>n</code> is not strictly positive
     */
    public Analysis<SolutionType> setNumRuns(String searchID, int n){
        if(!searches.containsKey(searchID)){
            throw new UnknownIDException("No search with ID " + searchID + " has been added.");
        }
        if(n <= 0){
            throw new IllegalArgumentException("Number of runs should be strictly positive.");
        }
        searchNumRuns.put(searchID, n);
        return this;
    }
    
    /**
     * Get the number of additional burn-in runs that will be performed for the search with the given ID.
     * If no specific number of burn-in runs has been set for this search using {@link #setNumBurnIn(String, int)},
     * the global value obtained from {@link #getNumBurnIn()} is returned. Burn-in runs are executed before the
     * actual search runs and not registered in the results, to "warm up" the analysis in order to reduce the
     * influence of just in time compilation (JIT) and on the fly optimizations performed by the JVM.
     * 
     * @param searchID ID of the search
     * @return number of additional burn-in runs that will be performed for this search
     * @throws UnknownIDException if no search with this ID has been added
     */
    public int getNumBurnIn(String searchID){
        if(!searches.containsKey(searchID)){
            throw new UnknownIDException("No search with ID " + searchID + " has been added.");
        }
        return searchNumBurnIn.getOrDefault(searchID, getNumBurnIn());
    }
    
    /**
     * Set the number of additional burn-in runs to be performed for the given search. This does not affect
     * the number of burn-in runs of the other searches. Returns a reference to the analysis object on which
     * this method was called so that methods can be chained.
     * 
     * @param searchID ID of the search
     * @param n number of additional burn-in runs to be performed for this specific search
     * @return reference to the analysis object on which this method was called
     * @throws UnknownIDException if no search with this ID has been added
     * @throws IllegalArgumentException if <code>n</code> is not strictly positive
     */
    public Analysis<SolutionType> setNumBurnIn(String searchID, int n){
        if(!searches.containsKey(searchID)){
            throw new UnknownIDException("No search with ID " + searchID + " has been added.");
        }
        if(n <= 0){
            throw new IllegalArgumentException("Number of burn-in runs should be strictly positive.");
        }
        searchNumBurnIn.put(searchID, n);
        return this;
    }
        
    /**
     * Add a problem to be analyzed. Returns a reference to the analysis object on which this
     * method was called so that methods can be chained.
     * 
     * @param ID unique ID of the added problem
     * @param problem problem to be added to the analysis
     * @return reference to the analysis object on which this method was called
     * @throws DuplicateIDException if a problem has already been added with the specified ID
     * @throws NullPointerException if <code>problem</code> is <code>null</code>
     */
    public Analysis<SolutionType> addProblem(String ID, Problem<SolutionType> problem){
        if(problem == null){
            throw new NullPointerException("Problem can not be null.");
        }
        if(problems.containsKey(ID)){
            throw new DuplicateIDException("Duplicate problem ID: " + ID + ".");
        }
        problems.put(ID, problem);
        return this;
    }
    
    /**
     * Add a search to be applied to solve the analyzed problems. Requires a search factory instead of a
     * plain search as a new instance of the search will be created for every run and for every analyzed
     * problem. Returns a reference to the analysis object on which this method was called so that methods
     * can be chained.
     * 
     * @param ID unique ID of the added search
     * @param searchFactory factory that creates a search given the problem to be solved
     * @return reference to the analysis object on which this method was called
     * @throws DuplicateIDException if a search has already been added with the specified ID
     * @throws NullPointerException if <code>searchFactory</code> is <code>null</code>
     */
    public Analysis<SolutionType> addSearch(String ID, SearchFactory<SolutionType> searchFactory){
        if(searchFactory == null){
            throw new NullPointerException("Search factory can not be null.");
        }
        if(searches.containsKey(ID)){
            throw new DuplicateIDException("Duplicate search ID: " + ID + ".");
        }
        searches.put(ID, searchFactory);
        return this;
    }
    
    /**
     * Run the analysis. The returned results can be accessed directly or written to a JSON file to be
     * loaded into R for analysis and visualization using the james-analysis R package. The analysis
     * progress is logged at INFO level, all log messages being tagged with an "analysis" marker.
     * 
     * @return results of the analysis
     * @throws JamesRuntimeException if anything goes wrong during any of the searches due to misconfiguration
     */
    public AnalysisResults<SolutionType> run(){
        
        // create results object
        AnalysisResults<SolutionType> results = new AnalysisResults<>();
        
        // log
        LOGGER.info(ANALYSIS_MARKER,
                    "Started analysis of {} problems {} using {} searches {}.",
                    problems.size(),
                    problems.keySet(),
                    searches.size(),
                    searches.keySet());
        
        // analyze each problem
        problems.forEach((problemID, problem) -> {
            LOGGER.info(ANALYSIS_MARKER, "Analyzing problem {}.", problemID);
            // apply all searches
            searches.forEach((searchID, searchFactory) -> {
                // execute burn-in runs
                int nBurnIn = getNumBurnIn(searchID);
                for(int burnIn=0; burnIn<nBurnIn; burnIn++){
                    LOGGER.info(ANALYSIS_MARKER,
                                "Burn-in of search {} applied to problem {} (burn-in run {}/{}).",
                                searchID, problemID, burnIn+1, nBurnIn);
                    // create search
                    Search<SolutionType> search = searchFactory.create(problem);
                    // run search
                    search.start();
                    // dispose search
                    search.dispose();
                    LOGGER.info(ANALYSIS_MARKER,
                                "Finished burn-in run {}/{} of search {} for problem {}.",
                                burnIn+1, nBurnIn, searchID, problemID);
                }
                // perform actual search runs and register results
                int nRuns = getNumRuns(searchID);
                for(int run=0; run<nRuns; run++){
                    LOGGER.info(ANALYSIS_MARKER,
                                "Applying search {} to problem {} (run {}/{}).",
                                searchID, problemID, run+1, nRuns);
                    // create search
                    Search<SolutionType> search = searchFactory.create(problem);
                    // attach listener
                    AnalysisListener listener = new AnalysisListener();
                    search.addSearchListener(listener);
                    // run search
                    search.start();
                    // dispose search
                    search.dispose();
                    // register search run in results object
                    results.registerSearchRun(problemID, searchID, listener.getBestSolutionUpdates());
                    LOGGER.info(ANALYSIS_MARKER,
                                "Finished run {}/{} of search {} for problem {}.",
                                run+1, nRuns, searchID, problemID);
                }
            });
            LOGGER.info(ANALYSIS_MARKER, "Done analyzing problem {}.", problemID);
        });
        
        // log
        LOGGER.info(ANALYSIS_MARKER, "Analysis complete.");
        
        return results;
        
    }
    
    /**
     * Listens to searches during analysis to track and register best solution updates.
     */
    private class AnalysisListener implements SearchListener<SolutionType> {

        // list of best solution updates
        private final List<BestSolutionUpdate<SolutionType>> updates;

        /**
         * Create listener.
         */
        public AnalysisListener() {
            updates = new ArrayList<>();
        }

        /**
         * Registers best solution updates.
         * 
         * @param search applied search
         * @param newBestSolution new best solution
         * @param newBestSolutionEvaluation new best evaluation
         * @param newBestSolutionValidation validation of new best solution
         */
        @Override
        public void newBestSolution(Search<? extends SolutionType> search, SolutionType newBestSolution,
                                    Evaluation newBestSolutionEvaluation, Validation newBestSolutionValidation) {
            updates.add(new BestSolutionUpdate<>(search.getRuntime(), newBestSolutionEvaluation.getValue(), newBestSolution));
        }
        
        /**
         * Get a list of all best solution updates that have occurred during search. Entries are ordered by time.
         * 
         * @return list of best solution updates
         */
        public List<BestSolutionUpdate<SolutionType>> getBestSolutionUpdates(){
            return updates;
        }
        
    }
    
}
