///*
// * Copyright (C) 2024 Cristian Frăsinaru and contributors
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.graph4j;
//
//import static org.graph4j.Network.COST;
//import static org.graph4j.Network.FLOW;
//
///**
// * {@inheritDoc}
// *
// * @author Cristian Frăsinaru
// */
//public class NetworkEdge<E> extends Edge<E> {
//
//    public NetworkEdge(int source, int target) {
//        super(source, target);
//    }
//
//    public double cost() {
//        //tricky, don't invoke this method if you expect null
//        if (data[COST] == null) {
//            return Network.DEFAULT_EDGE_COST;
//        }
//        return data[COST];
//    }
//
//    public double flow() {
//        //tricky, don't invoke this method if you expect null
//        if (data[FLOW] == null) {
//            return Network.DEFAULT_EDGE_FLOW;
//        }
//        return data[FLOW];
//    }
//       
//}
