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
 * Generates weights for the edges of a graph.
 *
 * @see EdgeDataGenerator
 * @author Cristian Frăsinaru
 */
public class EdgeWeightsGenerator {

    /**
     *
     * @param graph the input graph.
     * @param min minimum weight (inclusive).
     * @param max maximum weight (inclusive).
     */
    public static void randomIntegers(Graph graph, int min, int max) {
        Objects.requireNonNull(graph);
        Validator.checkRange(min, max);
        Random rnd = new Random();
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            it.setWeight(min + rnd.nextInt(max - min + 1));
        }
    }

    /**
     * Each edge will receive a distinct weight, between {@code 0} and
     * {@code numEdges - 1}.
     *
     * @param graph the input graph.
     */
    public static void consecutiveIntegers(Graph graph) {
        Objects.requireNonNull(graph);
        int weight = 0;
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            it.setWeight(++weight);
        }
    }

    /**
     *
     * @param graph the input graph.
     * @param min minimum weight (inclusive).
     * @param max maximum weight (inclusive).
     */
    public static void randomDoubles(Graph graph, double min, double max) {
        Objects.requireNonNull(graph);
        Validator.checkRange(min, max);
        Random rnd = new Random();
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            double weight = min + (max - min + Double.MIN_NORMAL) * rnd.nextDouble();
            it.setWeight(weight);
        }
    }

    /**
     *
     * @param graph the input graph.
     * @param value the weight of all edges.
     */
    public static void fill(Graph graph, double value) {
        Objects.requireNonNull(graph);
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            it.setWeight(value);
        }
    }

}
