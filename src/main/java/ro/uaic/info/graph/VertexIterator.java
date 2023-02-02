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
package ro.uaic.info.graph;

/**
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels.
 */
public interface VertexIterator<V> {

    /**
     *
     * @return
     */
    boolean hasNext();

    /**
     *
     * @return
     */
    int next();

    /**
     *
     * @param weight
     */
    void setWeight(double weight);

    /**
     *
     * @return
     */
    double getWeight();

    /**
     *
     * @param label
     */
    void setLabel(V label);

    /**
     *
     * @return
     */
    V getLabel();

    /**
     *
     */
    void remove();
}
