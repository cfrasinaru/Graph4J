package org.graph4j.isomorphism;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import org.graph4j.Graph;
import org.graph4j.traversal.BFSIterator;
import org.graph4j.traversal.SearchNode;
import org.graph4j.util.Validator;
import org.graph4j.util.CountingSort;
import org.graph4j.util.Pair;
import org.graph4j.util.RootedTree;
import org.graph4j.util.SortingAlgorithm;
import org.graph4j.util.VertexList;
import org.graph4j.util.VertexQueue;

/**
 * Algorithm for testing isomorphism of rooted trees.
 * <p>
 * This algorithm is based on "Alfred V. Aho and John E. Hopcroft. 1974. The
 * Design and Analysis of Computer Algorithms (1st ed.). Addison-Wesley Longman
 * Publishing Co., Inc., Boston, MA, USA.", page 83-84.
 * </p>
 *
 * <p>
 * Time complexity: linear in the number of vertices of the input trees. Space
 * complexity: linear in the number of vertices of the input trees.
 * </p>
 *
 * <p>
 * The trees may be either directed or undirected, but if they are directed, all
 * edges should flow from the root (they should be arborescences).
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 * @author Cristian Frasinaru
 *
 */
public class RootedTreeIsomorphism implements IsomorphismAlgorithm {

    private final Graph tree1;
    private final Graph tree2;
    private final int root1;
    private final int root2;
    private int[] labels1;
    private int[] labels2;
    private Map<List<Integer>, Integer> labelListToInt;
    private int maxLabel;
    private Boolean isomorphic;
    private Isomorphism isomorphism;
    private final SortingAlgorithm sortingAlgorithm = new CountingSort();

    /**
     * Constructor for the rooted tree isomorphism algorithm. It does not verify
     * if the input graphs are actually trees.
     *
     * @param tree1 first tree.
     * @param tree2 second tree.
     * @param root1 root of the first tree.
     * @param root2 root of the second tree.
     *
     * @throws NullPointerException if either {@code tree1} or {@code tree2} is
     * {@code null}.
     * @throws IllegalArgumentException if either {@code root1} or {@code root2}
     * is not in their respective trees.
     */
    public RootedTreeIsomorphism(Graph tree1, Graph tree2, int root1, int root2) {
        Objects.requireNonNull(tree1);
        Objects.requireNonNull(tree2);
        Validator.containsVertex(tree1, root1);
        Validator.containsVertex(tree2, root2);
        this.tree1 = tree1;
        this.root1 = root1;
        this.tree2 = tree2;
        this.root2 = root2;
    }

    /**
     * Constructor for the rooted tree isomorphism algorithm. It does not verify
     * if the input graphs are actually trees.
     *
     * @param rootedTree1 the first rooted tree.
     * @param rootedTree2 the second rooted tree.
     */
    public RootedTreeIsomorphism(RootedTree rootedTree1, RootedTree rootedTree2) {
        this(rootedTree1.tree(), rootedTree2.tree(), rootedTree1.root(), rootedTree2.root());
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
        isomorphism = createIsomorphism();
        return isomorphism;
    }

    private boolean computeIsomorphic() {
        if (!checkTrivialConditions(tree1, tree2)) {
            return false;
        }

        //for each tree, create the list of levels
        List<VertexList> nodesOnLevels1 = createLevels(tree1, root1);
        List<VertexList> nodesOnLevels2 = createLevels(tree2, root2);
        if (nodesOnLevels1.size() != nodesOnLevels2.size()) {
            return false;
        }

        labels1 = new int[tree1.numVertices()];
        labels2 = new int[tree2.numVertices()];
        Arrays.fill(labels1, -1);
        Arrays.fill(labels2, -1);

        // map from a list of labels to a single label
        // two nodes with the same label, suggest that their subtrees are isomorphic
        this.labelListToInt = new HashMap<>();
        labelListToInt.put(Collections.EMPTY_LIST, 0);
        this.maxLabel = 1;

        final int height = nodesOnLevels1.size() - 1;
        for (int lvl = height; lvl >= 0; lvl--) {
            var level1 = nodesOnLevels1.get(lvl);
            var level2 = nodesOnLevels2.get(lvl);
            if (level1.size() != level2.size()) {
                return false;
            }
            List<Integer> labelList1 = computeLabels(tree1, root1, level1, labels1);
            List<Integer> labelList2 = computeLabels(tree2, root2, level2, labels2);
            if (labelList1.size() != labelList2.size()) {
                return false;
            }
            if (!labelList1.equals(labelList2)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Using Breadth-First Search, it createa the list of vertices on each
     * level.
     */
    private List<VertexList> createLevels(Graph tree, int root) {
        List<VertexList> levelList = new ArrayList<>();
        BFSIterator bfsIterator = new BFSIterator(tree, root);
        while (bfsIterator.hasNext()) {
            SearchNode searchNode = bfsIterator.next();
            int level = searchNode.level();
            // if the level is not yet in the list, add it
            if (levelList.size() <= level) {
                levelList.add(new VertexList(tree));
            }
            // add the node to the list of nodes on the current level
            levelList.get(level).add(searchNode.vertex());
        }
        return levelList;
    }

    /*
     * Computes the labels of the nodes on a given level.
     * The labels are computed by sorting the labels of the children of a node.
     * The sorted list of labels is then mapped to a single label. If the list
     * of labels is not in the map, a new label is created.
     *
     */
    private List<Integer> computeLabels(Graph tree, int root,
            VertexList level,
            int[] labels) {

        List<Integer> levelLabels = new ArrayList<>();
        for (int v : level.vertices()) {
            Integer intLabel;
            int deg = tree.degree(v);
            if (deg == 1 && v != root) {
                intLabel = 0; //leaf
            } else {
                List<Integer> list = new ArrayList<>(deg);
                for (var it = tree.neighborIterator(v); it.hasNext();) {
                    int child = it.next();
                    int label = labels[tree.indexOf(child)];
                    // if the neighbour is not the parent
                    if (label != -1) {
                        list.add(label);
                    }
                }
                sortingAlgorithm.sort(list);
                intLabel = labelListToInt.get(list);
                if (intLabel == null) {
                    // if the list of labels is not in the map, create a new label
                    labelListToInt.put(list, maxLabel);
                    intLabel = maxLabel;
                    maxLabel++;
                }
            }

            // update the label of the node
            labels[tree.indexOf(v)] = intLabel;
            // add the label to the list of labels on the current level
            levelLabels.add(intLabel);
        }
        sortingAlgorithm.sort(levelLabels);
        return levelLabels;
    }

    /*
     * Method for matching the nodes of the two trees. 
     * It does the following:
     * For a node that is already matched, it will match its children to the
     * children of the node that it is matched to, with the same labels.     
     * It will start with the roots, then will add to a queue the nodes that were
     * matched.
     */
    private Isomorphism createIsomorphism() {
        // initialize the mappings
        int[] mapping = new int[tree1.numVertices()];
        int[] inverse = new int[tree2.numVertices()];
        Arrays.fill(mapping, -1);
        Arrays.fill(inverse, -1);

        // declare the queue -> BFS like queue
        Queue<Pair<Integer, Integer>> bfsQueue = new ArrayDeque<>();
        // add the roots to the queue
        bfsQueue.add(new Pair<>(root1, root2));

        while (!bfsQueue.isEmpty()) {
            // get the next pair of nodes for which we will match the children
            Pair<Integer, Integer> p = bfsQueue.poll();
            int node1 = p.first();
            int node2 = p.second();

            mapping[tree1.indexOf(node1)] = node2;
            inverse[tree2.indexOf(node2)] = node1;

            // every label will have a queue of nodes with that label
            Map<Integer, VertexQueue> labelToNodes = new HashMap<>();

            for (var it = tree1.neighborIterator(node1); it.hasNext();) {
                int child = it.next();
                // if the neighbour is not the parent
                if (mapping[tree1.indexOf(child)] == -1) {
                    int label = labels1[tree1.indexOf(child)];
                    // add node to the queue of nodes with the same label
                    labelToNodes.putIfAbsent(label, new VertexQueue(tree1));
                    labelToNodes.get(label).add(child);
                }
            }

            // now we match the nodes on tree2 with node on tree1 that have the same label
            for (var it = tree2.neighborIterator(node2); it.hasNext();) {
                int child = it.next();
                // if the neighbour is not the parent
                if (inverse[tree2.indexOf(child)] == -1) {
                    int label2 = labels2[tree2.indexOf(child)];

                    // it is guaranteed that at least one node has the same label, 
                    // because we have checked that the labels are equal previously
                    VertexQueue nodesWithSameLabel = labelToNodes.get(label2);
                    if (nodesWithSameLabel == null || nodesWithSameLabel.isEmpty()) {
                        throw new IllegalStateException("Something went wrong");
                    }
                    // pick a node with the same label
                    int node1WithSameLabel = nodesWithSameLabel.poll();
                    bfsQueue.add(new Pair<>(node1WithSameLabel, child));
                }
            }
        }
        return new Isomorphism(tree1, tree2, mapping, inverse);
    }

}
