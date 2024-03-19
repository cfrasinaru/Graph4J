package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.alg.ordering.VertexOrderings;

import java.util.Arrays;

public abstract class AbstractVF2State extends AbstractState {
    protected int[] order;
    protected int t1in_len;         // number of ingoing vertices from the subgraph G1(s),
    // where s is the current state
    protected int t2in_len;
    protected int t1out_len;
    protected int t2out_len;        // number of outgoing vertices from the subgraph G2(s)
    protected int t1both_len, t2both_len;
    protected int[] in1, in2;   // positive value
    protected int[] out1, out2;
    protected int prev_1, prev_2, last_added1;

    /**
     * @param g1: the first graph, with the vertices ordered by degree
     * @param g2: the second graph, with the vertices ordered by degree
     */
    public AbstractVF2State(Digraph g1, Digraph g2) {
        int[] vertices = VertexOrderings.largestDegreeFirst(g1);
        order = new int[vertices.length];
        for (int i = 0; i < vertices.length; i++)
            order[i] = g1.indexOf(vertices[i]);

        this.g1 = g1;
        this.g2 = g2;

        this.n1 = g1.numVertices();
        this.n2 = g2.numVertices();

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

    /**
     * Copy constructor: the arrays are just referenced, for memory efficiency
     * @param s: the state to be copied
     */
    public AbstractVF2State(AbstractVF2State s){
        this.order = s.order;

        this.g1 = s.g1;
        this.g2 = s.g2;

        this.n1 = s.n1;
        this.n2 = s.n2;

        this.core_len = s.core_len;
        this.t1in_len = s.t1in_len;
        this.t2in_len = s.t2in_len;
        this.t1out_len = s.t1out_len;
        this.t2out_len = s.t2out_len;
        this.t1both_len = s.t1both_len;
        this.t2both_len = s.t2both_len;

        // we can reference them, in order to save some memory
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

    public boolean nextPair() {
        if (prev_1 == NULL_NODE)
            prev_1 = 0;

        if (prev_2 == NULL_NODE)
            prev_2 = 0;
        else
            prev_2++;

        // choose the next vertex from the first graph
        if (t1both_len - core_len > 0 && t2both_len - core_len > 0) {
            while (prev_1 < n1 &&
                    (core_1[prev_1] != NULL_NODE || out1[prev_1] == 0 || in1[prev_1] == 0)) {
                prev_1++;
                prev_2 = 0;
            }
        } else if (t1out_len - core_len > 0 && t2out_len - core_len > 0) {
            while (prev_1 < n1 &&
                    (core_1[prev_1] != NULL_NODE || out1[prev_1] == 0)) {
                prev_1++;
                prev_2 = 0;
            }
        } else if (t1in_len - core_len > 0 && t2in_len - core_len > 0) {
            while (prev_1 < n1 &&
                    (core_1[prev_1] != NULL_NODE || in1[prev_1] == 0)) {
                prev_1++;
                prev_2 = 0;
            }
        } else if (prev_1 == 0 && order != null) {
            int i = 0;
            while (i < n1 && core_1[prev_1 = order[i]] != NULL_NODE)
                i++;
            if (i == n1)
                prev_1 = n1;
        } else {
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

        if (prev_1 < n1 && prev_2 < n2) {
            if (DEBUG)
                showLog(
                        "nextPair", "next candidate pair: (" + g1.vertexAt(prev_1) + ", "
                                + g2.vertexAt(prev_2) + ")");
            return true;
        }

        if (DEBUG)
            showLog("nextPair", "no more candidate pairs");
        return false;
    }

    public boolean isFeasiblePair() {
        assert (prev_1 < n1 && prev_2 < n2);
        assert (core_1[prev_1] == NULL_NODE);
        assert (core_2[prev_2] == NULL_NODE);

        int node_1 = g1.vertexAt(prev_1);
        int node_2 = g2.vertexAt(prev_2);

        if (!compatibleVertices(node_1, node_2))
            return false;

        int term_in1 = 0, term_out1 = 0, new_1 = 0,         // number of unmapped vertices that are going in or out the partial mapping, or neither(meaning they are new)
                term_in2 = 0, term_out2 = 0, new_2 = 0;

        // Check the 'out' edges of node1
        for (int other1 : g1.successors(node_1)) {
            int i = g1.indexOf(other1);
            if (core_1[i] != NULL_NODE) {
                int j = core_1[i];
                int other2 = g2.vertexAt(j);

                if (!g2.containsEdge(node_2, other2) ||
                        !compatibleEdges(g1.edge(node_1, other1), g2.edge(node_2, other2) )) {
                    return false;
                }
            } else {
                if (in1[i] != 0)
                    term_in1++;
                if (out1[i] != 0)
                    term_out1++;
                if (in1[i] == 0 && out1[i] == 0)
                    new_1++;
            }
        }

        // Check the 'out' edges of node2
        for (int other2 : g2.successors(node_2)) {
            int j = g2.indexOf(other2);
            if (core_2[j] != NULL_NODE) {
                int i = core_2[j];
                int other1 = g1.vertexAt(i);

                if (!g1.containsEdge(node_1, other1))
                    return false;
            } else {
                if (in2[j] != 0)
                    term_in2++;
                if (out2[j] != 0)
                    term_out2++;
                if (in2[j] == 0 && out2[j] == 0)
                    new_2++;
            }
        }

        if (!exactOrSubgraphIsomorphismCompatibilityCheck(term_in1, term_out1, term_in2, term_out2, new_1, new_2))
            return false;

        // Check the 'in' edges of node1
        for (int other1 : g1.predecessors(node_1)) {
            int i = g1.indexOf(other1);
            if (core_1[i] != NULL_NODE) {   // if mapped check the presence of the edge in the second graph and its compatibility
                int j = core_1[i];
                int other2 = g2.vertexAt(j);

                if (!g2.containsEdge(other2, node_2) ||
                        !compatibleEdges(g1.edge(other1, node_1),
                                g2.edge(other2, node_2) )) {
                    return false;
                }
            } else {    // not mapped, then update the counters
                if (in1[i] != 0)
                    term_in1++;
                if (out1[i] != 0)
                    term_out1++;
                if (in1[i] == 0 && out1[i] == 0)
                    new_1++;
            }
        }



        // Check the 'in' edges of node2
        for (int other2 : g2.predecessors(node_2)) {
            int j = g2.indexOf(other2);
            if (core_2[j] != NULL_NODE) {
                int i = core_2[j];
                int other1 = g1.vertexAt(i);

                if (!g1.containsEdge(other1, node_1))
                    return false;
            } else {
                if (in2[j] != 0)
                    term_in2++;
                if (out2[j] != 0)
                    term_out2++;
                if (in2[j] == 0 && out2[j] == 0)
                    new_2++;
            }
        }

        return exactOrSubgraphIsomorphismCompatibilityCheck(term_in1, term_out1, term_in2, term_out2, new_1, new_2);
    }

    public abstract boolean exactOrSubgraphIsomorphismCompatibilityCheck(int term_in1, int term_out1, int term_in2, int term_out2, int new_1, int new_2);

    public void addPair() {
        assert (prev_1 < n1 && prev_2 < n2);
        assert (core_len < n1 && core_len < n2);

        core_len++;
        last_added1 = prev_1;

        // vertex prev_1 is added to the mapping, so we need to mark it as in/out
        // also update the in/out/both counters
        if (in1[prev_1] == 0) {
            in1[prev_1] = core_len;
            t1in_len++;

            if (out1[prev_1] != 0)
                t1both_len++;
        }
        if (out1[prev_1] == 0) {
            out1[prev_1] = core_len;
            t1out_len++;

            if (in1[prev_1] != 0)
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

        int node_1 = g1.vertexAt(prev_1);
        int node_2 = g2.vertexAt(prev_2);

        // we added node_1 to the mapping, we mark its predecessors as 'in'
        // also update the in/both counters
        for (int other1 : g1.predecessors(node_1)) {
            int i = g1.indexOf(other1);
            if (in1[i] == 0) {          // not marked, then mark it as 'in'
                in1[i] = core_len;
                t1in_len++;

                if (out1[i] != 0)       // if it is also 'out', increment the 'both' counter
                    t1both_len++;
            }
        }

        // we mark its successors as 'out'
        // also update the out/both counters
        for (int other1 : g1.successors(node_1)) {
            int i = g1.indexOf(other1);
            if (out1[i] == 0) {         // not marked, then mark it as 'out'
                out1[i] = core_len;
                t1out_len++;

                if (in1[i] != 0)        // if it is also 'in', increment the 'both' counter
                    t1both_len++;
            }
        }

        // we added node_2 to the mapping, we mark its predecessors as 'in'
        // also update the in/both counters
        for (int other2 : g2.predecessors(node_2)) {
            int j = g2.indexOf(other2);
            if (in2[j] == 0) {
                in2[j] = core_len;
                t2in_len++;

                if (out2[j] != 0)
                    t2both_len++;
            }
        }

        // we mark its successors as 'out'
        // also update the out/both counters
        for (int other2 : g2.successors(node_2)) {
            int j = g2.indexOf(other2);
            if (out2[j] == 0) {
                out2[j] = core_len;
                t2out_len++;

                if (in2[j] != 0)
                    t2both_len++;
            }
        }
    }

    public void backTrack() {
        assert last_added1 != NULL_NODE;

        int prev_1 = last_added1;
        int node_1 = g1.vertexAt(last_added1);

        if (in1[prev_1] == core_len)
            in1[prev_1] = 0;
        for (int other1 : g1.predecessors(node_1)) {
            int i = g1.indexOf(other1);
            if (in1[i] == core_len)
                in1[i] = 0;
        }

        if (out1[prev_1] == core_len)
            out1[prev_1] = 0;
        for (int other1 : g1.successors(node_1)) {
            int i = g1.indexOf(other1);
            if (out1[i] == core_len)
                out1[i] = 0;
        }

        int prev_2 = core_1[last_added1];
        int node_2 = g2.vertexAt(prev_2);

        if (in2[prev_2] == core_len)
            in2[prev_2] = 0;
        for (int other2 : g2.predecessors(node_2)) {
            int j = g2.indexOf(other2);
            if (in2[j] == core_len)
                in2[j] = 0;
        }

        if (out2[prev_2] == core_len)
            out2[prev_2] = 0;
        for (int other2 : g2.successors(node_2)) {
            int j = g2.indexOf(other2);
            if (out2[j] == core_len)
                out2[j] = 0;
        }

        core_1[prev_1] = NULL_NODE;
        core_2[prev_2] = NULL_NODE;

        last_added1 = NULL_NODE;
    }

    public abstract boolean isGoal();

    public abstract boolean isDead();

    public void resetPreviousVertices() {
        prev_1 = prev_2 = NULL_NODE;
    }

}


