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

package org.jamesframework.ext.problems.objectives.evaluations;

import org.jamesframework.core.problems.objectives.evaluations.Evaluation;

/**
 * Normalizes the value of an arbitrary evaluation from a given interval [min, max] to [0, 1].
 * Normalization is linear, using the formula
 * \[
 *  normalized = \frac{value - min}{max - min}.
 * \]
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class NormalizedEvaluation implements Evaluation {

    // wrapped evaluation to normalize
    private final Evaluation eval;
    // normalization interval
    private final double min, max;

    /**
     * Create a normalized evaluation that wraps the given evaluation
     * and normalizes its values from [min, max] to [0, 1].
     * 
     * @param eval wrapped evaluation
     * @param min lower bound of normalization interval
     * @param max upper bound of normalization interval
     */
    public NormalizedEvaluation(Evaluation eval, double min, double max) {
        // store evaluation
        this.eval = eval;
        // store bounds
        this.min = min;
        this.max = max;
    }

    /**
     * Calculates normalized evaluation in [0, 1].
     * 
     * @return normalized evaluation
     */
    @Override
    public double getValue() {
        return (eval.getValue() - min) / (max - min);
    }
    
    /**
     * Get the original, unnormalized evaluation.
     * 
     * @return unnormalized evaluation
     */
    public Evaluation getUnnormalizedEvaluation(){
        return eval;
    }
    
}
