package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.iso.GraphUtil;
import org.graph4j.iso.IsomorphicGraphMapping;
import org.graph4j.iso.general.GraphIsomorphism;
import org.graph4j.iso.general.State;

import java.util.*;

/**
 * Abstract class for finding the isomorphism between two graphs.
 *
 * <p>
 *     It provides a method for finding all the mappings between two graphs and a method for finding the first mapping.
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 */
public abstract class AbstractGraphIsomorphism implements GraphIsomorphism {
    protected final Digraph dg1;
    protected final Digraph dg2;
    protected final boolean cache;

    /**
     * Constructor for the AbstractGraphIsomorphism class.
     *
     * @param g1 the first graph -> it is converted to a digraph
     * @param g2 the second graph -> it is converted to a digraph
     * @param cache if true, the algorithm will cache the successors, predecessors, the adjacency relations
     */
    public AbstractGraphIsomorphism(Graph g1, Graph g2, boolean cache) {
        validateGraphs(g1, g2);
        this.dg1 = GraphUtil.convertToDigraph(g1);
        this.dg2 = GraphUtil.convertToDigraph(g2);
        this.cache = cache;
    }

    public AbstractGraphIsomorphism(Graph g1, Graph g2) {
        this(g1, g2, false);
    }

    /**
     * Validates the graphs.
     * @param g1 the first graph
     * @param g2 the second graph
     * @throws NullPointerException if the graphs are null
     * @throws IllegalArgumentException if the graphs have different types
     */
    private void validateGraphs(Graph<?,?> g1, Graph<?,?> g2) {
        if(g1 == null || g2 == null)
            throw new NullPointerException("Graphs cannot be null");

        // they must have the same type(directed, pseudo graph, multi graph, graph)
        if(g1.isDirected() != g2.isDirected())
            throw new IllegalArgumentException("Graphs must have the same type");

        if(g1.isAllowingSelfLoops() != g2.isAllowingSelfLoops())
            throw new IllegalArgumentException("Graphs must have the same type");

        if(g1.isAllowingMultipleEdges() != g2.isAllowingMultipleEdges())
            throw new IllegalArgumentException("Graphs must have the same type");
    }

    /**
     * Checks if the two graphs are isomorphic by computing the first mapping, if it exists.
     * @return true if the graphs are isomorphic, false otherwise
     */
    @Override
    public boolean areIsomorphic() {
        Optional<IsomorphicGraphMapping> mappings = getMapping();
        return mappings.isPresent();
    }

    /**
     * @return the first mapping between the two graphs, if it exists.
     */
    @Override
    public Optional<IsomorphicGraphMapping> getMapping() {
        List<IsomorphicGraphMapping> mappings = match(true);
        return mappings.isEmpty() ? Optional.empty() : Optional.of(mappings.get(0));
    }

    /**
     * @return all the mappings between the two graphs.
     */
    @Override
    public List<IsomorphicGraphMapping> getAllMappings() {
        return match(false);
    }

    /**
     * Computes the mappings between the two graphs.
     * @param onlyFirstMapping if true, the method will return only the first mapping
     * @return a list of all the mappings between the two graphs
     */
    private List<IsomorphicGraphMapping> match(boolean onlyFirstMapping){
        return matchIterative(onlyFirstMapping);
    }


    /**
     * An iterative version of the match method, it simulates the recursive approach.
     * By doing this,we might gain some performance, because we avoid the overhead of the recursive calls.
     *
     * @return a list of the mappings between the two graphs
     */
    private List<IsomorphicGraphMapping> matchIterative(boolean onlyFirstMapping){
        List<IsomorphicGraphMapping> mappings = new ArrayList<>();

        // stack for simulating the recursive calls
        Deque<State> stack = new ArrayDeque<>();

        // the initial state with an empty mapping
        State s = getStateInstance(this.dg1, this.dg2, this.cache);

        // while there are more states to explore
        while(true){
            // while for the current state, there are more candidate pairs
            while(s.nextPair()){

                // if the pair is feasible, we continue, otherwise we truncate the branch
                if(s.isFeasiblePair()){

                    // if the state is dead, we continue with the next pair
                    if(s.isDead()) {
                        continue;
                    }

                    // else we add the pair to the mapping
                    stack.push(s);

                    // just like in the recursive approach, we create a copy of the current state and then we add the pair
                    s = getNewStateInstance(s);
                    s.addPair();

                    // if this state is a goal(complete solution), we add the mapping to the list
                    if(s.isGoal()) {
                        mappings.add(s.getMapping());

                        // if we need only the first mapping, we return it
                        if(onlyFirstMapping)
                            return mappings;
                    }

                    s.resetPreviousVertices();
                }
            }

            if(stack.isEmpty())
                break;

            // if we have no more pairs to explore, we backtrack(get to the previous state)
            s.backTrack();
            s = stack.pop();
        }

        return mappings;
    }

    /**
     *  Instantiates a new empty state.
     */
    protected abstract State getStateInstance(Digraph g1, Digraph g2, boolean cache);

    /**
     * Instantiates a new state with the same properties as the given state.
     */
    protected abstract State getNewStateInstance(State s);


    /*    protected List<IsomorphicGraphMapping> match(boolean onlyFirstMapping){
            List<IsomorphicGraphMapping> mappings = new ArrayList<>();
            matchRecursive(getStateInstance(dg1, dg2), mappings, onlyFirstMapping);

            return mappings;
        }

        private void matchRecursive(State s, List<IsomorphicGraphMapping> mappings, boolean onlyFirstMapping) {
            if(s.isGoal()){
                mappings.add(s.getMapping());
                return;
            }

            if (s.isDead())
                return;

            while(s.nextPair()){
                if(s.isFeasiblePair()){
                    State newS = getNewStateInstance(s);
                    newS.addPair();
                    newS.resetPreviousVertices();

                    matchRecursive(newS, mappings, onlyFirstMapping);
                    newS.backTrack();
                    if(onlyFirstMapping && !mappings.isEmpty())
                        return;
                }
            }
        }*/
}
