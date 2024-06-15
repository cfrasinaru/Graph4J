package org.graph4j.iso;

import org.graph4j.Graph;

import java.util.List;
import java.util.Optional;
import org.graph4j.Graphs;

/**
 * Not rooted tree isomorphism algorithm.
 *
 *
 * This algorithm is based on "Alfred V. Aho and John E. Hopcroft. 1974. The
 * Design and Analysis of Computer Algorithms (1st ed.). Addison-Wesley Longman
 * Publishing Co., Inc., Boston, MA, USA.", page 83-84.
 *
 *
 * Time complexity: linear in the number of vertices of the input trees. Space
 * complexity: linear in the number of vertices of the input trees.
 *
 *
 * Note: This inspector only returns a single mapping (chosen arbitrarily)
 * rather than all possible mappings.
 *
 *
 * Note: Works only for undirected trees.
 *
 * @author Ignat Gabriel-Andrei
 */
public class NotRootedTreeIsomorphism implements Isomorphism {

    private final Graph<?, ?> tree1;

    private final Graph<?, ?> tree2;

    private boolean done = false;

    // the algorithm for mapping retrieval and isomorphism checking
    private RootedTreeIsomorphism iso;

    /**
     * Constructor for the not rooted tree isomorphism algorithm. Validates the
     * trees.
     *
     * @param tree1 first tree
     * @param tree2 second tree
     * @throws NullPointerException if {@code tree1} or {@code tree2} is
     * {@code null}
     * @throws IllegalArgumentException if {@code tree1} or {@code tree2} is not
     * undirected
     * @throws IllegalArgumentException if {@code tree1} or {@code tree2} is
     * empty
     * @throws IllegalArgumentException if {@code tree1} or {@code tree2} is not
     * a tree
     */
    public NotRootedTreeIsomorphism(Graph<?, ?> tree1, Graph<?, ?> tree2) {
        validateTree(tree1);
        this.tree1 = tree1;

        validateTree(tree2);
        this.tree2 = tree2;
    }

    private void validateTree(Graph<?, ?> tree) {
        if (tree.isDirected()) {
            throw new IllegalArgumentException("Tree must be undirected");
        }

        if (!Graphs.isTree(tree)) {
            throw new IllegalArgumentException("The input graph must be a tree");
        }

        if (tree.numVertices() == 0) {
            throw new IllegalArgumentException("Tree cannot be empty");
        }
    }

    /**
     * Main method for checking if two not rooted trees are isomorphic.
     *
     *
     * Does the following:
     * <ul>
     * <li>Gets the centers for each tree.
     * <li>Checks if the number of centers is the same for both trees.
     * <li>If yes, it will try all combinations of choosing a root for the first
     * tree with a root from second tree. A root is a vertex from centers list.
     * <li>If any of this combination is successful, it means that the trees are
     * isomorphic.
     * </ul>
     *
     * @return {@code true} if the trees are isomorphic, {@code false} otherwise
     */
    @Override
    public boolean areIsomorphic() {
        if (done) {
            if (iso != null) {
                return iso.areIsomorphic();
            } else {
                return false;
            }
        }

        done = true;

        List<Integer> centers1 = GraphUtil.getCenters(tree1);
        List<Integer> centers2 = GraphUtil.getCenters(tree2);

        if (centers1.size() == 1 && centers2.size() == 1) {
            iso = new RootedTreeIsomorphism(tree1, tree2, centers1.get(0), centers2.get(0));
        } else if (centers1.size() == 2 && centers2.size() == 2) {
            iso = new RootedTreeIsomorphism(tree1, tree2, centers1.get(0), centers2.get(0));
            if (!iso.areIsomorphic()) {
                iso = new RootedTreeIsomorphism(tree1, tree2, centers1.get(1), centers2.get(0));
            }
        } else {
            // the structure of the trees is different
            return false;
        }

        return iso.areIsomorphic();
    }

    @Override
    public Optional<GraphMapping> getMappings() {
        return Optional.ofNullable(getMapping());
    }

    public IsomorphicGraphMapping getMapping() {
        if (areIsomorphic()) {
            return iso.getMapping();
        } else {
            return null;
        }
    }
}
