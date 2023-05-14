/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.alg.coloring;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexSet;

/**
 * A coloring of the vertices of a graph. Coloring algorithms will usually
 * produce as solution an object of this type.
 *
 * @see VertexColoringAlgorithm
 * @author Cristian Frăsinaru
 */
public class VertexColoring {

    private final Graph graph;
    private final int[] vertexColor;
    private int numColoredVertices = 0;
    private BitSet usedColors;
    private Map<Integer, VertexSet> colorMap;
    private int[] uncoloredNeighbors;

    /**
     * Creates an empty coloring - no vertex has a color assigned to it.
     *
     * @param graph the input graph.
     */
    public VertexColoring(Graph graph) {
        this(graph, graph.numVertices());
    }

    /**
     *
     * @param graph the input graph.
     * @param estimatedNumColors the estimated number of colors.
     */
    public VertexColoring(Graph graph, int estimatedNumColors) {
        this.graph = graph;
        vertexColor = new int[graph.numVertices()];
        Arrays.fill(vertexColor, -1);
        this.usedColors = new BitSet();
        //this.uncoloredNeighbors = new int[estimatedNumColors];
    }

    /**
     *
     * @param graph the input graph.
     * @param other a vertex coloring.
     */
    public VertexColoring(Graph graph, VertexColoring other) {
        this.graph = other.graph;
        this.numColoredVertices = other.numColoredVertices;
        this.vertexColor = IntArrays.copyOf(other.vertexColor);
        this.usedColors = (BitSet) other.usedColors.clone();
        if (other.colorMap != null) {
            this.colorMap = new HashMap<>(other.colorMap);
        }
        if (other.uncoloredNeighbors != null) {
            this.uncoloredNeighbors = IntArrays.copyOf(other.uncoloredNeighbors);
        }
    }

    /**
     * Creates a vertex coloring using the colors in the given array: the color
     * of the vertex with index {@code i} in the graph is {@code colors[i]}.
     *
     * @param graph the input graph.
     * @param colors an array of color numbers.
     */
    public VertexColoring(Graph graph, int colors[]) {
        this(graph);
        for (int i = 0; i < colors.length; i++) {
            this.vertexColor[i] = colors[i];
            if (colors[i] >= 0) {
                numColoredVertices++;
                this.usedColors.set(colors[i]);
            }
        }
    }

    /**
     * Creates a vertex coloring using the specified color classes: all the
     * vertices in the i-th set of the {@code colorClasses} list are assigned
     * color {@code i}.
     *
     * @param graph the input graph.
     * @param colorClasses the already computed color classes.
     */
    public VertexColoring(Graph graph, List<VertexSet> colorClasses) {
        this(graph);
        for (int color = 0, k = colorClasses.size(); color < k; color++) {
            for (int v : colorClasses.get(color).vertices()) {
                vertexColor[graph.indexOf(v)] = color;
                if (color >= 0) {
                    numColoredVertices++;
                    usedColors.set(color);
                }
            }
        }
    }

    /**
     *
     * @return the graph on which this coloring is defined.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns {@code true} if a color has been set for a vertex v.
     *
     * @param v a vertex number.
     * @return {@code true} if a color has been set for the vertex v.
     */
    public boolean isColorSet(int v) {
        return vertexColor[graph.indexOf(v)] >= 0;
    }

    /**
     * Returns {@code true} if the given color has been used for some vertex.
     *
     * @param color a color number.
     * @return {@code true} if the color has been used for some vertex.
     */
    public boolean isColorUsed(int color) {
        return usedColors.get(color);
    }

    /**
     *
     * @param color a color
     * @return the number of uncolored vertices adjacent to vertices colored
     * with the given color.
     */
    public int uncoloredNeighbors(int color) {
        return uncoloredNeighbors[color];
    }

    /**
     * Assigns a color to the specified vertex.
     *
     * @param v a vertex number.
     * @param color the color to be set, or {@code -1} to uncolor the specified
     * vertex.
     */
    public void setColor(int v, int color) {
        int vi = graph.indexOf(v);
        int oldColor = vertexColor[vi];
        vertexColor[vi] = color;
        if (oldColor < 0 && color >= 0) {
            numColoredVertices++;
        } else if (oldColor >= 0 && color < 0) {
            numColoredVertices--;
        }
        usedColors.set(color, color >= 0);

        if (uncoloredNeighbors != null) {
            int x = color >= 0 ? 1 : -1;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int other = getColor(u);
                if (other < 0) {
                    uncoloredNeighbors[color] += x;
                } else {
                    uncoloredNeighbors[other] -= x;
                }
            }
            //System.out.println(v + " colored with " + color);
            //System.out.println("Uncolored neigbors: " + Arrays.toString(uncoloredNeighbors));
        }

        colorMap = null;
    }

    /**
     * Returns the color assigned to a vertex v, or {@code -1} if no color has
     * been set.
     *
     * @param v a vertex number;
     * @return the color assigned to v, or {@code -1} if no color has been set.
     */
    public int getColor(int v) {
        int vi = graph.indexOf(v);
        return vi >= 0 ? vertexColor[vi] : -1;
    }

    /**
     * Creates and returns the color classes. It is executed in a lazy fashion:
     * if the color classes are already created, it only returns them. If the
     * coloring changes, the color classes are computed again.
     *
     * @return the color classes.
     */
    public Map<Integer, VertexSet> getColorClasses() {
        if (colorMap == null) {
            createColorClasses();
        }
        return colorMap;
    }

    private void createColorClasses() {
        colorMap = new HashMap<>();
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            int color = vertexColor[i];
            if (color == -1) {
                continue;
            }
            var set = colorMap.get(color);
            if (set == null) {
                set = new VertexSet(graph);
                colorMap.put(color, set);
            }
            set.add(graph.vertexAt(i));
        }
    }

    /**
     * Return the number of used colors.
     *
     * @return the number of used colors.
     */
    public int numUsedColors() {
        return usedColors.cardinality();
    }

    /**
     * Returns the number of vertices which have been assigned a color.
     *
     * @return the number of colored vertices.
     */
    public int numColoredVertices() {
        return numColoredVertices;
    }

    /**
     * Returns {@code true} if no vertex has been colored.
     *
     * @return {@code true} if no vertex has been colored.
     */
    public boolean isEmpty() {
        return numColoredVertices == 0;
    }

    /**
     * Returns {@code true} if all the vertices have been colored.
     *
     * @return {@code true} if all the vertices have been colored.
     */
    public boolean isComplete() {
        return numColoredVertices == vertexColor.length;
    }

    /**
     * A proper coloring is an assignment of colors to the vertices of a graph
     * so that no two adjacent vertices have the same color.
     *
     * @return {@code true} if the coloring is proper.
     */
    public boolean isProper() {
        try {
            checkValidity();
            return true;
        } catch (InvalidColoringException e) {
            return false;
        }
    }

    /**
     * If the coloring is not proper, it throws an exception.
     */
    public void checkValidity() {
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            int vi = graph.indexOf(e.source());
            int ui = graph.indexOf(e.target());
            int vc = vertexColor[vi];
            int uc = vertexColor[ui];
            if (vc != -1 && uc != -1 && vc == uc) {
                throw new InvalidColoringException(e.source(), e.target(), vc);
            }
        }
    }

    /**
     *
     * @return the set of the colored vertices.
     */
    public VertexSet getColoredVertices() {
        VertexSet set = new VertexSet(graph, numColoredVertices);
        for (var v : graph.vertices()) {
            if (isColorSet(v)) {
                set.add(v);
            }
        }
        return set;
    }

    /**
     *
     * @return the set of the vertices that do not have a color.
     */
    public VertexSet getUncoloredVertices() {
        VertexSet set = new VertexSet(graph, graph.numVertices() - numColoredVertices);
        for (var v : graph.vertices()) {
            if (!isColorSet(v)) {
                set.add(v);
            }
        }
        return set;
    }

    /**
     *
     * @param vertices a set of vertices.
     * @return the distinct colors used by the given vertices.
     */
    public Set<Integer> getColorsUsedBy(VertexSet vertices) {
        Set<Integer> set = new HashSet<>();
        for (var v : vertices) {
            if (isColorSet(v)) {
                set.add(v);
            }
        }
        return set;
    }

    /**
     *
     * @return the maximum color number.
     */
    public int maxColorNumber() {
        int max = -1;
        for (int i = 0; i < vertexColor.length; i++) {
            if (max < vertexColor[i]) {
                max = vertexColor[i];
            }
        }
        return max;
    }

    @Override
    public String toString() {
        var sb = new StringJoiner(",");
        for (int v : graph.vertices()) {
            int vc = vertexColor[graph.indexOf(v)];
            if (vc != -1) {
                sb.add(v + ":" + vc);
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.graph);
        hash = 59 * hash + Arrays.hashCode(this.vertexColor);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VertexColoring other = (VertexColoring) obj;
        if (!Objects.equals(this.graph, other.graph)) {
            return false;
        }
        return Arrays.equals(this.vertexColor, other.vertexColor);
    }

}
