package org.graph4j.isomorphism;

import java.util.Objects;
import org.graph4j.Graph;
import org.graph4j.GraphUtils;
import org.graph4j.util.Validator;
import org.graph4j.util.RootedTree;
import org.graph4j.util.VertexSet;

/**
 * Algorithm for testing isomorphism of forests with specified roots.
 *
 * <p>
 * This algorithm is based on "Alfred V. Aho and John E. Hopcroft. 1974. The
 * Design and Analysis of Computer Algorithms (1st ed.). Addison-Wesley Longman
 * Publishing Co., Inc., Boston, MA, USA.", page 83-84.
 * </p>
 * <p>
 * Time complexity: linear in the number of vertices of the input trees. Space
 * complexity: linear in the number of vertices of the input trees.
 * </p>
 *
 * @see RootedTreeIsomorphism
 *
 * @author Ignat Gabriel-Andrei
 * @author Cristian Frasinaru
 */
public class RootedForestIsomorphism implements IsomorphismAlgorithm {

    private final Graph forest1;
    private final Graph forest2;
    private final VertexSet roots1;
    private final VertexSet roots2;
    private RootedTreeIsomorphism rootedTreeAlg;
    private Graph tree1;
    private Graph tree2;
    private int root1;
    private int root2;
    private Boolean isomorphic;
    private Isomorphism isomorphism;

    /**
     * Constructor for the rooted forest isomorphism algorithm. It does not
     * verify if the input graphs are actually forests.
     *
     * @param forest1 the first forest.
     * @param forest2 the second forest.
     * @param roots1 the roots of the first forest.
     * @param roots2 the roots of the second forest.
     * @throws NullPointerException if either {@code forest1} or {@code forest2}
     * is {@code null}.
     * @throws IllegalArgumentException if either {@code forest1} or
     * {@code forest2} is not a simple graph.
     * @throws IllegalArgumentException if either {@code roots1} or
     * {@code roots2} contain vertices that are not in the forest.
     */
    public RootedForestIsomorphism(Graph forest1, Graph forest2,
            VertexSet roots1, VertexSet roots2) {
        validateForest(forest1, roots1);
        validateForest(forest2, roots2);
        this.forest1 = forest1;
        this.roots1 = roots1;
        this.forest2 = forest2;
        this.roots2 = roots2;
    }

    private void validateForest(Graph forest, VertexSet roots) {
        Validator.requireSimple(forest);
        Objects.requireNonNull(roots);
        if (roots.isEmpty()) {
            throw new IllegalArgumentException("Roots cannot be empty.");
        }
        for (int root : roots.vertices()) {
            Validator.containsVertex(forest, root);
        }
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
        var treeIso = rootedTreeAlg.findIsomorphism();
        if (tree1 == forest1 && tree2 == forest2) {
            isomorphism = treeIso;
        } else {
            // Only the vertices in the forest are mapped
            int[] mapping = new int[tree1.numVertices()];
            int[] inverse = new int[tree2.numVertices()];
            for (int v : forest1.vertices()) {
                mapping[forest1.indexOf(v)] = treeIso.mapping(v);
            }
            for (int v : forest2.vertices()) {
                inverse[forest2.indexOf(v)] = treeIso.inverse(v);
            }
            isomorphism = new Isomorphism(forest1, forest2, mapping, inverse);
        }
        return isomorphism;
    }

    private boolean computeIsomorphic() {
        if (!checkTrivialConditions(forest1, forest2)) {
            return false;
        }
        createRootedTreeAlg();
        return rootedTreeAlg.areIsomorphic();
    }

    /*
     * Does the following:     
     * If the forests have a single root, then it checks if the two trees
     * are isomorphic.
     * Otherwise, it builds a rooted tree for each forest and checks if the
     * two trees are isomorphic.
     * If yes, it will construct a mapping for the forests from the created
     * trees.
     */
    public void createRootedTreeAlg() {
        if (roots1.size() == 1 && roots2.size() == 1) {
            // the forests are actually trees
            tree1 = forest1;
            tree2 = forest2;
            root1 = roots1.iterator().next();
            root2 = roots2.iterator().next();
        } else {
            // build a rooted tree for each forest 
            var rootedTree1 = buildRootedTree(forest1, roots1);
            var rootedTree2 = buildRootedTree(forest2, roots2);
            tree1 = rootedTree1.tree();
            root1 = rootedTree1.root();
            tree2 = rootedTree2.tree();
            root2 = rootedTree2.root();
        }
        rootedTreeAlg = new RootedTreeIsomorphism(tree1, tree2, root1, root2);
    }

    /*
     * Builds a rooted tree from a forest.
     * The method adds a new root vertex and connects it to the roots of the
     * forest.
     */
    private RootedTree buildRootedTree(Graph forest, VertexSet roots) {
        Graph tree = forest.copy();
        int newRoot = tree.addVertex();
        for (int root : roots.vertices()) {
            tree.addEdge(newRoot, root);
        }
        return new RootedTree(GraphUtils.union(tree, forest), newRoot);
    }
}
