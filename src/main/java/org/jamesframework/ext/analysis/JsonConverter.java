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

import mjson.Json;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * A JSON converter is a functional interface defining a single method to convert objects
 * of a specific type to a JSON representation. The JSON representation is defined in the
 * <a href="http://bolerio.github.io/mjson/">mJson library</a>. Implementing classes should
 * follow the instructions of mJson regarding how to convert a Java object to JSON representation.
 * 
 * @see <a href="http://bolerio.github.io/mjson/">mJson library</a>
 * @param <T> type of objects than can be converted to JSON
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
@FunctionalInterface
public interface JsonConverter<T> {
    
    /**
     * Predefined converter for subset solutions that creates a JSON array containing the selected IDs.
     */
    public static final JsonConverter<SubsetSolution> SUBSET_SOLUTION = sol -> Json.array(sol.getSelectedIDs().toArray());

    /**
     * Convert an object to a JSON representation.
     * 
     * @param object object to be converted to JSON
     * @return JSON representation of object
     */
    public Json toJson(T object);
    
}
