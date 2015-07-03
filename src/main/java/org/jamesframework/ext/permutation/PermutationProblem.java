/*
 * Copyright 2014 Ghent University, Bayer CropScience.
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

package org.jamesframework.ext.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jamesframework.core.problems.GenericProblem;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;
import org.jamesframework.core.problems.objectives.Objective;

/**
 * Generic permutation problem. Requires that every item in the data set is identified with a unique integer ID.
 * The solution type is fixed to {@link PermutationSolution}, which models a solution as an ordered sequence
 * of these IDs. The data type can be any class or interface that implements the {@link IntegerIdentifiedData}
 * interface, which imposes the assignment of IDs to items in the data set. A default random solution generator
 * is used, which creates random permutations by shuffling the list of IDs as retrieved from the data.
 * 
 * @param <DataType> data type of the permutation problem, required to implement the {@link IntegerIdentifiedData} interface
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PermutationProblem<DataType extends IntegerIdentifiedData> extends GenericProblem<PermutationSolution, DataType>{

    /**
     * Create a permutation problem with given objective and data. None of the arguments can be <code>null</code>.
     * 
     * @param data underlying data
     * @param objective objective of the problem
     * @throws NullPointerException if <code>objective</code> or <code>data</code> are <code>null</code>
     */
    public PermutationProblem(DataType data, Objective<? super PermutationSolution, ? super DataType> objective) {
        // already checks that objective is not null
        super(data, objective, (r,d) -> {
            // create list with all IDs
            List<Integer> ids = new ArrayList<>(d.getIDs());
            // shuffle IDs
            Collections.shuffle(ids, r);
            // create and return permutation solution
            return new PermutationSolution(ids);
        });
        // check: data not null
        if(data == null){
            throw new NullPointerException("Error while creating permutation problem: data is required, can not be null.");
        }
    }

}
