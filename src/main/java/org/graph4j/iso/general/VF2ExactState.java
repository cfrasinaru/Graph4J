package org.graph4j.iso.general;

import org.graph4j.Digraph;

/**
 * Class for the VF2 algorithm for exact graph isomorphism.
 *
 * @author Ignat Gabriel-Andrei
 */
public class VF2ExactState extends AbstractVF2State {
    public VF2ExactState(Digraph g1, Digraph g2) {
        super(g1, g2);
    }

    public VF2ExactState(Digraph g1, Digraph g2, boolean cache) {
        super(g1, g2, cache);
    }

    public VF2ExactState(VF2ExactState s){
        super(s);
    }

    /**
     * For exact isomorphism, a new pair of candidate vertices are feasible if
     * the number of 'in'/'out'/'new' vertices in the subgraph G1(s) is the same as in the subgraph G2(s)
     * @param term_in1  number of unmapped vertices that are going in the subgraph G1(s)
     * @param term_out1 number of unmapped vertices that are going out the subgraph G1(s)
     * @param term_in2  number of unmapped vertices that are going in the subgraph G2(s)
     * @param term_out2 number of unmapped vertices that are going out the subgraph G2(s)
     * @param new_1     number of unmapped vertices that were not yet marked as 'in' or 'out'
     * @param new_2     number of unmapped vertices that were not yet marked as 'in' or 'out'
     */
    @Override
    public boolean exactOrSubgraphIsomorphismCompatibilityCheck(int term_in1, int term_out1, int term_in2, int term_out2, int new_1, int new_2) {
        return term_in1 == term_in2 &&
                term_out1 == term_out2 &&
                new_1 == new_2;
    }

    /**
     * For exact isomorphism, a state is complete if all vertices from the both graphs are mapped.
     */
    @Override
    public boolean isGoal() {
        return core_len == n1 && core_len == n2;
    }

    /**
     * For exact isomorphism, a state is 'dead' if the number of 'in'/'out'/'both' vertices
     * in the subgraph G1(s) is different from that in the subgraph G2(s)
     */
    @Override
    public boolean isDead() {
        return n1 != n2 ||
                t1both_len != t2both_len ||
                t1out_len != t2out_len ||
                t1in_len != t2in_len ;
    }


}
