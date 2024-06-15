package org.graph4j.iso.general;

import org.graph4j.DirectedMultigraph;
import org.graph4j.DirectedPseudograph;
import org.graph4j.Edge;
import org.graph4j.iso.IsomorphicGraphMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for the state of the search algorithm.
 *
 * <p>
 *     A state is a partial solution of the graph isomorphism problem.
 * </p>
 * <p>
 *     It has fields for the current partial forward/backward mapping.
 * </p>
 * <p>
 *     A partial mapping works only with indices of the vertices in the ordered digraph(meaning the position in the sorted list of the vertices).
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 */
public abstract class AbstractState implements State {
    protected OrderedDigraph o1, o2;    // the ordered digraphs
    protected int n1, n2;               // number of vertices in the ordered digraphs
    protected int core_len;         // length of current mapping
    protected int[] core_1;         // forward mapping; core_1[i] = j means that the i-th
                                    // vertex in the sorted order of the first digraph is mapped
                                    // to the j-th vertex in the sorted order of the second digraph
    protected int[] core_2;         // backward mapping

    public int getCoreLen()
    {
        return core_len;
    }

    /**
     * Computes the mapping when a complete solution is found.
     * @return isomorphic graph mapping
     */
    @Override
    public IsomorphicGraphMapping getMapping() {
        int[] forwardMap = new int[n1];
        int[] backwardMap = new int[n2];

        for (int v1 : o1.getGraph().vertices()) {
            int index_1 = o1.getVertexOrder(v1);    // index in the sorted list of vertices
            int index_2 = core_1[index_1];          // index of the mapped vertex in the second digraph

            // the index of the vertex in the first digraph is mapped to the vertex in the second digraph
            forwardMap[o1.getGraph().indexOf(v1)] = o2.getVertexNumber(index_2);
        }

        for (int v2 : o2.getGraph().vertices()) {
            int index_2 = o2.getVertexOrder(v2);    // index in the sorted list of vertices
            int index_1 = core_2[index_2];          // index of the mapped vertex in the first digraph
            if (index_1 != NULL_NODE) {
                // the index of the vertex in the second digraph is mapped to the vertex in the first digraph
                backwardMap[o2.getGraph().indexOf(v2)] = o1.getVertexNumber(index_1);
            }
        }

        return new IsomorphicGraphMapping(forwardMap, backwardMap, o1.getGraph(), o2.getGraph());
    }

    /**
     * If the graphs allow self loops, the vertices must have the same number of self loops
     * @param vertexIndex1 the index of a vertex from the ordered digraph 1  (the index in the sorted list of vertices)
     * @param vertexIndex2 the index of a vertex from the ordered digraph 2  (the index in the sorted list of vertices)
     *
     * @return true if the vertices are compatible, false otherwise
     */
    protected boolean compatibleVertices(int vertexIndex1, int vertexIndex2) {
        int node_1 = o1.getVertexNumber(vertexIndex1);
        int node_2 = o2.getVertexNumber(vertexIndex2);

        // semantic equivalence
        if (o1.getGraph().getVertexLabel(node_1) != null && o2.getGraph().getVertexLabel(node_2) != null) {
            if (!o1.getGraph().getVertexLabel(node_1).equals(o2.getGraph().getVertexLabel(node_2))) {
                return false;
            }
        }

        if (o1.getGraph() instanceof DirectedPseudograph ps1 && o2.getGraph() instanceof DirectedPseudograph ps2){
            if (ps1.selfLoops(node_1) != ps2.selfLoops(node_2)) {
                return false;
            }

            // L1 = list of all labels of self loops of vertex node_1
            // L2 = list of all labels of self loops of vertex node_2

            List<Object> labels1 = new ArrayList<>();
            List<Object> labels2 = new ArrayList<>();

            for (Edge e : ps1.outgoingEdgesFrom(node_1)) {
                if (e != null && e.target() == node_1) {
                    labels1.add(e.label());
                }
            }
            for (Edge e : ps2.outgoingEdgesFrom(node_2)) {
                if (e != null && e.target() == node_2) {
                    labels2.add(e.label());
                }
            }

            if (labels1.size() != labels2.size()) {
                return false;
            }

            for (Object label : labels1) {
                boolean removed = labels2.remove(label);
                if (!removed) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    /**
     * If the graphs allow multiple edges, the edges must have the same multiplicity
     * @param i1 the index of the source vertex of the edge in the ordered digraph 1
     * @param j1 the index of the target vertex of the edge in the ordered digraph 1
     *
     * @param i2 the index of the source vertex of the edge in the ordered digraph 2
     * @param j2 the index of the target vertex of the edge in the ordered digraph 2
     *
     * @return true if the edges are compatible, false otherwise
     */
    protected boolean compatibleEdges(int i1, int j1, int i2, int j2) {
        int u1 = o1.getVertexNumber(i1);
        int v1 = o1.getVertexNumber(j1);
        int u2 = o2.getVertexNumber(i2);
        int v2 = o2.getVertexNumber(j2);

        if (o1.getGraph() instanceof DirectedMultigraph mg1 && o2.getGraph() instanceof DirectedMultigraph mg2){
            if (mg1.multiplicity(u1, v1) != mg2.multiplicity(u2, v2)) {
                return false;
            }
            // L1 = list of all labels of edge (u1, v1)
            // L2 = list of all labels of edge (u2, v2)
            // every label from L1 must be in L2 and vice versa

            List<Object> labels1 = new ArrayList<>();
            List<Object> labels2 = new ArrayList<>();

            for (Edge e : mg1.outgoingEdgesFrom(u1)) {
                if (e != null && e.target() == v1) {
                    labels1.add(e.label());
                }
            }
            for (Edge e : mg2.outgoingEdgesFrom(u2)) {
                if (e != null && e.target() == v2) {
                    labels2.add(e.label());
                }
            }

            if (labels1.size() != labels2.size()) {
                return false;
            }

            for (Object label : labels1) {
                boolean removed = labels2.remove(label);
                if (!removed) {
                    return false;
                }
            }
            return true;
        } else // not multi edges
            if (o1.getGraph().getEdgeLabel(u1, v1) != null && o2.getGraph().getEdgeLabel(u2, v2) != null) {
            return o1.getGraph().getEdgeLabel(u1, v1).equals(o2.getGraph().getEdgeLabel(u2, v2));
        }

        return true;
    }
}