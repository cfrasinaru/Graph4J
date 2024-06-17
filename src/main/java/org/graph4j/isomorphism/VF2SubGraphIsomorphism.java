package org.graph4j.isomorphism;

import org.graph4j.Digraph;
import org.graph4j.Graph;

/**
 * Class for the VF2 algorithm for exact graph isomorphism.
 *
 * <p>
 *     Based on the paper "A (sub)graph isomorphism algorithm for matching large graphs - Cordella, L.P. and
 * Foggia, P. and Sansone, C. and Vento, M. - IEEE Transactions on Pattern Analysis and
 * Machine Intelligence, 2004 (10.1109/TPAMI.2004.75)" and the original implementation done by
 * MIVIA research Lab of the University of Salerno</p>
 *
 * This implementation of the VF2 algorithm does support all types of graphs.
 *
 * @author Ignat Gabriel-Andrei
 */
public class VF2SubGraphIsomorphism extends AbstractGraphIsomorphism {
    public VF2SubGraphIsomorphism(Graph g1, Graph g2) {
        super(g1, g2);
    }

    public VF2SubGraphIsomorphism(Graph g1, Graph g2, boolean cache) {
        super(g1, g2, cache);
    }
    @Override
    protected State getStateInstance(Digraph g1, Digraph g2, boolean cache) {
        return new VF2SubState(g1, g2, cache);
    }

    @Override
    protected State getNewStateInstance(State s) {
        return new VF2SubState((VF2SubState) s);
    }
}
