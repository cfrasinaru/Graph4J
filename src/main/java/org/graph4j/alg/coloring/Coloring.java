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
import org.graph4j.Graph;
import org.graph4j.util.VertexSet;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

/**
 * A coloring of the vertices of a graph. Coloring algorithms will usually
 * produce as solution an object of this type.
 *
 * @see VertexColoringAlgorithm
 * @author Cristian Frăsinaru
 */
public class Coloring {

    protected final Graph graph;
    protected final int[] vertexColor;
    protected int numColoredVertices = 0;
    protected BitSet usedColors;
    protected Map<Integer, VertexSet> colorMap;

    /**
     * Creates an empty coloring - no vertex has a color assigned to it.
     *
     * @param graph the input graph.
     */
    public Coloring(Graph graph) {
        this.graph = graph;
        vertexColor = new int[graph.numVertices()];
        Arrays.fill(vertexColor, -1);
        this.usedColors = new BitSet();
    }

    /**
     *
     * @param graph the input graph.
     * @param other a vertex coloring.
     */
    public Coloring(Graph graph, Coloring other) {
        this.graph = graph;
        if (other.graph == graph) {
            this.numColoredVertices = other.numColoredVertices;
            this.vertexColor = Arrays.copyOf(other.vertexColor, graph.numVertices());
            assert vertexColor.length == graph.numVertices();
            this.usedColors = (BitSet) other.usedColors.clone();
            if (other.colorMap != null) {
                this.colorMap = new HashMap<>();
                for (var entry : other.colorMap.entrySet()) {
                    int color = entry.getKey();
                    VertexSet set = entry.getValue();
                    this.colorMap.put(color, new VertexSet(this.graph, set.vertices()));
                }
            }
        } else {
            vertexColor = new int[graph.numVertices()];
            Arrays.fill(vertexColor, -1);
            this.usedColors = new BitSet();
            for (int v : graph.vertices()) {
                int col = other.getColor(v);
                if (col >= 0) {
                    this.setColor(v, col);
                }
            }
        }
    }

    /**
     * Creates a vertex coloring using the colors in the given array: the color
     * of the vertex with index {@code i} in the graph is {@code colors[i]}.
     *
     * @param graph the input graph.
     * @param colors an array of color numbers.
     */
    public Coloring(Graph graph, int colors[]) {
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
    public Coloring(Graph graph, List<VertexSet> colorClasses) {
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
        int pos = graph.indexOf(v);
        if (pos < 0) {
            return false;
        }
        return vertexColor[pos] >= 0;
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

    private boolean checkColorUsed(int color) {
        for (int i = 0; i < vertexColor.length; i++) {
            if (vertexColor[i] == color) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assigns a color to the specified vertex.
     *
     * @param v a vertex number.
     * @param color the color to be set, or {@code -1} to uncolor the specified
     * vertex.
     */
    public final void setColor(int v, int color) {
        int vi = graph.indexOf(v);
        int oldColor = vertexColor[vi];
        if (oldColor == color) {
            return;
        }
        vertexColor[vi] = color;
        if (oldColor < 0 && color >= 0) {
            numColoredVertices++;
        } else if (oldColor >= 0 && color < 0) {
            numColoredVertices--;
        }
        if (color >= 0) {
            usedColors.set(color);
        } else {
            usedColors.set(color, checkColorUsed(color));
        }
        if (oldColor >= 0) {
            usedColors.set(oldColor, checkColorUsed(oldColor));
        }

        //update colorMap
        if (colorMap != null) {
            var set = colorMap.get(color);
            if (color >= 0) {
                if (set == null) {
                    set = new VertexSet(graph);
                    colorMap.put(color, set);
                }
                set.add(v);
            } else {
                if (set != null) {
                    set.remove(v);
                    if (set.isEmpty()) {
                        colorMap.remove(color);
                    }
                }
            }
            if (oldColor >= 0) {
                var oldSet = colorMap.get(oldColor);
                if (oldSet != null) {
                    oldSet.remove(v);
                    if (oldSet.isEmpty()) {
                        colorMap.remove(oldColor);
                    }
                }
            }
        }
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
        return vi < 0 || vi >= vertexColor.length ? -1 : vertexColor[vi];
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

    //key=color, value = vertices colored with the color
    private void createColorClasses() {
        colorMap = new HashMap<>();
        for (int i = 0; i < vertexColor.length; i++) {
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
     *
     * @param color a color.
     * @return the number of vertices colored with color.
     */
    public int numColoredVertices(int color) {
        if (colorMap == null) {
            createColorClasses();
        }
        var set = colorMap.get(color);
        return set == null ? 0 : set.size();
        /*
        int count = 0;
        for (int i = 0; i < vertexColor.length; i++) {
            if (vertexColor[i] == color) {
                count++;
            }
        }
        return count;
         */
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
            checkProper();
            return true;
        } catch (InvalidColoringException e) {
            return false;
        }
    }

    /**
     *
     * @return {@code true} if the coloring is equitable.
     */
    public boolean isEquitable() {
        try {
            checkEquitable();
            return true;
        } catch (InvalidColoringException e) {
            return false;
        }
    }

    /**
     * If the coloring is not proper, it throws an exception.
     */
    public void checkProper() {
        assert graph.numVertices() == vertexColor.length;
        for (int v : graph.vertices()) {
            int vc = vertexColor[graph.indexOf(v)];
            if (vc == -1) {
                continue;
            }
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int uc = vertexColor[graph.indexOf(u)];
                if (uc != -1 && vc == uc) {
                    throw new InvalidColoringException(v, u, vc);
                }
            }
        }
    }

    /**
     * If the coloring is not equitable, it throws an exception.
     */
    public void checkEquitable() {
        getColorClasses();
        int k = colorMap.size();
        for (int i = 0; i < k - 1; i++) {
            var set1 = colorMap.get(i);
            int size1 = set1 == null ? 0 : set1.size();
            for (int j = i + 1; j < k; j++) {
                var set2 = colorMap.get(j);
                int size2 = set2 == null ? 0 : set2.size();
                if (Math.abs(size1 - size2) > 1) {
                    throw new InvalidColoringException(
                            "Invalid color class sizes: "
                            + size1 + ", " + size2
                            + "\n\t" + set1 + "\n\t" + set2);
                }
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
     * @return the maximum color number that was used.
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
        for (int i = 0; i < vertexColor.length; i++) {
            int vc = vertexColor[i];
            if (vc != -1) {
                sb.add(graph.vertexAt(i) + ":" + vc);
            }
        }
        return sb.toString() + "\n" + getColorClasses().toString();
         
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
        final Coloring other = (Coloring) obj;
        if (!Objects.equals(this.graph, other.graph)) {
            return false;
        }
        return Arrays.equals(this.vertexColor, other.vertexColor);
    }

}
