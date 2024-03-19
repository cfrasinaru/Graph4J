package org.graph4j.iso.general;

import org.graph4j.Digraph;

public class VF2ExactState extends AbstractVF2State {
    public VF2ExactState(Digraph g1, Digraph g2) {
        super(g1, g2);
    }

    public VF2ExactState(VF2ExactState s){
        super(s);
    }

    @Override
    public boolean exactOrSubgraphIsomorphismCompatibilityCheck(int term_in1, int term_out1, int term_in2, int term_out2, int new_1, int new_2) {
        return term_in1 == term_in2 &&
                term_out1 == term_out2 &&
                new_1 == new_2;
    }

    @Override
    public boolean isGoal() {
        return core_len == n1 &&
                core_len == n2;
    }

    @Override
    public boolean isDead() {
        return n1 != n2 ||
                t1both_len != t2both_len ||
                t1out_len != t2out_len ||
                t1in_len != t2in_len ;
    }
}

