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

import org.jamesframework.core.problems.Solution;

/**
 * Represents the event of finding a new best solution during search, for the problem being solved.
 * The update contains a reference to the new best solution, the value of its evaluation and the time
 * at which the solution was obtained (since starting the search).
 * 
 * @param <SolutionType> solution type of the problem solved
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class BestSolutionUpdate<SolutionType extends Solution> {

    private final long time;
    private final double value;
    private final SolutionType solution;

    /**
     * Create an event.
     * 
     * @param time time at which the new best solution was found (since starting the search), in milliseconds
     * @param value evaluation value of the new best solution
     * @param solution new best solution
     */
    public BestSolutionUpdate(long time, double value, SolutionType solution) {
        this.time = time;
        this.value = value;
        this.solution = solution;
    }

    /**
     * Get the time at which the new best solution was found (since starting the search), in milliseconds
     * 
     * @return time when solution was found, in milliseconds
     */
    public long getTime() {
        return time;
    }

    /**
     * Get the evaluation value of the new best solution.
     * 
     * @return evaluation value of solution
     */
    public double getValue() {
        return value;
    }

    /**
     * Get the new best solution that was found.
     * 
     * @return best solution found at this time during search
     */
    public SolutionType getSolution() {
        return solution;
    }
    
}
