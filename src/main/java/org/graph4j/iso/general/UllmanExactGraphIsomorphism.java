package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.Graph;

public class UllmanExactGraphIsomorphism extends AbstractGraphIsomorphism {
    public UllmanExactGraphIsomorphism(Graph g1, Graph g2) {
        super(g1, g2);
    }
    @Override
    protected State getStateInstance(Digraph g1, Digraph g2) {
        return new UllmanExactState(g1, g2);
    }

    @Override
    protected State getNewStateInstance(State s) {
        return new UllmanExactState((UllmanExactState) s);
    }
}
