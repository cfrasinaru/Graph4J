package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.Graph;

public class VF2ExactGraphIsomorphism extends AbstractGraphIsomorphism {
    public VF2ExactGraphIsomorphism(Graph g1, Graph g2) {
        super(g1, g2);
    }
    @Override
    protected State getStateInstance(Digraph g1, Digraph g2) {
        return new VF2ExactState(g1, g2);
    }

    @Override
    protected State getNewStateInstance(State s) {
        return new VF2ExactState((VF2ExactState) s);
    }
}

