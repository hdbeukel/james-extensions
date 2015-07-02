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
import java.util.Collections;
import java.util.List;
import org.jamesframework.core.problems.sol.Solution;

/**
 * Contains results of a search run. For every point in time during search when a
 * new best solution had been found, the elapsed runtime (in milliseconds) and value
 * of the newly obtained best solution are stored. The final best solution itself
 * is stored as well.
 * 
 * @param <SolutionType> solution type of the analyzed problem
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SearchRunResults<SolutionType extends Solution> {

    // update times
    private final List<Long> times;
    // values of best found solutions at each update
    private final List<Double> values;
    // final best solution
    private SolutionType bestSolution;

    /**
     * Create an empty instance.
     */
    public SearchRunResults() {
        // create lists
        times = new ArrayList<>();
        values = new ArrayList<>();
    }
    
    /**
     * Copy constructor. A deep copy is made of the lists of update times and values.
     * The final best solution is <b>not</b> copied; i.e. a reference to the same object
     * is stored.
     * 
     * @param run search run results to be copied
     */
    public SearchRunResults(SearchRunResults<SolutionType> run){
        this();
        // copy update times and values
        times.addAll(run.times);
        values.addAll(run.values);
        // store final best solution (no copy)
        bestSolution = run.getBestSolution();
    }
    
    /**
     * Update the best found solution. The update time and newly obtained value are added
     * to the list of updates and the final best solution is overwritten.
     * 
     * @param time time at which the solution was found (milliseconds)
     * @param value value of the solution
     * @param newBestSolution newly found best solution
     */
    public void updateBestSolution(long time, double value, SolutionType newBestSolution){
        times.add(time);
        values.add(value);
        bestSolution = newBestSolution;
    }
    
    /**
     * Get the number of registered best solution updates.
     * 
     * @return number of updates
     */
    public long getNumUpdates(){
        return times.size();
    }
    
    /**
     * Get the best solution found during this search run
     * 
     * @return best found solution
     */
    public SolutionType getBestSolution(){
        return bestSolution;
    }
    
    /**
     * Get a list of the update times (unmodifiable view).
     * 
     * @return list of update times
     */
    public List<Long> getTimes(){
        return Collections.unmodifiableList(times);
    }
    
    /**
     * Get a list of the values of the newly found best solutions (unmodifiable view).
     * 
     * @return list of values of newly found best solutions
     */
    public List<Double> getValues(){
        return Collections.unmodifiableList(values);
    }
    
}
