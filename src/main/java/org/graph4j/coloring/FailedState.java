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

import java.util.List;
import java.util.Objects;
import org.graph4j.util.Domain;

/**
 *
 * @author Cristian Frăsinaru
 */
class FailedState {

    final int vertex; //pivot
    final List<Domain> domains;

    public FailedState(int vertex, List<Domain> domains) {
        this.vertex = vertex;
        this.domains = domains;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.vertex;
        hash = 31 * hash + Objects.hashCode(this.domains);
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
        final FailedState other = (FailedState) obj;
        if (this.vertex != other.vertex) {
            return false;
        }
        return Objects.equals(this.domains, other.domains);
    }

    @Override
    public String toString() {
        return vertex + ": " + domains;
    }

}
