package org.graph4j.isomorphism;

import org.graph4j.Digraph;
import java.util.List;

/**
 * Class for the Ullman algorithm for exact graph isomorphism. Two vertices are
 * compatible if the degree in the first graph is less than or equal to the
 * degree in the second graph.
 *
 * @author Ignat Gabriel-Andrei
 */
public class UllmanSubState extends AbstractUllmanState {

    public UllmanSubState(Digraph g1, Digraph g2) {
        super(g1, g2);
    }

    public UllmanSubState(Digraph g1, Digraph g2, boolean cache) {
        super(g1, g2, cache);
    }

    public UllmanSubState(UllmanSubState s) {
        super(s);
    }

    /**
     * For exact isomorphism, two vertices are compatible if the degree in the
     * first graph is less than or equal to the degree in the second graph.
     *
     * @param vertexIndex1 the index of the vertex in the first graph (in the
     * list of vertices ordered by degree)
     * @param vertexIndex2 the index of the vertex in the second graph (in the
     * list of vertices ordered by degree)
     *
     * @return {@code true} if the vertices are compatible, false otherwise.
     */
    @Override
    public boolean exactOrSubgraphIsomorphismCompatibilityCheck(int vertexIndex1, int vertexIndex2) {
        return o1.indegree(vertexIndex1) <= o2.indegree(vertexIndex2)
                && o1.outdegree(vertexIndex1) <= o2.outdegree(vertexIndex2);
    }

    /**
     * For subgraph isomorphism, a mapping is complete when all vertices from
     * the first graph are mapped.
     *
     * @return {@code true} if the current state is complete.
     */
    @Override
    public boolean isGoal() {
        return core_len == n1;
    }

    /**
     * A partial mapping is "dead" if there is a vertex from the first graph
     * that has no compatible vertex in the second graph.
     *
     * @return {@code true} if the current state is dead.
     */
    @Override
    public boolean isDead() {
        if (n1 > n2) // if the first graph has more vertices than the second graph, there is no subgraph isomorphism
        {
            return true;
        }

        for (int i = 0; i < n1; i++) {
            List<Integer> candidates = getCandidates(i);
            if (candidates.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
