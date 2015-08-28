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
import java.util.Objects;
import java.util.stream.Collectors;
import org.jamesframework.core.exceptions.SolutionModificationException;
import org.jamesframework.core.problems.sol.Solution;

/**
 * High-level order solution modeled in terms of IDs of ordered items.
 * The order consists of an ordered sequence of items which are each
 assumed to be identified using a unique integer ID.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class PermutationSolution extends Solution {

    // ordered sequence of IDs (+ unmodifiable view)
    private final List<Integer> order, orderView;

    /**
     * Creates a order solution. Items are ordered according to the given list of IDs.
     * Contents of this list are copied into internal data structures so that no reference
     * to the given list is stored in this solution.
     * 
     * @param order ordered IDs
     * @throws IllegalArgumentException if <code>order</code> is empty or contains duplicates
     * @throws NullPointerException if <code>order</code> is <code>null</code> or contains
     *                              any <code>null</code> elements
     */
    public PermutationSolution(List<Integer> order) {
        this(order, true);
    }
    
    /**
     * Private constructor used to skip input checks when copying a solution
     * (see {@link #copy()}).
     * 
     * @param order ordered IDs
     * @param checkInput indicates whether the input should be checked (see thrown exceptions)
     * @throws IllegalArgumentException if <code>checkInput</code> is <code>true</code> and
     *                                  <code>order</code> is empty or contains duplicates
     * @throws NullPointerException if <code>checkInput</code> is <code>true</code> and
     *                              <code>order</code> is <code>null</code> or contains
     *                              any <code>null</code> elements     
     */
    private PermutationSolution(List<Integer> order, boolean checkInput){
        // check input if requested
        if(checkInput){
            if(order == null || order.stream().anyMatch(Objects::isNull)){
                throw new NullPointerException("Error while creating permutation solution: given list of IDs can not "
                                             + "be null and can not contain any null elements."); 
            }
            if(order.isEmpty()){
                throw new IllegalArgumentException("Error while creating permutation solution: given list of IDs can not be empty.");
            }
            if(order.stream().distinct().count() < order.size()){
                throw new IllegalArgumentException("Error while creating permutation solution: given list of IDs can not contain duplicates.");
            }
        }
        // store ordered IDs (copy list)
        this.order = new ArrayList<>(order);
        // initialize view
        this.orderView = Collections.unmodifiableList(this.order);
    }
    
    /**
     * Get an unmodifiable view of the current order of IDs.
     * 
     * @return ordered IDs
     */
    public List<Integer> getOrder(){
        return orderView;
    }
    
    /**
     * Get the number of items in this permutation.
     * 
     * @return size of this permutation
     */
    public int size(){
        return getOrder().size();
    }
    
    /**
     * Swap the items at position i and j in the permutation.
     * Both positions should be positive and smaller than the
     * number of items in the permutation.
     * 
     * @param i position of first item to be swapped
     * @param j position of second item to be swapped
     * @throws SolutionModificationException if <code>i</code> or <code>j</code> is
=     *                                      negative, or larger than or equal to the
     *                                       number of items in the permutation
     */
    public void swap(int i, int j){
        try{
            // swap items
            Collections.swap(order, i, j);
        } catch (IndexOutOfBoundsException ex){
            throw new SolutionModificationException("Error while modifying permutation solution: swapped positions should be positive "
                                                    + "and smaller than the number of items in the permutation.", this);
        }
    }
    
    /**
     * Creates a deep copy of this permutation solution with the same order of IDs.
     * The copy does not share any object references with the original solution.
     * 
     * @return deep copy
     */
    @Override
    public PermutationSolution copy() {
        // skip input checks, we know it's valid
        return new PermutationSolution(getOrder(), false);
    }

    /**
     * Checks whether the given other object is also a permutation solution consisting
     * of the same IDs in exactly the same order.
     * 
     * @param other object to compare for equality
     * @return <code>true</code> if the given object is an equal permutation solution
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        final PermutationSolution otherPerm = (PermutationSolution) other;
        return Objects.equals(getOrder(), otherPerm.getOrder());
    }

    /**
     * Hash code computation corresponding to the implementation of {@link #equals(Object)}.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(getOrder());
        return hash;
    }

    /**
     * Create a string representation of the permutation solution,
     * indicating the current order of IDs.
     * 
     * @return string representation
     */
    @Override
    public String toString(){
        return getOrder().stream()
                         .map(Object::toString)
                         .collect(Collectors.joining(", ", "Permutation solution: {", "}"));
    }
    
}
