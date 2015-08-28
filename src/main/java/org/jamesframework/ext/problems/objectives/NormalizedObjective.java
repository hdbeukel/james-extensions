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

package org.jamesframework.ext.problems.objectives;

import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.sol.Solution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.ext.problems.objectives.evaluations.NormalizedEvaluation;

/**
 * A normalized objective wraps another objective and normalizes all produces evaluations
 * from a given interval [min, max] to [0, 1]. Normalization is linear, using the formula
 * \[
 *  normalized = \frac{value - min}{max - min}.
 * \]
 * Both methods used for full and delta evaluation return a {@link NormalizedEvaluation}.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 * @param <SolutionType> solution type to be evaluated, required to extend {@link Solution}
 * @param <DataType> underlying data type
 */
public class NormalizedObjective<SolutionType extends Solution, DataType> implements Objective<SolutionType, DataType> {

    // wrapped objective
    private final Objective<? super SolutionType, ? super DataType> obj;
    // bounds of interval [min,max] which is normalized to [0,1] (linear)
    private final double min, max;

    /**
     * Create a normalized objective that wraps the given objective and normalizes all produced
     * evaluation from [min, max] to [0, 1].
     * 
     * @param obj original, unnormalized objective
     * @param min lower bound of normalization interval
     * @param max upper bound of normalization interval
     * @throws NullPointerException if <code>obj</code> is <code>null</code>
     */
    public NormalizedObjective(Objective<? super SolutionType, ? super DataType> obj, double min, double max) {
        // check
        if(obj == null){
            throw new NullPointerException("Error while initializing normalized objective: wrapped objective "
                                         + "can not be null.");
        }
        // store
        this.obj = obj;
        this.min = min;
        this.max = max;
    }

    /**
     * Get the original, unnormalized objective.
     * 
     * @return unnormalized objective
     */
    public Objective<? super SolutionType, ? super DataType> getUnnormalizedObjective() {
        return obj;
    }

    /**
     * Get lower bound of normalization interval.
     * 
     * @return lower bound
     */
    public double getMin() {
        return min;
    }

    /**
     * Get upper bound of normalization interval
     * 
     * @return upper bound
     */
    public double getMax() {
        return max;
    }

    /**
     * Computes the normalized evaluation of the given solution. The solution is first evaluated
     * using the original, unnormalized objective after which the returned evaluation is wrapped
     * in a {@link NormalizedEvaluation}.
     * 
     * @param solution solution to evaluate
     * @param data underlying data
     * @return normalized evaluation
     */
    @Override
    public NormalizedEvaluation evaluate(SolutionType solution, DataType data) {
        // evaluate objective
        Evaluation eval = obj.evaluate(solution, data);
        // normalize
        return new NormalizedEvaluation(eval, min, max);
    }

    /**
     * Computes the normalized evaluation of the solution obtained by applying the given move
     * to the current solution. Relies on delta evaluation performed by the original, unnormalized
     * objective, whose result is wrapped in a {@link NormalizedEvaluation}.
     * 
     * @param move move to evaluate
     * @param curSolution current solution
     * @param curEvaluation evaluation of current solution
     * @param data underlying data used for evaluation
     * @param <ActualSolutionType> the actual solution type of the problem that is being solved;
     *                             a subtype of the solution types of both the objective and the applied move
     * @return evaluation of modified solution obtained when applying the move to the current solution
     * @throws IncompatibleDeltaEvaluationException if the delta evaluation of the original objective
     *                                              is not compatible with the received move type
     */
    @Override
    public <ActualSolutionType extends SolutionType> NormalizedEvaluation evaluate(Move<? super ActualSolutionType> move,
                                                                                   ActualSolutionType curSolution,
                                                                                   Evaluation curEvaluation,
                                                                                   DataType data) {
        // cast evaluation
        NormalizedEvaluation normalizedEval = (NormalizedEvaluation) curEvaluation;
        // retrieve original, unnormalized evaluation
        Evaluation eval = normalizedEval.getUnnormalizedEvaluation();
        // delta evaluation for original objective
        Evaluation newEval = obj.evaluate(move, curSolution, eval, data);
        // wrap in normalized evaluation
        NormalizedEvaluation newNormalizedEval = new NormalizedEvaluation(newEval, min, max);
        
        return newNormalizedEval;
        
    }
    
    /**
     * Indicates whether the produced evaluations are to be minimized.
     * Delegates to the original, unnormalized evaluation.
     * 
     * @return <code>true</code> if evaluations are to be minimized,
     *         <code>false</code> if they are to be maximized
     */
    @Override
    public boolean isMinimizing() {
        return obj.isMinimizing();
    }
    
}
