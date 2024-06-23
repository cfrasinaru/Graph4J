/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graph4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import static org.graph4j.GraphImpl.WEIGHT;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 * @param <V>
 * @param <E>
 */
class DigraphImpl<V, E> extends GraphImpl<V, E> implements Digraph<V, E> {

    protected int[][] predList; //predecessors
    protected int[][] predPos; //positions of a predecessor in the adjList
    protected int[] indegree;
    //outdegree is GraphImpl.degree

    protected DigraphImpl() {
    }

    protected DigraphImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
        super(vertices, maxVertices, avgDegree, directed, allowingMultipleEdges, allowingSelfLoops,
                vertexDataSize, edgeDataSize);
        indegree = new int[maxVertices];
        predList = new int[maxVertices][];
        predPos = new int[maxVertices][];
    }

    @Override
    protected GraphImpl newInstance() {
        return new DigraphImpl();
    }

    @Override
    protected GraphImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
        return new DigraphImpl(vertices, maxVertices, avgDegree, directed, allowingMultipleEdges, allowingSelfLoops,
                vertexDataSize, edgeDataSize);
    }

    @Override
    public long maxEdges() {
        return Digraph.maxEdges(numVertices);
    }

    @Override
    public boolean isComplete() {
        return numEdges == Digraph.maxEdges(numVertices);
    }

    @Override
    public Digraph<V, E> copy() {
        return copy(true, true, true, true, true);
    }

    @Override
    public Digraph<V, E> copy(boolean vertexWeights, boolean vertexLabels, boolean edges, boolean edgeWeights, boolean edgeLabels) {
        var copy = (DigraphImpl<V, E>) super.copy(vertexWeights, vertexLabels, edges, edgeWeights, edgeLabels);
        //
        copy.indegree = edges ? Arrays.copyOf(indegree, numVertices) : new int[vertices.length];
        copy.predList = new int[numVertices][];
        copy.predPos = new int[numVertices][];
        if (edges) {
            for (int i = 0; i < numVertices; i++) {
                if (predList[i] != null) {
                    copy.predList[i] = Arrays.copyOf(predList[i], predList[i].length);
                    copy.predPos[i] = Arrays.copyOf(predPos[i], predPos[i].length);
                }
            }
        }
        return copy;
    }

    @Override
    public int addEdge(int v, int u) {
        int pos = super.addEdge(v, u);
        int ui = indexOf(u);
        //v -> u: add v to predList of u
        addToPredList(u, v);
        //increase indegree of u
        indegree[ui]++;
        return pos;
    }

    //adds u to the predList of v (v <- ..., u, ...)
    protected int addToPredList(int v, int u) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        if (predList[vi] == null || indegree[vi] == predList[vi].length) {
            growPredList(v);
        }
        int pos = indegree[vi];
        predList[vi][pos] = u;
        predPos[vi][pos] = degree[indexOf(u)] - 1; //v was added at the end of adjList[u]
        return pos;
    }

    @Override
    protected void removeEdgeAt(int vi, int pos) {
        int v = vertices[vi];
        int u = adjList[vi][pos];
        int ui = indexOf(u);
        super.removeEdgeAt(vi, pos); //removing the edge v -> u
        //remove v from the predecessors of u
        int posvu = predListPosOf(u, v);
        if (posvu < indegree[ui] - 1) {
            swapPredWithLast(ui, posvu);
        }
        //decrease indegree of u
        indegree[ui]--;
    }

    protected void swapPredWithLast(int vi, int pos) {
        int lastPos = indegree[vi] - 1;
        predList[vi][pos] = predList[vi][lastPos];
        predPos[vi][pos] = predPos[vi][lastPos];
        //inform the vertex which was swapped of its current pos
        /*
        int w = predList[vi][pos];
        int wi = indexOf(w);
        if (wi != vi) {
            if (!directed) {
                adjPos[wi][adjPos[vi][pos]] = pos;
            }
        } else {
            adjPos[wi][pos] = pos;
        }*/
    }

    //Returns the first position of u in the predecessor list of v.
    protected int predListPosOf(int v, int u) {
        int vi = indexOf(v);
        if (predList[vi] == null) {
            return -1;
        }
        for (int i = 0; i < indegree[vi]; i++) {
            if (predList[vi][i] == u) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int indegree(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        return indegree[vi];
    }

    @Override
    public int[] indegrees() {
        return IntArrays.copyOf(indegree);
    }

    @Override
    public int[] predecessors(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        int[] pred = new int[indegree[vi]];
        for (int pos = 0, indeg = indegree[vi]; pos < indeg; pos++) {
            pred[pos] = predList[vi][pos];
        }
        return pred;
    }

    @Override
    protected void growVertices() {
        super.growVertices();
        indegree = Arrays.copyOf(indegree, vertices.length);
        predList = Arrays.copyOf(predList, vertices.length);
        predPos = Arrays.copyOf(predPos, vertices.length);
    }

    protected void growPredList(int v) {
        int vi = indexOf(v);
        int oldLen = indegree[vi];
        int newLen = Math.max(avgDegree, oldLen + (oldLen >> 1) + 1);
        if (predList[vi] != null) {
            predList[vi] = Arrays.copyOf(predList[vi], newLen);
            predPos[vi] = Arrays.copyOf(predPos[vi], newLen);
        } else {
            predList[vi] = new int[newLen];
            predPos[vi] = new int[newLen];
        }
    }
    
    @Override
    public Digraph<V, E> subgraph(VertexSet vertexSet) {
        return (Digraph<V, E>) super.subgraph(vertexSet);
    }

    @Override
    public Digraph<V, E> subgraph(Collection<Edge> edges) {
        return (Digraph<V, E>) super.subgraph(edges);
    }

    @Override
    public Digraph<V, E> complement() {
        return (Digraph<V, E>) super.complement();
    }

    @Override
    public SuccessorIterator successorIterator(int v, int pos) {
        return new SuccessorIteratorImpl(v, pos);

    }

    @Override
    public PredecessorIterator predecessorIterator(int v, int pos) {
        return new PredecessorIteratorImpl(v, pos);
    }

    @Override
    public NeighborIterator<E> neighborIterator(int v, boolean allEdges) {
        if (allEdges) {
            return new SuccessorPredecessorIteratorImpl(v);
        }
        return super.neighborIterator(v);

    }

    //successors: same as NeighborIterator
    protected class SuccessorIteratorImpl extends NeighborIteratorImpl
            implements SuccessorIterator<E> {

        public SuccessorIteratorImpl(int v) {
            super(v);
        }

        public SuccessorIteratorImpl(int v, int pos) {
            super(v, pos);
        }

    }

    //predecessors
    protected class PredecessorIteratorImpl implements PredecessorIterator<E> {

        protected final int v;
        protected final int vi;
        protected int pos;

        public PredecessorIteratorImpl(int v) {
            this(v, -1);
        }

        public PredecessorIteratorImpl(int v, int pos) {
            this.v = v;
            this.vi = indexOf(v);
            this.pos = pos;
        }

        @Override
        public int adjListPos() {
            return predPos[vi][pos];
        }

        @Override
        public boolean hasNext() {
            return pos < indegree[vi] - 1;
        }

        @Override
        public boolean hasPrevious() {
            return pos > 0;
        }

        @Override
        public int next() {
            if (pos >= indegree[vi] - 1) {
                throw new NoSuchElementException();
            }
            return predList[vi][++pos];
        }

        @Override
        public int previous() {
            if (pos <= 0) {
                throw new NoSuchElementException();
            }
            return predList[vi][--pos];
        }

        @Override
        public void setEdgeWeight(double weight) {
            setEdgeData(WEIGHT, weight);
        }

        @Override
        public double getEdgeWeight() {
            return getEdgeData(WEIGHT, DEFAULT_EDGE_WEIGHT);
        }

        @Override
        public void setEdgeData(int dataType, double value) {
            checkPos();
            int u = predList[vi][pos]; //u -> v
            int ui = indexOf(u);
            setEdgeDataAt(dataType, ui, predPos[vi][pos], value);
        }

        @Override
        public void incEdgeData(int dataType, double amount) {
            checkPos();
            int u = predList[vi][pos]; //u -> v
            int ui = indexOf(u);
            incEdgeDataAt(dataType, ui, predPos[vi][pos], amount);
        }

        @Override
        public double getEdgeData(int dataType) {
            return getEdgeData(dataType, 0);
        }

        @Override
        public double getEdgeData(int dataType, double defaultValue) {
            checkPos();
            if (!hasEdgeData(dataType)) {
                return defaultValue;
            }
            int u = predList[vi][pos]; //u -> v
            int ui = indexOf(u);
            return edgeData[dataType][ui][predPos[vi][pos]];
        }

        @Override
        public void setEdgeLabel(E label) {
            checkPos();
            if (edgeLabel == null) {
                initEdgeLabels();
            }
            int u = predList[vi][pos]; //u -> v
            int ui = indexOf(u);
            edgeLabel[ui][predPos[vi][pos]] = label;
        }

        @Override
        public E getEdgeLabel() {
            checkPos();
            if (edgeLabel == null) {
                return null;
            }
            int u = predList[vi][pos]; //u -> v
            int ui = indexOf(u);
            return edgeLabel[ui][predPos[vi][pos]];
        }

        @Override
        public void removeEdge() {
            checkPos();
            int u = predList[vi][pos]; //u -> v
            int ui = indexOf(u);
            removeEdgeAt(ui, predPos[vi][pos]);
            pos--;
        }

        @Override
        public Edge edge() {
            checkPos();
            int u = predList[vi][pos]; //u -> v
            int ui = indexOf(u);
            return edgeAt(ui, predPos[vi][pos]);
        }

        protected void checkPos() {
            if (pos < 0) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean isSuccessor() {
            return false;
        }

        @Override
        public boolean isPredecessor() {
            return true;
        }
    }

    //successors and predecessors
    //SLOWER THAN IT SHOULD BE
    protected class SuccessorPredecessorIteratorImpl implements NeighborIterator {

        NeighborIterator currentIterator;
        SuccessorIterator succIterator;
        PredecessorIterator predIterator;

        public SuccessorPredecessorIteratorImpl(int v) {
            succIterator = successorIterator(v);
            predIterator = predecessorIterator(v);
        }

        @Override
        public boolean hasNext() {
            if (succIterator.hasNext()) {
                currentIterator = succIterator;
                return true;
            }
            if (predIterator.hasNext()) {
                currentIterator = predIterator;
                return true;
            }
            return false;
        }

        @Override
        public int next() {
            return currentIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            if (predIterator.hasPrevious()) {
                currentIterator = predIterator;
                return true;
            }
            if (succIterator.hasPrevious()) {
                currentIterator = succIterator;
                return true;
            }
            return false;
        }

        @Override
        public int previous() {
            return currentIterator.previous();
        }

        @Override
        public int adjListPos() {
            return currentIterator.adjListPos();
        }

        @Override
        public Edge edge() {
            return currentIterator.edge();
        }

        @Override
        public double getEdgeWeight() {
            return currentIterator.getEdgeWeight();
        }

        @Override
        public void setEdgeWeight(double weight) {
            currentIterator.setEdgeWeight(weight);
        }

        @Override
        public void setEdgeData(int dataType, double value) {
            currentIterator.setEdgeData(dataType, value);
        }

        @Override
        public void incEdgeData(int dataType, double amount) {
            currentIterator.incEdgeData(dataType, amount);
        }

        @Override
        public double getEdgeData(int dataType) {
            return currentIterator.getEdgeData(dataType);
        }

        @Override
        public double getEdgeData(int dataType, double defaultValue) {
            return currentIterator.getEdgeData(dataType, defaultValue);
        }

        @Override
        public Object getEdgeLabel() {
            return currentIterator.getEdgeLabel();
        }

        @Override
        public void setEdgeLabel(Object label) {
            currentIterator.setEdgeLabel(label);
        }

        @Override
        public void removeEdge() {
            currentIterator.removeEdge();
        }

        @Override
        public boolean isSuccessor() {
            return currentIterator == succIterator;
        }

        @Override
        public boolean isPredecessor() {
            return currentIterator == predIterator;
        }

    }
}
