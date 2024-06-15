package org.graph4j.iso.general;

import org.graph4j.Digraph;
import java.util.Arrays;

/**
 * Abstract class for the VF2(Vento Foggia) algorithm.
 *
 * <p>
 *     Based on the paper "A (sub)graph isomorphism algorithm for matching large graphs - Cordella, L.P. and Foggia, P. and Sansone, C. and Vento, M. - IEEE Transactions on Pattern Analysis and
 * Machine Intelligence, 2004 (10.1109/TPAMI.2004.75)" and the original implementation done by
 * MIVIA research Lab of the University of Salerno
 * </p>
 *
 * This implementation of the VF2 algorithm was adapted to support all types of graphs.
 *
 * @author Ignat Gabriel-Andrei
 */
public abstract class AbstractVF2State extends AbstractState {
    protected int t1in_len;         // number of ingoing vertices from the subgraph G1(s),
                                    // where s is the current state(partial mapping)
                                    // The mapped vertices are also counted as 'in'/'out'/'both'
    protected int t2in_len;
    protected int t1out_len;
    protected int t2out_len;
    protected int t1both_len, t2both_len;
    protected int[] in1, in2;       // 0 if the vertex is new(undiscovered yet),
                                    // positive if it is mapped or marked as ingoing vertex to the partial mapping
    protected int[] out1, out2;
    protected int prev_1, prev_2, last_added1;  // previously added vertices

    /**
     * Constructor for the initial state of the search algorithm.
     * @param g1: the first graph
     * @param g2: the second graph
     */
    public AbstractVF2State(Digraph g1, Digraph g2, boolean cache) {
        this.o1 = new OrderedDigraph(g1, cache);
        this.o2 = new OrderedDigraph(g2, cache);

        this.n1 = o1.getNumVertices();
        this.n2 = o2.getNumVertices();

        this.core_len = 0;
        this.t1in_len = this.t1out_len = 0;
        this.t2in_len = this.t2out_len = 0;

        this.core_1 = new int[n1];      Arrays.fill(this.core_1, NULL_NODE);
        this.core_2 = new int[n2];      Arrays.fill(this.core_2, NULL_NODE);

        this.in1 = new int[n1];         Arrays.fill(this.in1, 0);
        this.out1 = new int[n1];        Arrays.fill(this.out1, 0);

        this.in2 = new int[n2];         Arrays.fill(this.in2, 0);
        this.out2 = new int[n2];        Arrays.fill(this.out2, 0);

        this.prev_1 = this.prev_2 = this.last_added1 = NULL_NODE;
    }

    public AbstractVF2State(Digraph g1, Digraph g2) {
        this(g1, g2, false);
    }

    /**
     * Copy constructor: the arrays are just referenced, for memory efficiency.
     * @param s: the state to be copied
     */
    public AbstractVF2State(AbstractVF2State s){
        this.n1 = s.n1;
        this.n2 = s.n2;

        this.o1 = s.o1;
        this.o2 = s.o2;

        this.core_len = s.core_len;
        this.t1in_len = s.t1in_len;
        this.t2in_len = s.t2in_len;
        this.t1out_len = s.t1out_len;
        this.t2out_len = s.t2out_len;
        this.t1both_len = s.t1both_len;
        this.t2both_len = s.t2both_len;

        // we can reference them, in order to save me memory
        // we'll use backtrack() method to restore the previous state
        this.core_1 = s.core_1;
        this.core_2 = s.core_2;

        this.in1 = s.in1;
        this.in2 = s.in2;
        this.out1 = s.out1;
        this.out2 = s.out2;


        this.prev_1 = s.prev_1;
        this.prev_2 = s.prev_2;
        this.last_added1 = NULL_NODE;
    }

    /**
     * Computes the next pair of candidate vertices for the isomorphism.
     * If the previous vertices are not set, they are initialized with 0.
     * @return true if a pair was found, false otherwise
     */
    public boolean nextPair() {
        if (prev_1 == NULL_NODE)
            prev_1 = 0;

        if (prev_2 == NULL_NODE)
            prev_2 = 0;
        else
            prev_2++;

        // choose the next vertex from the first graph
        // check 'both' vertices first(meaning they have both in and out edges)
        if (t1both_len - core_len > 0 && t2both_len - core_len > 0) {
            while (prev_1 < n1 &&
                    (core_1[prev_1] != NULL_NODE || out1[prev_1] == 0 || in1[prev_1] == 0)) {
                prev_1++;
                prev_2 = 0;
            }
        }
        // else, check 'out' vertices
        else if (t1out_len - core_len > 0 && t2out_len - core_len > 0) {
            while (prev_1 < n1 &&
                    (core_1[prev_1] != NULL_NODE || out1[prev_1] == 0)) {
                prev_1++;
                prev_2 = 0;
            }
        }
        // else, check 'in' vertices
        else if (t1in_len - core_len > 0 && t2in_len - core_len > 0) {
            while (prev_1 < n1 &&
                    (core_1[prev_1] != NULL_NODE || in1[prev_1] == 0)) {
                prev_1++;
                prev_2 = 0;
            }
        }
        // else, check the unmapped vertices
        else {
            while (prev_1 < n1 && core_1[prev_1] != NULL_NODE) {
                prev_1++;
                prev_2 = 0;
            }
        }

        // choose the next vertex from the second graph
        if (t1both_len - core_len > 0 && t2both_len - core_len > 0) {
            while (prev_2 < n2 &&
                    (core_2[prev_2] != NULL_NODE || out2[prev_2] == 0 || in2[prev_2] == 0))
                prev_2++;
        } else if (t1out_len - core_len > 0 && t2out_len - core_len > 0) {
            while (prev_2 < n2 &&
                    (core_2[prev_2] != NULL_NODE || out2[prev_2] == 0))
                prev_2++;
        } else if (t1in_len - core_len > 0 && t2in_len - core_len > 0) {
            while (prev_2 < n2 &&
                    (core_2[prev_2] != NULL_NODE || in2[prev_2] == 0))
                prev_2++;
        } else {
            while (prev_2 < n2 && core_2[prev_2] != NULL_NODE)
                prev_2++;
        }

        return prev_1 < n1 && prev_2 < n2;
    }

    /**
     * <p>Checks if the pair of vertices is feasible.
     * Checks if the vertices are compatible.
     * For every edge that is going in/out the partial mapping, we check if the corresponding
     * edge is present in the second graph.</p>
     *
     * <p>We also count the number of unmapped and out/in/new vertices.
     * For exact isomorphism, a new pair of candidate vertices are feasible if
     * these numbers are equal: term_in1 with term_in2, term_out1 with term_out2, new_1 with new_2.</p>
     */
    public boolean isFeasiblePair() {
        if (!compatibleVertices(prev_1, prev_2)) {
            return false;
        }

        int term_in1 = 0, term_out1 = 0, new_1 = 0,         // number of unmapped vertices that are going in or out the partial mapping, or neither(meaning they are new)
                term_in2 = 0, term_out2 = 0, new_2 = 0;

        // Check the 'out' edges of prev_1
        for (int other1 : o1.successors(prev_1)) {
            if (core_1[other1] != NULL_NODE) {  // mapped node
                int other2 = core_1[other1];

                if (!o2.containsEdge(prev_2, other2) ||                         //
                        !compatibleEdges(prev_1, other1, prev_2, other2)) {
                    return false;
                }
            } else {    // not mapped
                if (in1[other1] != 0)   // incoming vertex
                    term_in1++;
                if (out1[other1] != 0)  // outgoing vertex
                    term_out1++;
                if (in1[other1] == 0 && out1[other1] == 0)  // not explored yet
                    new_1++;
            }
        }

        // Check the 'in' edges of node1
        for (int other1 : o1.predecessors(prev_1)) {
            if (core_1[other1] != NULL_NODE) {
                int other2 = core_1[other1];

                if (!o2.containsEdge(other2, prev_2) ||
                        !compatibleEdges(other1, prev_1, other2, prev_2)) {
                    return false;
                }
            } else {    // not mapped, then update the counters
                if (in1[other1] != 0)
                    term_in1++;
                if (out1[other1] != 0)
                    term_out1++;
                if (in1[other1] == 0 && out1[other1] == 0)
                    new_1++;
            }
        }

        // Check the 'in' edges of node2
        for (int other2 : o2.predecessors(prev_2)) {
            if (core_2[other2] != NULL_NODE) {
                int other1 = core_2[other2];

                if (!o1.containsEdge(other1, prev_1)) {
                    return false;
                }
            } else {
                if (in2[other2] != 0)
                    term_in2++;
                if (out2[other2] != 0)
                    term_out2++;
                if (in2[other2] == 0 && out2[other2] == 0)
                    new_2++;
            }
        }


        // Check the 'out' edges of prev_2
        for (int other2 : o2.successors(prev_2)) {
            if (core_2[other2] != NULL_NODE) {
                int other1 = core_2[other2];

                if (!o1.containsEdge(prev_1, other1)) {
                    return false;
                }
            } else {
                if (in2[other2] != 0)
                    term_in2++;
                if (out2[other2] != 0)
                    term_out2++;
                if (in2[other2] == 0 && out2[other2] == 0)
                    new_2++;
            }
        }

        return exactOrSubgraphIsomorphismCompatibilityCheck(term_in1, term_out1, term_in2, term_out2, new_1, new_2);
    }

    /**
     * For exact isomorphism, a new pair of candidate vertices are feasible if
     * these numbers are equal: term_in1 with term_in2, term_out1 with term_out2, new_1 with new_2.
     * @param term_in1  number of unmapped vertices that are going in the subgraph G1(s)
     * @param term_out1 number of unmapped vertices that are going out the subgraph G1(s)
     * @param term_in2  number of unmapped vertices that are going in the subgraph G2(s)
     * @param term_out2 number of unmapped vertices that are going out the subgraph G2(s)
     * @param new_1     number of unmapped vertices that were not yet marked as 'in' or 'out'
     * @param new_2     number of unmapped vertices that were not yet marked as 'in' or 'out'
     * @return  true if the pair is feasible, false otherwise
     */
    public abstract boolean exactOrSubgraphIsomorphismCompatibilityCheck(int term_in1, int term_out1, int term_in2, int term_out2, int new_1, int new_2);

    /**
     * Adds the pair of vertices to the mapping.
     * Updates the in/out/both counters, the in/out vectors and the mapping.
     */
    public void addPair() {
        core_len++;             // increase the size of the mapping
        last_added1 = prev_1;   // save the last added vertex

        // vertex prev_1 is added to the mapping, so we need to mark it as in/out
        // also update the in/out/both counters
        if (in1[prev_1] == 0) {     // if it is not marked, then mark it as 'in'
            in1[prev_1] = core_len;
            t1in_len++;             // increment the 'in' counter

            if (out1[prev_1] != 0)  // if it is also 'out', increment the 'both' counter
                t1both_len++;
        }
        if (out1[prev_1] == 0) {    // if it is not marked, then mark it as 'out'
            out1[prev_1] = core_len;
            t1out_len++;            // increment the 'out' counter

            if (in1[prev_1] != 0)   // if it is also 'in', increment the 'both' counter
                t1both_len++;
        }

        // vertex prev_2 is added to the mapping, so we need to mark it as in/out(is considered to be 'in' and 'out' at the same time)
        // also update the in/out/both counters
        if (in2[prev_2] == 0) {
            in2[prev_2] = core_len;
            t2in_len++;

            if (out2[prev_2] != 0)
                t2both_len++;
        }
        if (out2[prev_2] == 0) {
            out2[prev_2] = core_len;
            t2out_len++;

            if (in2[prev_2] != 0)
                t2both_len++;
        }

        core_1[prev_1] = prev_2;
        core_2[prev_2] = prev_1;

        // we added prev_1 to the mapping, we mark its predecessors as 'in'
        // also update the in/both counters
        for (int other1 : o1.predecessors(prev_1)) {    // ingoing vertices
            if (in1[other1] == 0) {          // not marked, then mark it as 'in'
                in1[other1] = core_len;
                t1in_len++;

                if (out1[other1] != 0)       // if it is also 'out', increment the 'both' counter
                    t1both_len++;
            }
        }

        // we mark its successors as 'out'
        // also update the out/both counters
        for (int other1 : o1.successors(prev_1)) {      // outgoing vertices
            if (out1[other1] == 0) {         // not marked, then mark it as 'out'
                out1[other1] = core_len;
                t1out_len++;

                if (in1[other1] != 0)        // if it is also 'in', increment the 'both' counter
                    t1both_len++;
            }
        }

        // we added prev_2 to the mapping, we mark its predecessors as 'in'
        // also update the in/both counters
        for (int other2 : o2.predecessors(prev_2)) {
            if (in2[other2] == 0) {
                in2[other2] = core_len;
                t2in_len++;

                if (out2[other2] != 0)
                    t2both_len++;
            }
        }

        // we mark its successors as 'out'
        // also update the out/both counters
        for (int other2 : o2.successors(prev_2)) {
            if (out2[other2] == 0) {
                out2[other2] = core_len;
                t2out_len++;

                if (in2[other2] != 0)
                    t2both_len++;
            }
        }
    }

    /**
     * Removes the last added pair of vertices from the mapping.
     * Updates the in/out/both counters, the in/out vectors and the mapping.
     */
    public void backTrack() {
        int prev_1 = last_added1;

        if (in1[prev_1] == core_len)
            in1[prev_1] = 0;
        for (int other1 : o1.predecessors(prev_1)) {
            if (in1[other1] == core_len)    // if it was marked as 'in' at the last step, then unmark it
                in1[other1] = 0;
        }

        if (out1[prev_1] == core_len)
            out1[prev_1] = 0;
        for (int other1 : o1.successors(prev_1)) {
            if (out1[other1] == core_len)   // if it was marked as 'out' at the last step, then unmark it
                out1[other1] = 0;
        }

        int prev_2 = core_1[last_added1];

        if (in2[prev_2] == core_len)
            in2[prev_2] = 0;
        for (int other2 : o2.predecessors(prev_2)) {
            if (in2[other2] == core_len)
                in2[other2] = 0;
        }

        if (out2[prev_2] == core_len)
            out2[prev_2] = 0;
        for (int other2 : o2.successors(prev_2)) {
            if (out2[other2] == core_len)
                out2[other2] = 0;
        }

        core_1[prev_1] = NULL_NODE;
        core_2[prev_2] = NULL_NODE;

        core_len--;
        last_added1 = NULL_NODE;
    }

    public abstract boolean isGoal();

    public abstract boolean isDead();

    public void resetPreviousVertices() {
        prev_1 = prev_2 = NULL_NODE;
    }

}



