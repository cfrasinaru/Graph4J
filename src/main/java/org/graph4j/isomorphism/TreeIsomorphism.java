package org.graph4j.isomorphism;

import org.graph4j.Graph;

import org.graph4j.metrics.TreeMetrics;
import org.graph4j.util.Validator;
import org.graph4j.util.VertexSet;

/**
 * Algorithm for testing isomorphism of undirected trees.
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
public class TreeIsomorphism implements IsomorphismAlgorithm {

    private final Graph tree1;
    private final Graph tree2;
    private RootedTreeIsomorphism rootedAlg;
    private Boolean isomorphic;
    private Isomorphism isomorphism;

    /**
     * Constructor for the tree isomorphism algorithm. It does not verify if the
     * input graphs are actually trees.
     *
     * @param tree1 the first tree.
     * @param tree2 the second tree.
     * @throws NullPointerException if either {@code tree1} or {@code tree2} is
     * {@code null}.
     * @throws IllegalArgumentException if either {@code tree1} or {@code tree2}
     * is not undirected.
     */
    public TreeIsomorphism(Graph tree1, Graph tree2) {
        Validator.requireUndirected(tree1);
        Validator.requireUndirected(tree2);
        this.tree1 = tree1;
        this.tree2 = tree2;
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
        isomorphism = rootedAlg.findIsomorphism();
        return isomorphism;
    }

    protected boolean computeIsomorphic() {
        if (tree1.isEmpty() && tree2.isEmpty()) {
            return true;
        }
        if (!checkTrivialConditions(tree1, tree2)) {
            return false;
        }
        VertexSet center1 = new TreeMetrics(tree1).center();
        VertexSet center2 = new TreeMetrics(tree2).center();

        if (center1.size() == 1 && center2.size() == 1) {
            //both trees have a single center vertex
            int root1 = center1.vertices()[0];
            int root2 = center2.vertices()[0];
            rootedAlg = new RootedTreeIsomorphism(tree1, tree2, root1, root2);
        } else if (center1.size() == 2 && center2.size() == 2) {
            //both trees have two vertices in the center
            int root1 = center1.vertices()[0];
            int root2 = center2.vertices()[0];
            rootedAlg = new RootedTreeIsomorphism(tree1, tree2, root1, root2);
            if (!rootedAlg.areIsomorphic()) {
                root2 = center2.vertices()[1];
                rootedAlg = new RootedTreeIsomorphism(tree1, tree2, root1, root2);
            }
        } else {
            // the structure of the trees is different
            return false;
        }
        return rootedAlg.areIsomorphic();
    }

    /**
     * The first tree.
     *
     * @return the first tree.
     */
    public Graph getTree1() {
        return tree1;
    }

    /**
     * The second tree.
     *
     * @return the second tree.
     */
    public Graph getTree2() {
        return tree2;
    }

}
