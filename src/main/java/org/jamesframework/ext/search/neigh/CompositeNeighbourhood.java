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

package org.jamesframework.ext.search.neigh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.jamesframework.core.problems.Solution;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.util.RouletteSelector;

/**
 * A composite neighbourhood combines a collection of other neighbourhoods which each have a weight that determines
 * the relative frequency with which random moves are sampled from each contained neighbourhood. When requesting
 * a random move to the composite neighbourhood, a random move produced by one of the contained neighbourhoods is
 * returned, picked through roulette selection based on the weights given at construction. When requesting all moves,
 * the union of all moves generated by all contained neighbourhoods is returned, regardless of the assigned weights.
 * 
 * @param <SolutionType> solution type for which this neighbourhood can be applied, required to extend {@link Solution}
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class CompositeNeighbourhood<SolutionType extends Solution> implements Neighbourhood<SolutionType>{

    // contained neighbourhoods
    private final List<? extends Neighbourhood<? super SolutionType>> neighs;
    // neighbourhood weights
    private final List<Double> weights;

    /**
     * Creates a composite neighbourhood consisting of the list of given neighbourhoods and specified weights.
     * The list of neighbourhoods and weights can not be <code>null</code>, should be of the same size and can
     * not contain any <code>null</code> elements. At least one neighbourhood should be given and all weights
     * should be strictly positive.
     * 
     * @param neighbourhoods list of neighbourhoods
     * @param neighbourhoodWeights list of weights assigned to the neighbourhoods (same order as neighbourhoods)
     * @throws NullPointerException if <code>neighbourhoods</code> or <code>neighbourhoodWeights</code> are
     *                              <code>null</code> or contain any <code>null</code> elements
     * @throws IllegalArgumentException if <code>neighbourhoods</code> and <code>neighbourhoodWeights</code> are
     *                                  not of the same size or both empty, or if <code>neighbourhoodWeights</code>
     *                                  contains any weight &le; 0
     */
    public CompositeNeighbourhood(List<? extends Neighbourhood<? super SolutionType>> neighbourhoods, List<Double> neighbourhoodWeights) {
        // check not null
        if(neighbourhoods == null){
            throw new NullPointerException("Neighbourhood list can not be null.");
        }
        if(neighbourhoodWeights == null){
            throw new NullPointerException("Neighbourhood weight list can not be null.");
        }
        // check size
        if(neighbourhoods.size() != neighbourhoodWeights.size()){
            throw new IllegalArgumentException("Neighbourhood and weight list should be of the same size.");
        }
        if(neighbourhoods.isEmpty()){
            throw new IllegalArgumentException("At least one neighbourhood should be specified.");
        }
        // inspect list contents
        neighbourhoods.forEach(neigh -> {
            if(neigh == null){
                throw new NullPointerException("Neighbourhood list can not contain any null elements.");
            }
        });
        neighbourhoodWeights.forEach(w -> {
            if(w == null){
                throw new NullPointerException("Neighbourhood weight list can not contain any null elements.");
            }
            if(w <= 0){
                throw new IllegalArgumentException("All weights should be strictly positive.");
            }
        });
        // store neighbourhoods and weights
        this.neighs = neighbourhoods;
        this.weights = neighbourhoodWeights;
    }
    
    /**
     * Produces a random move generated by one of the contained neighbourhoods based on the assigned weights.
     * First, a random move is requested from each neighbourhood as it may happen that one or more neighbourhoods
     * are unable to produce a random move. If no moves are obtained, this method returns <code>null</code>.
     * Else, it selects a move through roulette selection based on the weights assigned at construction.
     * 
     * @param solution solution for which a move is generated
     * @param rnd source of randomness used to generate random move
     * @return random move generated by one of the contained neighbourhoods, which is selected by
     *         roulette selection based on the assigned weights; <code>null</code> if none of the
     *         contained neighbourhoods are able to produce a random move
     */
    @Override
    public Move<? super SolutionType> getRandomMove(SolutionType solution, Random rnd) {
        // generate random move for each neighbourhood
        List<Move<? super SolutionType>> moves = new ArrayList<>();
        List<Double> respectiveWeights = new ArrayList<>();
        Move<? super SolutionType> move;
        for(int i=0; i<neighs.size(); i++){
            move = neighs.get(i).getRandomMove(solution, rnd);
            if(move != null){
                // neighbourhood was able to produce a move
                moves.add(move);
                respectiveWeights.add(weights.get(i));
            }
        }
        if(moves.isEmpty()){
            // no moves produced by any neighbourhood
            return null;
        } else {
            // roulette selection to pick a move
            return RouletteSelector.select(moves, respectiveWeights, rnd);
        }
    }

    /**
     * Creates and returns the union of all moves generated by each of the contained neighbourhoods.
     * The returned list may be empty if none of the contained neighbourhoods can generate any move.
     * 
     * @param solution solution for which all moves are generated
     * @return union of all possible moves generated by each contained neighbourhood
     */
    @Override
    public List<Move<? super SolutionType>> getAllMoves(SolutionType solution) {
        return neighs.stream()
                     .flatMap(neigh -> neigh.getAllMoves(solution).stream()) // flatten to one stream of all moves
                     .collect(Collectors.toList());                          // collect in one list
    }

}
