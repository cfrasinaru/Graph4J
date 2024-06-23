package org.graph4j.isomorphism;

import java.util.ArrayList;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.connectivity.ConnectivityAlgorithm;
import org.graph4j.metrics.TreeExtremaCalculator;
import org.graph4j.util.RootedTree;
import org.graph4j.util.Validator;
import org.graph4j.util.VertexSet;

/**
 * Algorithm for testing isomorphism of undirected forests.
 *
 * The implementation runs in linear time (in the number of vertices of the
 * input forest).
 *
 * @see TreeIsomorphism
 * @author Cristian Frasinaru
 */
public class ForestIsomorphism implements IsomorphismAlgorithm {

    private final Graph forest1, forest2;
    private RootedTreeIsomorphism rootedTreeAlg;
    private Boolean isomorphic;
    private Isomorphism isomorphism;

    /**
     * Constructor for the forest isomorphism algorithm. It does not verify if
     * the input graphs are actually forests.
     *
     * @param forest1 the first forest.
     * @param forest2 the second forest.
     * @throws NullPointerException if either {@code forest1} or {@code forest2}
     * is {@code null}.
     * @throws IllegalArgumentException if either {@code forest1} or
     * {@code forest2} is not undirected.
     */
    public ForestIsomorphism(Graph forest1, Graph forest2) {
        Validator.requireUndirected(forest1);
        Validator.requireUndirected(forest2);
        this.forest1 = forest1;
        this.forest2 = forest2;
    }

    @Override
    public boolean areIsomorphic() {
        if (isomorphic == null) {
            isomorphic = computeIsomorphic();
        }
        return isomorphic;
    }

    @Override
    public Isomorphism findIsomorphism() {
        if (isomorphism != null) {
            return isomorphism;
        }
        if (!areIsomorphic()) {
            return null;
        }
        int n = forest1.numVertices();
        int[] mapping = new int[n];
        int[] inverse = new int[n];
        var treeIso = rootedTreeAlg.findIsomorphism();
        for (int v : forest1.vertices()) {
            mapping[forest1.indexOf(v)] = treeIso.mapping(v);
        }
        for (int u : forest2.vertices()) {
            inverse[forest2.indexOf(u)] = treeIso.inverse(u);
        }
        isomorphism = new Isomorphism(forest1, forest2, mapping, inverse);
        assert isomorphism.isValid();
        return isomorphism;
    }

    private boolean computeIsomorphic() {
        if (forest1.isEmpty() && forest2.isEmpty()) {
            return true;
        }
        if (!checkTrivialConditions(forest1, forest2)) {
            return false;
        }
        // Determine the connected components
        List<VertexSet> connectedSets1 = new ConnectivityAlgorithm(forest1).getConnectedSets();
        List<VertexSet> connectedSets2 = new ConnectivityAlgorithm(forest2).getConnectedSets();
        if (connectedSets1.size() != connectedSets2.size()) {
            return false;
        }

        // Determine the center of each component
        List<VertexSet> centers1 = computeCenters(forest1, connectedSets1);
        List<VertexSet> centers2 = computeCenters(forest2, connectedSets2);

        int singleCenters1 = (int) centers1.stream().filter(c -> c.size() == 1).count();
        int singleCenters2 = (int) centers2.stream().filter(c -> c.size() == 1).count();
        if (singleCenters1 != singleCenters2) {
            return false;
        }

        // Created two rooted trees and test if they are isomorph
        var rootedTree1 = createRootedTree(forest1, centers1);
        var rootedTree2 = createRootedTree(forest2, centers2);
        rootedTreeAlg = new RootedTreeIsomorphism(rootedTree1, rootedTree2);

        return rootedTreeAlg.areIsomorphic();
    }

    private List<VertexSet> computeCenters(Graph forest, List<VertexSet> connectedSets) {
        int k = connectedSets.size();
        List<VertexSet> centers = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            var set = connectedSets.get(i);
            centers.add(i, new TreeExtremaCalculator(forest, set.peek()).getCenter());
        }
        return centers;
    }

    private RootedTree createRootedTree(Graph forest, List<VertexSet> centers) {
        Graph graph = forest.copy();
        graph.setSafeMode(false);
        int root = graph.addVertex();
        int root1 = graph.addVertex(); //for subtrees with |center|=1
        int root2 = graph.addVertex(); //for subtrees with |center|=2
        graph.addEdge(root, root1);
        graph.addEdge(root, root2);
        for (int i = 0, n = centers.size(); i < n; i++) {
            var center = centers.get(i);
            if (center.size() == 1) {
                graph.addEdge(root1, center.peek());
            } else {
                int v = center.vertices()[0];
                int u = center.vertices()[1];
                int w = graph.splitEdge(v, u);
                graph.addEdge(root2, w);
            }
        }
        return new RootedTree(graph, root);
    }

}
