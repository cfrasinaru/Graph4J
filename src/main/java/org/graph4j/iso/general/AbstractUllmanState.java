package org.graph4j.iso.general;

import org.graph4j.Digraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for the Ullman's algorithm: exact isomorphism and subgraph isomorphism.
 *
 * <p>
 *     Based on the paper " J.R. Ullmann, An Algorithm for Subgraph Isomorphism, Journal of the Association for Computing Machinery, 1976"
 * </p>
 * <p>
 *     A matrix M is used to store the compatibility between the vertices of the two graphs.
 * </p>
 * <p>
 *     After a pair of vertices is added to the matching, the matrix M is refined, in order to remove the inconsistent pairs, thus reducing the search space.
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 */
public abstract class AbstractUllmanState extends AbstractState {
    public static final int COMPATIBLE = 0;     // convention: there is no other value that means compatible
    protected int[][] M;            // compatibility matrix
    protected int prev_1, prev_2;   // previously added vertices

    /**
     * @param g1: the first graph, with the vertices ordered by degree
     * @param g2: the second graph, with the vertices ordered by degree
     */
    public AbstractUllmanState(Digraph g1, Digraph g2, boolean cache) {
        this.o1 = new OrderedDigraph(g1, cache);
        this.o2 = new OrderedDigraph(g2, cache);

        this.n1 = o1.getNumVertices();
        this.n2 = o2.getNumVertices();

        this.core_len = 0;

        this.core_1 = new int[n1];
        this.core_2 = new int[n2];
        Arrays.fill(this.core_1, NULL_NODE);
        Arrays.fill(this.core_2, NULL_NODE);

        this.prev_1 = this.prev_2 = NULL_NODE;

        this.M = new int[n1][n2];

        for(int i = 0; i < n1; i++){
            for(int j = 0; j < n2; j++){
                this.M[i][j] = NULL_NODE;

                if (exactOrSubgraphIsomorphismCompatibilityCheck(i, j) &&
                        compatibleVertices(i, j)) {
                    this.M[i][j] = COMPATIBLE;
                }
            }
        }
    }

    public AbstractUllmanState(Digraph g1, Digraph g2) {
        this(g1, g2, false);
    }

    /**
     * Copy constructor: the arrays are just referenced, for memory efficiency
     * @param s: the state to be copied
     */
    public AbstractUllmanState(AbstractUllmanState s) {
        this.o1 = s.o1;
        this.o2 = s.o2;

        this.n1 = s.n1;
        this.n2 = s.n2;
        this.core_len = s.core_len;

        // there is no need to copy, just reference to the array, because we have the backtrack method
        this.M = s.M;
        this.core_1 = s.core_1;
        this.core_2 = s.core_2;

        this.prev_1 = s.prev_1;
        this.prev_2 = s.prev_2;
    }

    /**
     * Compatibility function for the 2 cases: exact and subgraph isomorphism
     */
    public abstract boolean exactOrSubgraphIsomorphismCompatibilityCheck(int v1, int v2);

    /**
     * Finds the next pair of vertices to be matched
     * prev_1 must always be equal to core_len(clasic backtracking behaviour)
     * prev_2 will have values that are compatible with prev_1
     */
    public boolean nextPair(){
        // when we first put the state in the stack, prev_1 and prev_2 are NULL_NODE
        if (prev_1 == NULL_NODE && prev_2 == NULL_NODE){
            prev_1 = core_len;  // which node is currently being matched
            prev_2 = 0;         // start from 0
        } else {
            prev_2++;           // try the next candidate
        }

        // completed the matching
        if (prev_1 >= n1)
            return false;

        // find the value of prev_2 that is compatible with prev_1
        while (prev_2 < n2 && M[prev_1][prev_2] != COMPATIBLE)
            prev_2++;

        if (prev_2 < n2){
            return true;
        }

        // not found
        prev_1 = prev_2 = NULL_NODE;
        return false;
    }

    /**
     * Feasible pair: vertices are compatible
     */
    public boolean isFeasiblePair(){
        return M[prev_1][prev_2] == COMPATIBLE;
    }

    /**
     * Adds the pair (prev_1, prev_2) to the matching
     */
    public void addPair(){
        // add the pair to the matching
        core_1[prev_1] = prev_2;
        core_2[prev_2] = prev_1;

        // increase the size of the matching
        core_len++;

        // in the next steps, we cannot use the same prev_2 vertex for the second graph
        for (int k = core_len; k < n1; k++){
            if (M[k][prev_2] == COMPATIBLE)
                M[k][prev_2] = core_len;
        }

        // update the compatibility matrix
        refineState();
    }

    /**
     * Removes the candidates of some vertices in g1, after a match is found
     */
    private void refineState(){
        for (int i = core_len; i < n1; i++)                     // for the remaining vertices in g1
            for (int j = 0; j < n2; j++)
                if (M[i][j] == COMPATIBLE)                     // for all the candidates in g2
                    if (!existsCandidateNeighbourInSecondGraph(i, j))
                        M[i][j] = core_len;                          // mark as incompatible
    }

    /**
     * Return to the previous state
     */
    public void backTrack(){
        for (int i = core_len; i < n1; i++){
            for (int j = 0; j < n2; j++){
                if (M[i][j] == core_len)     // marked incompatible in the last addPair
                    M[i][j] = COMPATIBLE;
            }
        }

        core_len--;
    }

    /**
     * After a successful addPair(), we must reinitialize prev_1 and prev_2 in order to find the next pair
     */
    public void resetPreviousVertices() {
        prev_1 = prev_2 = NULL_NODE;
    }

    /**
     * Checks if the current state is complete
     */
    public abstract boolean isGoal();

    /**
     * Checks if the current state can be pruned
     */
    public abstract boolean isDead();

    /**
     * For every neighbour of i, there must be at least one candidate that is neighbour of j
     * If this condition is not respected, then j is eliminated as a candidate for i
     *
     * @param i: node from g1
     * @param j: node from g2, some candidate for i
     *
     * @return true if the condition is respected, false otherwise
     */
    protected boolean existsCandidateNeighbourInSecondGraph(int i, int j){
        // for every neighbour of i, there must be at least one candidate neighbour of j
        for (int k = 0 ; k < n1 ; k++){
            boolean edge_ik = o1.containsEdge(i, k);
            boolean edge_ki = o1.containsEdge(k, i);

            // if k is not neighbor of i, continue
            if (!edge_ik && !edge_ki)
                continue;

            boolean found = false;
            for (int l : getCandidates(k)){
                boolean edge_jl = o2.containsEdge(j, l);
                boolean edge_lj = o2.containsEdge(l, j);

                // if edge i->k exists in g1, then must exist at least one edge j->l in g2
                // same for edge k->i and l->j
                if (edge_ik != edge_jl || edge_ki != edge_lj)
                    continue;

                // if the edges i->k and j->l exist, they must be compatible
                if (edge_ik && !compatibleEdges(i, k, j, l))
                    continue;

                // if the edges k->i and l->j exist, they must be compatible
                if (edge_ki && !compatibleEdges(k, i, l, j))
                    continue;

                // found a candidate neighbour of j, that respects the conditions
                found = true;
                break;
            }

            // for a certain neighbour of i, there is no candidate neighbour of j(that respects the conditions)
            if(!found)
                return false;
        }

        return true;
    }

    protected List<Integer> getCandidates(int i){
        List<Integer> candidates = new ArrayList<>();
        if (i < core_len) {
            candidates.add(core_1[i]);
            return candidates;
        }

        for (int j = 0; j < n2; j++)
            if (M[i][j] == COMPATIBLE)
                candidates.add(j);
        return candidates;
    }
}

