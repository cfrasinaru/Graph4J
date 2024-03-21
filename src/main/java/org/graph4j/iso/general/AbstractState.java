package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.DirectedMultigraph;
import org.graph4j.DirectedPseudograph;
import org.graph4j.Edge;
import org.graph4j.iso.IsomorphicGraphMapping;

import java.util.Arrays;

public abstract class AbstractState implements State {
    protected Digraph g1;
    protected Digraph g2;
    protected int n1, n2;
    protected int core_len;         // length of current mapping
    protected int[] core_1;         // forward mapping
    protected int[] core_2;         // backward mapping

    public int getCoreLen()
    {
        return core_len;
    }

    public IsomorphicGraphMapping getMapping() {
        int[] forwardMap = new int[n1];
        int[] backwardMap = new int[n2];

        for (int i = 0; i < n1; i++)
            forwardMap[i] = g2.vertexAt(core_1[i]);

        for (int j = 0; j < n2; j++) {
            int i = core_2[j];
            if (i != NULL_NODE)
                backwardMap[j] = g1.vertexAt(core_2[j]);
        }

        return new IsomorphicGraphMapping(forwardMap, backwardMap, g1, g2);
    }

    /**
     * If the graphs allow self loops, the vertices must have the same number of self loops
     * @param x vertex from the first graph
     * @param y candidate vertex from the second graph
     */
    protected boolean compatibleVertices(int x, int y) {
        if (g1 instanceof DirectedPseudograph ps1 &&
                g2 instanceof DirectedPseudograph ps2){
            int nr1 = ps1.selfLoops(x);
            int nr2 = ps2.selfLoops(y);
            if (DEBUG) {
                showLog("compatibleVertices", "nr1: " + nr1 + " nr2: " + nr2);
            }
            return nr1 == nr2;
        }
        return true;
    }

    /**
     * If the graphs allow multiple edges, the edges must have the same multiplicity
     * @param edge1 edge from the first graph
     * @param edge2 candidate edge from the second graph
     */
    protected boolean compatibleEdges(Edge<?> edge1, Edge<?> edge2) {
        if (g1 instanceof DirectedMultigraph mg1 &&
                g2 instanceof DirectedMultigraph mg2){

            return mg1.multiplicity(edge1) == mg2.multiplicity(edge2);
        }
        return true;
    }

    protected void showLog(String method, String str)
    {
        if (!DEBUG) {
            return;
        }

        char[] indent = new char[2 * core_len];
        Arrays.fill(indent, ' ');
        System.out.println((new String(indent)) + method + "> " + str);
    }
}

