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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mjson.Json;
import org.jamesframework.core.problems.sol.Solution;

/**
 * Groups results of an analysis performed by an {@link Analysis} object. The results can be accessed directly or
 * written to a JSON file (see {@link #writeJSON(String)} and {@link #writeJSON(String, JsonConverter)}) that can
 * then be loaded into R to be inspected and visualized using the james-analysis R package.
 * 
 * @param <SolutionType> solution type of the analyzed problems
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class AnalysisResults<SolutionType extends Solution> {
    
    // stores results: problem ID -> search ID -> list of search run results
    private final Map<String, Map<String, List<SearchRunResults<SolutionType>>>> results;
    
    /**
     * Create an empty results object.
     */
    public AnalysisResults() {
        results = new HashMap<>();
    }
    
    /**
     * Merge the given results into this results object. A copy of the newly added search 
     * runs is made.
     * 
     * @param otherResults other results to be merged into this results object
     * @return a reference to the updated results object
     */
    public AnalysisResults<SolutionType> merge(AnalysisResults<SolutionType> otherResults){
        otherResults.results.keySet().forEach(problemID -> {
            otherResults.results.get(problemID).keySet().forEach(searchID -> {
                List<SearchRunResults<SolutionType>> runs = otherResults.results.get(problemID).get(searchID);
                runs.forEach(run -> {
                    // deep copy run
                    SearchRunResults<SolutionType> runCopy = new SearchRunResults<>(run);
                    // register in this results object
                    registerSearchRun(problemID, searchID, runCopy);
                });
            });
        });
        return this;
    }
    
    /**
     * Register results of a search run, specifying the IDs of the problem being solved and the applied search.
     * If no runs have been registered before for this combination of problem and search, new entries are created.
     * Else, this run is appended to the existing runs.
     * 
     * @param problemID ID of the problem being solved
     * @param searchID ID of the applied search
     * @param run results of the search run
     */
    public void registerSearchRun(String problemID, String searchID, SearchRunResults<SolutionType> run){
        if(!results.containsKey(problemID)){
            results.put(problemID, new HashMap<>());
        }
        if(!results.get(problemID).containsKey(searchID)){
            results.get(problemID).put(searchID, new ArrayList<>());
        }
        results.get(problemID).get(searchID).add(run);
    }
    
    /**
     * Get the number of analyzed problems.
     * 
     * @return number of analyzed problems
     */
    public int getNumProblems(){
        return results.size();
    }
    
    /**
     * Get the IDs of the analyzed problems (unmodifiable view).
     * 
     * @return IDs of analyzed problems
     */
    public Set<String> getProblemIDs(){
        return Collections.unmodifiableSet(results.keySet());
    }
    
    /**
     * Get the number of different searches that have been applied to solve the problem with the given ID.
     * 
     * @param problemID ID of the problem
     * @return number of different searches applied to solve the problem
     * @throws UnknownIDException if an unknown problem ID is given
     */
    public int getNumSearches(String problemID){
        if(!results.containsKey(problemID)){
            throw new UnknownIDException("Unknown problem ID " + problemID + ".");
        }
        return results.get(problemID).size();
    }
    
    /**
     * Get the IDs of the different searches that have been applied to solve the problem with the
     * given ID (unmodifiable view).
     * 
     * @param problemID ID of the problem
     * @return IDs of different searches applied to solve the problem
     * @throws UnknownIDException if an unknown problem ID is given
     */
    public Set<String> getSearchIDs(String problemID){
        if(!results.containsKey(problemID)){
            throw new UnknownIDException("Unknown problem ID " + problemID + ".");
        }
        return Collections.unmodifiableSet(results.get(problemID).keySet());
    }
    
    /**
     * Get the number of performed runs of the given search when solving the given problem.
     * 
     * @param problemID ID of the problem
     * @param searchID ID of the applied search
     * @return number of performed runs of the given search when solving the given problem
     * @throws UnknownIDException if an unknown problem or search ID is given
     */
    public int getNumRuns(String problemID, String searchID){
        if(!results.containsKey(problemID)){
            throw new UnknownIDException("Unknown problem ID " + problemID + ".");
        }
        if(!results.get(problemID).containsKey(searchID)){
            throw new UnknownIDException("Unknown search ID " + searchID + " for problem " + problemID + ".");
        }
        return results.get(problemID).get(searchID).size();
    }
    
    /**
     * Get the results of the i-th performed run of the given search when solving the given problem.
     * 
     * @param problemID ID of the problem
     * @param searchID ID of the applied search
     * @param i search run index
     * @return results of i-th run of the given search when solving the given problem
     * @throws UnknownIDException if an unknown problem or search ID is given
     * @throws IndexOutOfBoundsException if there is no i-th run for this search and problem
     */
    public SearchRunResults<SolutionType> getRun(String problemID, String searchID, int i){
        if(!results.containsKey(problemID)){
            throw new UnknownIDException("Unknown problem ID " + problemID + ".");
        }
        if(!results.get(problemID).containsKey(searchID)){
            throw new UnknownIDException("Unknown search ID " + searchID + " for problem " + problemID + ".");
        }
        return results.get(problemID).get(searchID).get(i);
    }
    
    /**
     * Write the results to a JSON file that can be loaded into R to be inspected and visualized using
     * the james-analysis R package. If the specified file already exists, it is overwritten. This method
     * only stores the evaluation values and update times for each search run, skipping the actual best
     * found solutions. If desired to store the solutions as well use {@link #writeJSON(String, JsonConverter)}.
     * 
     * @param filePath path of the file to which the JSON output is written
     * @throws IOException if an I/O error occurs when writing to the file
     */
    public void writeJSON(String filePath) throws IOException{
        writeJSON(filePath, null);
    }
    
    /**
     * Write the results to a JSON file that can be loaded into R to be inspected and visualized using
     * the james-analysis R package. If the specified file already exists, it is overwritten. This method
     * stores the evaluation values, the update times and the actual best found solution for each search
     * run. The solutions are converted to a JSON representation using the given converter. If the latter
     * is <code>null</code>, the actual solutions are not stored in the output file.
     * 
     * @param filePath path of the file to which the JSON output is written
     * @param solutionJsonConverter converts solutions to a JSON representation
     * @throws IOException if an I/O error occurs when writing to the file
     */
    public void writeJSON(String filePath, JsonConverter<SolutionType> solutionJsonConverter) throws IOException{
        
        /**************************************************/
        /* STEP 1: Convert results to JSON representation */
        /**************************************************/
        
        Json resultsJson = Json.object();
        
        // register problems
        results.forEach((problemID, searches) -> {
        
            Json problemJson = Json.object();
            
            searches.forEach((searchID, runs) -> {
            
                Json searchJson = Json.array();
                
                // register search runs
                runs.forEach(run -> {
                
                    Json runJson = Json.object();
                    
                    // register update times and values
                    Json times = Json.array(run.getTimes().toArray());
                    Json values = Json.array(run.getValues().toArray());           
                    runJson.set("times", times);
                    runJson.set("values", values);
                    // register best found solution, if a JSON converter is given
                    if(solutionJsonConverter != null){
                        runJson.set("best.solution", solutionJsonConverter.toJson(run.getBestSolution()));
                    }
                    
                    searchJson.add(runJson);
                
                });
                
                problemJson.set(searchID, searchJson); 
            
            });
            
            resultsJson.set(problemID, problemJson);
        
        });
        
        
        /*************************************/
        /* STEP 2: Write JSON string to file */
        /*************************************/
        
        Files.write(Paths.get(filePath), Collections.singleton(resultsJson.toString()));
        
    }
    
}
