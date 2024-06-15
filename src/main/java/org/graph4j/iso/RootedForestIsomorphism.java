package org.graph4j.iso;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.Graphs;
import org.graph4j.util.Pair;

import java.util.*;

/**
 * Isomorphism checker for forests with specified roots.
 *
 *
 * This algorithm is based on "Alfred V. Aho and John E. Hopcroft. 1974. The
 * Design and Analysis of Computer Algorithms (1st ed.). Addison-Wesley Longman
 * Publishing Co., Inc., Boston, MA, USA.", page 83-84.
 *
 * Time complexity: linear in the number of vertices of the input trees. Space
 * complexity: linear in the number of vertices of the input trees.
 *
 *
 * Note: This inspector only returns a single mapping (chosen arbitrarily)
 * rather than all possible mappings.
 *
 * @author Ignat Gabriel-Andrei
 */
public class RootedForestIsomorphism implements Isomorphism {

    private final Graph<?, ?> forest1;
    private final Graph<?, ?> forest2;
    private final Set<Integer> roots1;
    private final Set<Integer> roots2;
    private boolean done = false;
    IsomorphicGraphMapping mapping;

    /**
     * Constructor for the rooted forest isomorphism algorithm. Validates the
     * forests.
     *
     * @param forest1 first forest
     * @param forest2 second forest
     * @param roots1 roots of the first forest
     * @param roots2 roots of the second forest
     * @throws NullPointerException if {@code forest1}, {@code forest2},
     * {@code roots1} or {@code roots2} is {@code null}
     * @throws IllegalArgumentException if {@code forest1} or {@code forest2} is
     * not undirected
     * @throws IllegalArgumentException if {@code forest1} or {@code forest2} is
     * not simple(no self loops or multiple edges)
     * @throws IllegalArgumentException if {@code forest1} or {@code forest2} is
     * empty
     * @throws IllegalArgumentException if {@code roots1} or {@code roots2} is
     * empty
     * @throws IllegalArgumentException if {@code roots1} or {@code roots2} are
     * not in the forest
     */
    public RootedForestIsomorphism(Graph<?, ?> forest1, Graph<?, ?> forest2, Set<Integer> roots1, Set<Integer> roots2) {
        if (forest1 == null || forest2 == null) {
            throw new NullPointerException("Forest must not be null");
        }

        if (roots1 == null || roots2 == null) {
            throw new NullPointerException("Roots set can not be null");
        }

        validateForest(forest1, roots1);
        this.forest1 = forest1;
        this.roots1 = roots1;

        validateForest(forest2, roots2);
        this.forest2 = forest2;
        this.roots2 = roots2;
    }

    private void validateForest(Graph<?, ?> forest, Set<Integer> roots) {
        if (forest.numVertices() == 0) {
            throw new IllegalArgumentException("Forest cannot be empty");
        }

        if (roots.isEmpty()) {
            throw new IllegalArgumentException("Roots cannot be empty");
        }

        if (forest.isAllowingSelfLoops() || forest.isAllowingMultipleEdges()) {
            throw new IllegalArgumentException("Forest must not allow self loops or multiple edges");
        }

        for (int root : roots) {
            if (!forest.containsVertex(root)) {
                throw new IllegalArgumentException("Roots must be in the forest");
            }
        }
    }

    @Override
    public Optional<GraphMapping> getMappings() {
        GraphMapping graphMapping = getMapping();

        return Optional.ofNullable(graphMapping);
    }

    public IsomorphicGraphMapping getMapping() {
        if (!done) {
            areIsomorphic();
        }

        return mapping;
    }

    /**
     * Main method for checking if the two forests are isomorphic.
     *
     *
     * Does the following:
     * <ul>
     * <li>If the forests have a single root, then it checks if the two trees
     * are isomorphic.
     * <li>Otherwise, it builds a rooted tree for each forest and checks if the
     * two trees are isomorphic.
     * <li>If yes, it will construct a mapping for the forests from the created
     * trees.
     * </ul>
     *
     * @return true if the forests are isomorphic, false otherwise
     */
    @Override
    public boolean areIsomorphic() {
        if (done) {
            return mapping != null;
        }

        done = true;

        // If the forests have a single root, then it checks if the two trees are isomorphic.
        if (roots1.size() == 1 && roots2.size() == 1) {
            int root1 = roots1.iterator().next();
            int root2 = roots2.iterator().next();

            mapping = new RootedTreeIsomorphism(forest1, forest2, root1, root2).getMapping();
        } else {
            Pair<Integer, Graph<?, ?>> pair1 = buildRootedTree(forest1, roots1);
            Pair<Integer, Graph<?, ?>> pair2 = buildRootedTree(forest2, roots2);

            int root1 = pair1.first();
            int root2 = pair2.first();

            Graph<?, ?> tree1 = pair1.second();
            Graph<?, ?> tree2 = pair2.second();

            mapping = new RootedTreeIsomorphism(tree1, tree2, root1, root2).getMapping();

            if (mapping != null) {
                int[] forwardCopy = mapping.getForwardMapping();
                int[] backwardCopy = mapping.getBackwardMapping();

                int[] forwardMap = new int[tree1.numVertices()];
                int[] backwardMap = new int[tree2.numVertices()];

                // Only the vertices from the forest are mapped
                for (int vertex : forest1.vertices()) {
                    forwardMap[forest1.indexOf(vertex)] = forwardCopy[tree1.indexOf(vertex)];
                }

                // Only the vertices from the forest are mapped
                for (int vertex : forest2.vertices()) {
                    backwardMap[forest2.indexOf(vertex)] = backwardCopy[tree2.indexOf(vertex)];
                }

                mapping = new IsomorphicGraphMapping(forwardMap, backwardMap, forest1, forest2);
            }
        }
        return mapping != null;
    }

    /**
     * Builds a rooted tree from a forest.
     *
     *
     * The method adds a new root vertex and connects it to the roots of the
     * forest. The new root is the maximum vertex number + 1.
     *
     * @param forest forest
     * @param roots roots of the forest
     * @return a pair of the new root and the new forest
     */
    private Pair<Integer, Graph<?, ?>> buildRootedTree(Graph<?, ?> forest, Set<Integer> roots) {
        Graph<?, ?> newForest = GraphBuilder.empty().estimatedNumVertices(roots.size() + 1).buildGraph();

        for (int root : roots) {
            newForest.addVertex(root);
        }

        int newRoot = forest.maxVertexNumber() + 1;
        newForest.addVertex(newRoot);

        for (int root : roots) {
            newForest.addEdge(newRoot, root);
        }

        return new Pair<>(newRoot, Graphs.union(newForest, forest));
    }
}
