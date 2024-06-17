package org.graph4j.isomorphism;

import org.graph4j.Digraph;

import java.util.List;

/**
 * Class for the Ullman algorithm for exact graph isomorphism.
 * Two vertices are compatible if they have the same degree and same labels.
 *
 * @author Ignat Gabriel-Andrei
 */
public class UllmanExactState extends AbstractUllmanState {
    public UllmanExactState(Digraph g1, Digraph g2) {
        super(g1, g2);
    }

    public UllmanExactState(Digraph g1, Digraph g2, boolean cache) {
        super(g1, g2, cache);
    }

    public UllmanExactState(UllmanExactState s) {
        super(s);
    }

    /**
     * For exact isomorphism, two vertices are compatible if they have the same degree.
     * @param vertexIndex1 the index of the vertex in the first graph (in the list of vertices ordered by degree)
     * @param vertexIndex2 the index of the vertex in the second graph (in the list of vertices ordered by degree)
     * @return true if the vertices are compatible, false otherwise
     */
    @Override
    public boolean exactOrSubgraphIsomorphismCompatibilityCheck(int vertexIndex1, int vertexIndex2) {
        return o1.indegree(vertexIndex1) == o2.indegree(vertexIndex2) &&
                o1.outdegree(vertexIndex1) == o2.outdegree(vertexIndex2);
    }

    /**
     * For exact isomorphism, a mapping is complete when all vertices from both graphs are mapped.
     */
    @Override
    public boolean isGoal() {
        return core_len == n1 && core_len == n2;
    }

    /**
     * A partial mapping is "dead" if there is a vertex from the first graph that
     * has no compatible vertex in the second graph.
     */
    @Override
    public boolean isDead() {
        if (n1 != n2)       // if the graphs have different number of vertices, there is no isomorphism
            return true;

        for (int i = 0; i < n1; i++){
            // for each vertex in g1, there is at least one candidate in g2
            List<Integer> candidates = getCandidates(i);
            if (candidates.isEmpty())
                return true;
        }
        return false;
    }
}

