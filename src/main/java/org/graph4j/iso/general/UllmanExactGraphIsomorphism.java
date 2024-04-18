package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.Graph;

/**
 * Class for the Ullman algorithm for exact graph isomorphism.
 *
 * <p>
 *     Based on the paper " J.R. Ullmann, An Algorithm for Subgraph Isomorphism, Journal of the
 * Association for Computing Machinery, 1976"
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 */
public class UllmanExactGraphIsomorphism extends AbstractGraphIsomorphism {
    public UllmanExactGraphIsomorphism(Graph g1, Graph g2, boolean cache) {
        super(g1, g2, cache);
    }

    public UllmanExactGraphIsomorphism(Graph g1, Graph g2) {
        super(g1, g2);
    }

    @Override
    protected State getStateInstance(Digraph g1, Digraph g2, boolean cache) {
        return new UllmanExactState(g1, g2, cache);
    }

    @Override
    protected State getNewStateInstance(State s) {
        return new UllmanExactState((UllmanExactState) s);
    }
}

