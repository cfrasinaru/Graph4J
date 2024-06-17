package org.graph4j.isomorphism;

import org.graph4j.Digraph;
import org.graph4j.Graph;

/**
 * Class for the Ullman algorithm for subgraph isomorphism.
 *
 * <p>
 *     Based on the paper " J.R. Ullmann, An Algorithm for Subgraph Isomorphism, Journal of the
 * Association for Computing Machinery, 1976"
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 */
public class UllmanSubGraphIsomorphism extends AbstractGraphIsomorphism {
    public UllmanSubGraphIsomorphism(Graph g1, Graph g2) {
        super(g1, g2);
    }

    public UllmanSubGraphIsomorphism(Graph g1, Graph g2, boolean cache) {
        super(g1, g2, cache);
    }
    @Override
    protected State getStateInstance(Digraph g1, Digraph g2, boolean cache) {
        return new UllmanSubState(g1, g2, cache);
    }

    @Override
    protected State getNewStateInstance(State s) {
        return new UllmanSubState((UllmanSubState) s);
    }
}

