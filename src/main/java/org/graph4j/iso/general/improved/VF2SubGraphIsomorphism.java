package org.graph4j.iso.general.improved;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.iso.general.AbstractGraphIsomorphism;
import org.graph4j.iso.general.State;

public class VF2SubGraphIsomorphism extends AbstractGraphIsomorphism {
    public VF2SubGraphIsomorphism(Graph g1, Graph g2) {
        super(g1, g2);
    }
    @Override
    protected State getStateInstance(Digraph g1, Digraph g2) {
        return new VF2SubState(g1, g2);
    }

    @Override
    protected State getNewStateInstance(State s) {
        return new VF2SubState((VF2SubState) s);
    }
}
