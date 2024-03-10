package org.graph4j.iso;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import org.graph4j.Graph;
import org.graph4j.Graphs;
import org.graph4j.traverse.BFSIterator;
import org.graph4j.traverse.SearchNode;
import org.graph4j.util.Pair;
import org.graph4j.util.RadixSort;

/**
 * Rooted tree isomorphism algorithm.
 * <p>
 *     This algorithm is based on "Alfred V. Aho and John E. Hopcroft. 1974. The
 *     Design and Analysis of Computer Algorithms (1st ed.). Addison-Wesley Longman Publishing Co.,
 *     Inc., Boston, MA, USA.", page 83-84.
 * </p>
 *
 * <p>
 *     Time complexity: linear in the number of vertices of the input trees.
 *     Space complexity: linear in the number of vertices of the input trees.
 * </p>
 *
 * <p>
 *     Note: This inspector only returns a single mapping (chosen arbitrarily) rather than all possible
 *     mappings.
 * </p>
 *
 * <p>
 *     Note: If the input graph is directed, it will treat only the subtree that is reachable from the root.
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 */
public class RootedTreeIsomorphism implements Isomorphism {
    private final Graph<?,?> tree1;
    private final Graph<?,?> tree2;
    private final Integer root1;
    private final Integer root2;
    private int[] forwardMapping = null;
    private int[] backwardMapping = null;
    private int newLabel;
    private boolean done;
    private boolean isIsomorphic;

    /**
     * Constructor for the rooted tree isomorphism algorithm.
     * Checks if the input trees are valid.
     *
     * @param tree1 first tree
     * @param tree2 second tree
     * @param root1 root of the first tree
     * @param root2 root of the second tree
     *
     * @throws NullPointerException if {@code tree1} or {@code tree2} is {@code null}
     * @throws NullPointerException if {@code root1} or {@code root2} is {@code null}
     * @throws IllegalArgumentException if {@code tree1} or {@code tree2} is not a tree
     * @throws IllegalArgumentException if {@code tree1} or {@code tree2} is empty
     * @throws IllegalArgumentException if {@code root1} or {@code root2} is not in the respective tree
     */
    public RootedTreeIsomorphism(Graph<?,?> tree1, Graph<?,?> tree2,
                                 Integer root1, Integer root2) {
        if(tree1 == null || tree2 == null)
            throw new NullPointerException("Input trees cannot be null");

        validateInputTree(tree1, root1);
        this.tree1 = tree1;
        this.root1 = root1;

        validateInputTree(tree2, root2);
        this.tree2 = tree2;
        this.root2 = root2;
    }

    private void validateInputTree(Graph<?,?> tree, Integer root) {
        if(!Graphs.isTree(tree))
            throw new IllegalArgumentException("The input graph must be a tree");

        if(root == null)
            throw new NullPointerException("Input root cannot be null");

        if(tree.numVertices() == 0)
            throw new IllegalArgumentException("Input tree cannot be empty");

        if(!tree.containsVertex(root))
            throw new IllegalArgumentException("Input tree must contain the root");
    }

    /**
     * Using Breadth-First Search, it computes the labels of the nodes on all levels.
     *
     * @param tree input tree to be traversed
     * @param root node from which the traversal starts
     * @param levels list of lists of nodes on each level, that will be filled
     */
    private void groupByLevels(Graph<?,?> tree, int root,
                               List<List<Integer>> levels) {
        BFSIterator bfsIterator = new BFSIterator(tree, root);
        int level;

        while(bfsIterator.hasNext()){
            SearchNode searchNode = bfsIterator.next();
            int node = searchNode.vertex();

            level = searchNode.level();

            // if the level is not yet in the list, add it
            if(levels.size() <= level)
                levels.add(new ArrayList<>());

            // add the node to the list of nodes on the current level
            levels.get(level).add(node);
        }
    }

    /**
     * Main method for checking if two trees are isomorphic.
     * Does the following:
     * <ul>
     *     <li>checks if the number of vertices and edges is the same</li>
     *     <li>groups the nodes by levels</li>
     *     <li>checks if the number of nodes on each level is the same</li>
     *     <li>computes the labels of the nodes on each level</li>
     *     <li>checks if labels of one level are the same on each tree</li>
     *     <li>computes the mapping(forward, backward)</li>
     * </ul>
     *
     * @return boolean value indicating if the two trees are isomorphic
     */
    @Override
    public boolean areIsomorphic() {
        /* If the isomorphism has already been computed,
         there is no point in recomputing it */
        if(done) {
            return isIsomorphic;
        }

        // otherwise, let's compute the isomorphism
        if(tree1.numVertices() != tree2.numVertices()
                || tree1.numEdges() != tree2.numEdges()) {
            return finish(false);
        }

        List<List<Integer>> nodesOnLevels1 = new ArrayList<>();
        List<List<Integer>> nodesOnLevels2 = new ArrayList<>();

        groupByLevels(tree1, root1, nodesOnLevels1);
        groupByLevels(tree2, root2, nodesOnLevels2);

        if(nodesOnLevels1.size() != nodesOnLevels2.size()) {
            return finish(false);
        }

        int[] labels1 = new int[tree1.numVertices()];
        int[] labels2 = new int[tree2.numVertices()];
        Arrays.fill(labels1, -1);
        Arrays.fill(labels2, -1);

        final int HEIGHT = nodesOnLevels1.size() - 1;

        // map from a list of labels to a single label
        // two nodes with the same label, suggest that their subtrees are isomorphic
        Map<List<Integer>, Integer> labelListToInt = new HashMap<>();

        this.newLabel = 0;

        for(int lvl = HEIGHT ; lvl >= 0 ; lvl--){
            List<Integer> nodesOnLevelK1 = nodesOnLevels1.get(lvl);
            List<Integer> nodesOnLevelK2 = nodesOnLevels2.get(lvl);

            if(nodesOnLevelK1.size() != nodesOnLevelK2.size()) {
                return finish(false);
            }

            List<Integer> labelList1 = findLabels(tree1, nodesOnLevelK1, labels1, labelListToInt);
            List<Integer> labelList2 = findLabels(tree2, nodesOnLevelK2, labels2, labelListToInt);

            if(labelList1.size() != labelList2.size()) {
                return finish(false);
            }

            if(!labelList1.equals(labelList2)) {
                return finish(false);
            }
        }

        // if we got here, the trees are isomorphic
        matchNodes(labels1, labels2);

        return finish(true);
    }

    /**
     * Method for matching the nodes of the two trees.
     * <p>
     *      It does the following:
     *      <ul>
     *          <li>For a node that is already matched, it will match its children to
     *          the children of the node that it is matched to, with the same labels.</li>
     *          <li>It will start with the roots, then will add to a queue the nodes that were matched.</li>
     *          <li>While there are still nodes in the queue, repeat step 1.</li>
     * </p>
     *
     * @param labelList1 labels of the nodes of the first tree
     * @param labelList2 labels of the nodes of the second tree
     */
    private void matchNodes(int[] labelList1, int[] labelList2) {
        // initialize the mappings
        forwardMapping = new int[tree1.numVertices()];
        backwardMapping = new int[tree2.numVertices()];

        Arrays.fill(forwardMapping, -1);
        Arrays.fill(backwardMapping, -1);

        // declare the queue -> BFS like queue
        Queue<Pair<Integer, Integer>> bfsQueue = new ArrayDeque<>();
        // add the roots to the queue
        bfsQueue.add(new Pair<>(root1, root2));

        while(!bfsQueue.isEmpty()){
            // get the next pair of nodes for which we will match the children
            Pair<Integer, Integer> p = bfsQueue.poll();
            int node1 = p.first();
            int node2 = p.second();

            forwardMapping[tree1.indexOf(node1)] = node2;
            backwardMapping[tree2.indexOf(node2)] = node1;

            // every label will have a list of nodes with that label
            Map<Integer, List<Integer>> labelToNodes = new HashMap<>();


            for(int child : tree1.neighbors(node1)){
                // if the neighbour is not the parent
                if(forwardMapping[tree1.indexOf(child)] == -1){
                    int label = labelList1[tree1.indexOf(child)];

                    // add node to the list of nodes with the same label
                    labelToNodes.putIfAbsent(label, new ArrayList<>());
                    labelToNodes.get(label).add(child);
                }
            }

            // now we match the nodes on tree2 with node on tree1 that have the same label
            for(int child : tree2.neighbors(node2)){
                // if the neighbour is not the parent
                if(backwardMapping[tree2.indexOf(child)] == -1){
                    int label2 = labelList2[tree2.indexOf(child)];

                    // it is guaranteed that at least one node has the same label, because we have checked that the labels are equal previously
                    List<Integer> nodesWithSameLabel = labelToNodes.get(label2);

                    if(nodesWithSameLabel == null || nodesWithSameLabel.isEmpty())
                        throw new RuntimeException("Something went wrong");

                    // take the first node with the same label
                    int node1WithSameLabel = nodesWithSameLabel.remove(0);
                    bfsQueue.add(new Pair<>(node1WithSameLabel, child));
                }
            }
        }
    }

    /**
     * Finds the labels of the nodes on a given level.
     * <p>
     *  The labels are computed by sorting the labels of the children of a node.
     *  The sorted list of labels is then mapped to a single label.
     *  If the list of labels is not in the map, a new label is created.
     * </p>
     *
     * @param tree the tree that needs to be labeled
     * @param nodesOnLevelK the nodes on the current level
     * @param labels the labels of the nodes, that will be updated
     * @param labelListToInt the map from a list of labels to a single label
     * @return the list of labels of the nodes on the current level, sorted, for further validation
     */
    private List<Integer> findLabels(Graph<?,?> tree,
                                                    List<Integer> nodesOnLevelK,
                                                    int[] labels,
                                                    Map<List<Integer>, Integer> labelListToInt){
        List<Integer> labelListOnLevelK = new ArrayList<>();

        for (int node : nodesOnLevelK) {

            List<Integer> list = new ArrayList<>();
            for (int child : tree.neighbors(node)){
                int label = labels[tree.indexOf(child)];

                // if the neighbour is not the parent
                if(label != -1)
                    list.add(label);
            }

            RadixSort.sort(list);

            Integer intLabel = labelListToInt.get(list);

            // if the list of labels is not in the map, create a new label
            if (intLabel == null) {
                labelListToInt.put(list, newLabel);
                intLabel = newLabel;

                newLabel++;
            }

            // update the label of the node
            labels[tree.indexOf(node)] = intLabel;

            // add the label to the list of labels on the current level
            labelListOnLevelK.add(intLabel);
        }

        RadixSort.sort(labelListOnLevelK);

        return labelListOnLevelK;
    }


    /**
     * Method for finishing the algorithm.
     * <p>
     *     It sets the isIsomorphic flag to the given value, and sets the done flag to true.
     *     If the trees are not isomorphic, the mappings are set to null.(erase the mappings)
     *     It returns the isIsomorphic flag.
     * </p>
     * @param isIsomorphic the value to which the isIsomorphic flag will be set
     * @return the isIsomorphic flag
     */
    private boolean finish(boolean isIsomorphic){
        this.isIsomorphic = isIsomorphic;
        this.done = true;
        if(!isIsomorphic){
            this.forwardMapping = null;
            this.backwardMapping = null;
        }
        return isIsomorphic;
    }

    @Override
    public Optional<GraphMapping> getMappings() {
        GraphMapping graphMapping = getMapping();

        return Optional.ofNullable(graphMapping);
    }

    /**
     * Method for getting the mapping of the two trees.
     * <p>
     *     If the trees are not isomorphic, it returns null.
     *     Otherwise, it returns a new IsomorphicGraphMapping object.
     * </p>
     * @return the mapping of the two trees
     */
    public IsomorphicGraphMapping getMapping() {
        if(areIsomorphic())
            return new IsomorphicGraphMapping(forwardMapping, backwardMapping, tree1, tree2);
        else
            return null;
    }
}

