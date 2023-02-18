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
package ro.uaic.info.graph.util;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Adapted after {@code LinearProbingHashST},
 * <a href="https://algs4.cs.princeton.edu/34hash">Section 3.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Cristian Frăsinaru
 * @param <K>
 */
public class Map2Long<K> {

    private static final int INITIAL_CAPACITY = 32;
    private static final long DEFAULT_RETURN_VALUE = -1;

    private int size;
    private int capacity;
    private K[] keys;
    private long[] values;

    public Map2Long() {
        this(INITIAL_CAPACITY);
    }

    public Map2Long(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.keys = (K[]) new Object[capacity];
        this.values = new long[capacity];
    }

    public Map2Long(Map2Long other) {
        this.size = other.size;
        this.capacity = other.capacity;
        this.keys = Arrays.copyOf(keys, capacity);
        this.values = Arrays.copyOf(values, capacity);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(K key) {
        return get(key) != DEFAULT_RETURN_VALUE;
    }

    //returns a value between 0 and capacity-1 
    //assumes capacity is a power of 2
    private int hash(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12) ^ (h >>> 7) ^ (h >>> 4);
        return h & (capacity - 1);
    }

    // resizes the hash table to the given capacity by re-hashing all of the keys
    private void resize(int newCapacity) {
        System.out.println("resizing to " + newCapacity);
        Map2Long<K> copy = new Map2Long<>(newCapacity);
        for (int i = 0; i < this.capacity; i++) {
            if (keys[i] != null) {
                copy.put(keys[i], values[i]);
            }
        }
        this.keys = copy.keys;
        this.values = copy.values;
        this.capacity = newCapacity;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void put(K key, long value) {
        if (key == null) {
            throw new NullPointerException();
        }
        // double table size if 50% full
        if (size >= capacity / 2) {
            resize(2 * capacity);
        }
        int i;
        for (i = hash(key); keys[i] != null; i = (i + 1) % capacity) {
            if (keys[i].equals(key)) {
                values[i] = value;
                return;
            }
        }
        keys[i] = key;
        values[i] = value;
        size++;
    }

    /**
     *
     * @param key
     * @return
     */
    public long get(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        for (int i = hash(key); keys[i] != null; i = (i + 1) % capacity) {
            if (keys[i].equals(key)) {
                return values[i];
            }
        }
        return DEFAULT_RETURN_VALUE;
    }

    /**
     *
     * @param key
     */
    public void remove(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (!contains(key)) {
            return;
        }

        // find position i of key
        int i = hash(key);
        while (!key.equals(keys[i])) {
            i = (i + 1) % capacity;
        }

        // delete key and associated value
        keys[i] = null;
        values[i] = DEFAULT_RETURN_VALUE;

        // rehash all keys in same cluster
        i = (i + 1) % capacity;
        while (keys[i] != null) {
            // delete keys[i] and vals[i] and reinsert
            K keyToRehash = keys[i];
            long valToRehash = values[i];
            keys[i] = null;
            values[i] = DEFAULT_RETURN_VALUE;
            size--;
            put(keyToRehash, valToRehash);
            i = (i + 1) % capacity;
        }

        size--;

        // halves size of array if it's 12.5% full or less
        if (size > 0 && size <= capacity / 8) {
            resize(capacity / 2);
        }
    }

    /**
     *
     * @return
     */
    public K[] keys() {
        K[] temp = (K[]) new Object[size];
        int k = 0;
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null) {
                temp[k++] = keys[i];
            }
        }
        return temp;
    }

    /**
     *
     * @return
     */
    public long[] values() {
        long[] temp = new long[size];
        int k = 0;
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null) {
                temp[k++] = values[i];
            }
        }
        return temp;
    }

    @Override
    public String toString() {
        var str = new StringJoiner(",", "{", "}");
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null) {
                str.add(keys[i] + "=" + values[i]);
            }
        }
        return str.toString();
    }

}
