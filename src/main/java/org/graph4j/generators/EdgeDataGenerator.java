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
package org.graph4j.generators;

import java.util.Objects;
import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.util.Validator;

/**
 * Generates costs for the edges of a network.
 *
 * @author Cristian Frăsinaru
 */
public class EdgeDataGenerator {

    private final Graph graph;
    private final int dataType;

    /**
     * Creates a generator for setting random or specific values on the edges of
     * a graph.The data type of the values may be {@code Graph.WEIGHT},
     * {@code Network.CAPACITY}, {@code Network.COST} or user defined.
     *
     * @param graph the input graph.
     * @param dataType the id of an edge data type.
     */
    public EdgeDataGenerator(Graph graph, int dataType) {
        Objects.requireNonNull(graph);
        this.dataType = dataType;
        this.graph = graph;
    }

    /**
     *
     * @param min minimum cost (inclusive).
     * @param max maximum cost (inclusive).
     */
    public void randomIntegers(int min, int max) {
        Validator.checkRange(min, max);
        Random rnd = new Random();
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            it.setData(dataType, min + rnd.nextInt(max - min + 1));
        }
    }

    /**
     * Each edge will receive a distinct cost, between {@code 0} and
     * {@code numEdges - 1}.
     */
    public void consecutiveIntegers() {
        int cost = 0;
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            it.setData(dataType, ++cost);
        }
    }

    /**
     *
     * @param min minimum cost (inclusive).
     * @param max maximum cost (inclusive).
     */
    public void randomDoubles(double min, double max) {
        Validator.checkRange(min, max);
        Random rnd = new Random();
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            double cost = min + (max - min + Double.MIN_NORMAL) * rnd.nextDouble();
            it.setData(dataType, cost);
        }
    }

    /**
     *
     * @param value the cost of all edges.
     */
    public void fill(double value) {
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            it.setData(dataType, value);
        }
    }

}
