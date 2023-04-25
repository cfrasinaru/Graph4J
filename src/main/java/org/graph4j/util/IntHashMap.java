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
package org.graph4j.util;

/**
 * Adappte after the implementation of IntHashMap in Apache Commons Collection.
 *
 * @author Justin Couch
 * @author Alex Chaffee (alex@apache.org)
 * @author Stephen Colebourne
 * @author Cristian Frăsinaru
 */
public class IntHashMap {

    public static final int NONE = -1;
    /**
     * The hash table data.
     */
    private transient Entry table[];

    /**
     * The total number of entries in the hash table.
     */
    private transient int count;

    /**
     * The table is rehashed when its size exceeds this threshold. (The value of
     * this field is (int)(capacity * loadFactor).)
     *
     * @serial
     */
    private int threshold;

    /**
     * The load factor for the hashtable.
     *
     * @serial
     */
    private float loadFactor;

    /**
     * <p>
     * Innerclass that acts as a datastructure to create a new entry in the
     * table.</p>
     */
    private static class Entry {

        int hash;
        int key;
        int value;
        Entry next;

        /**
         * <p>
         * Create a new entry with the given values.</p>
         *
         * @param hash The code used to hash the object with
         * @param key The key used to enter this in the table
         * @param value The value for this key
         * @param next A reference to the next entry in the table
         */
        protected Entry(int hash, int key, int value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * <p>
     * Constructs a new, empty hashtable with a default capacity and load
     * factor, which is <code>20</code> and <code>0.75</code> respectively.</p>
     */
    public IntHashMap() {
        this(20, 0.75f);
    }

    /**
     * <p>
     * Constructs a new, empty hashtable with the specified initial capacity and
     * default load factor, which is <code>0.75</code>.</p>
     *
     * @param initialCapacity the initial capacity of the hashtable.
     * @throws IllegalArgumentException if the initial capacity is less than
     * zero.
     */
    public IntHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    /**
     * <p>
     * Constructs a new, empty hashtable with the specified initial capacity and
     * the specified load factor.</p>
     *
     * @param initialCapacity the initial capacity of the hashtable.
     * @param loadFactor the load factor of the hashtable.
     * @throws IllegalArgumentException if the initial capacity is less than
     * zero, or if the load factor is nonpositive.
     */
    public IntHashMap(int initialCapacity, float loadFactor) {
        super();
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        if (loadFactor <= 0) {
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }

        this.loadFactor = loadFactor;
        table = new Entry[initialCapacity];
        threshold = (int) (initialCapacity * loadFactor);
    }

    /**
     *
     * @param other
     */
    public IntHashMap(IntHashMap other) {
        this.loadFactor = other.loadFactor;
        this.threshold = other.threshold;
        this.table = new Entry[other.table.length];
        this.count = 0;
        for (int i = other.table.length; i-- > 0;) {
            for (Entry e = other.table[i]; e != null; e = e.next) {
                this.put(e.key, e.value);
            }
        }
    }

    /**
     * <p>
     * Returns the number of keys in this hashtable.</p>
     *
     * @return the number of keys in this hashtable.
     */
    public int size() {
        return count;
    }

    /**
     * <p>
     * Tests if this hashtable maps no keys to values.</p>
     *
     * @return  <code>true</code> if this hashtable maps no keys to values;
     * <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * <p>
     * Tests if some key maps into the specified value in this hashtable. This
     * operation is more expensive than the <code>containsKey</code> method.</p>
     *
     * <p>
     * Note that this method is identical in functionality to containsValue,
     * (which is part of the Map interface in the collections framework).</p>
     *
     * @param value a value to search for.
     * @return     <code>true</code> if and only if some key maps to the
     * <code>value</code> argument in this hashtable as determined by the
     * <tt>equals</tt> method; <code>false</code> otherwise.
     * @throws NullPointerException if the value is <code>null</code>.
     * @see #containsKey(int)
     * @see #containsValue(Object)
     * @see java.util.Map
     */
    public boolean containsValue(int value) {
        Entry tab[] = table;
        for (int i = tab.length; i-- > 0;) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                if (e.value == value) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Tests if the specified object is a key in this hashtable.</p>
     *
     * @param key possible key.
     * @return <code>true</code> if and only if the specified object is a key in
     * this hashtable, as determined by the <tt>equals</tt>
     * method; <code>false</code> otherwise.
     * @see #contains(Object)
     */
    public boolean containsKey(int key) {
        Entry tab[] = table;
        int hash = key;
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Returns the value to which the specified key is mapped in this map.</p>
     *
     * @param key a key in the hashtable.
     * @return the value to which the key is mapped in this hashtable;
     * <code>null</code> if the key is not mapped to any value in this
     * hashtable.
     * @see #put(int, Object)
     */
    public int get(int key) {
        Entry tab[] = table;
        int hash = key;
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash) {
                return e.value;
            }
        }
        return NONE;
    }

    /**
     *
     * @param key a key.
     * @param defaultValue the returned value if the key is not in the
     * hashtable.
     * @return the value to which the key is mapped in this hashtable, or the
     * default value if the key is not in the hashtable.
     */
    public int getOrDefault(int key, int defaultValue) {
        int value = get(key);
        return value == NONE ? defaultValue : value;
    }

    /**
     * <p>
     * Increases the capacity of and internally reorganizes this hashtable, in
     * order to accommodate and access its entries more efficiently.</p>
     *
     * <p>
     * This method is called automatically when the number of keys in the
     * hashtable exceeds this hashtable's capacity and load factor.</p>
     */
    protected void rehash() {
        int oldCapacity = table.length;
        Entry oldMap[] = table;

        int newCapacity = oldCapacity * 2 + 1;
        Entry newMap[] = new Entry[newCapacity];

        threshold = (int) (newCapacity * loadFactor);
        table = newMap;

        for (int i = oldCapacity; i-- > 0;) {
            for (Entry old = oldMap[i]; old != null;) {
                Entry e = old;
                old = old.next;

                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = newMap[index];
                newMap[index] = e;
            }
        }
    }

    /**
     * <p>
     * Maps the specified <code>key</code> to the specified <code>value</code>
     * in this hashtable. The key cannot be <code>null</code>. </p>
     *
     * <p>
     * The value can be retrieved by calling the <code>get</code> method with a
     * key that is equal to the original key.</p>
     *
     * @param key the hashtable key.
     * @param value the value.
     * @return the previous value of the specified key in this hashtable, or
     * <code>null</code> if it did not have one.
     * @throws NullPointerException if the key is <code>null</code>.
     * @see #get(int)
     */
    public final int put(int key, int value) {
        // Makes sure the key is not already in the hashtable.
        Entry tab[] = table;
        int hash = key;
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash) {
                int old = e.value;
                e.value = value;
                return old;
            }
        }

        if (count >= threshold) {
            // Rehash the table if the threshold is exceeded
            rehash();

            tab = table;
            index = (hash & 0x7FFFFFFF) % tab.length;
        }

        // Creates the new entry.
        Entry e = new Entry(hash, key, value, tab[index]);
        tab[index] = e;
        count++;
        return NONE;
    }

    /**
     * <p>
     * Removes the key (and its corresponding value) from this hashtable.</p>
     *
     * <p>
     * This method does nothing if the key is not present in the hashtable.</p>
     *
     * @param key the key that needs to be removed.
     * @return the value to which the key had been mapped in this hashtable, or
     * <code>null</code> if the key did not have a mapping.
     */
    public int remove(int key) {
        Entry tab[] = table;
        int hash = key;
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry e = tab[index], prev = null; e != null; prev = e, e = e.next) {
            if (e.hash == hash) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                count--;
                int oldValue = e.value;
                e.value = NONE;
                return oldValue;
            }
        }
        return NONE;
    }

    /**
     * <p>
     * Clears this hashtable so that it contains no keys.</p>
     */
    public synchronized void clear() {
        Entry tab[] = table;
        for (int index = tab.length; --index >= 0;) {
            tab[index] = null;
        }
        count = 0;
    }

    /**
     *
     * @return the keys in the hash table.
     */
    public int[] keys() {
        int[] keys = new int[count];
        int j = 0;
        for (int i = table.length; i-- > 0;) {
            for (Entry e = table[i]; e != null; e = e.next) {
                keys[j++] = e.key;
            }
        }
        return keys;
    }

    /**
     *
     * @return the values in the hash table.
     */
    public int[] values() {
        int[] values = new int[count];
        int j = 0;
        for (int i = table.length; i-- > 0;) {
            for (Entry e = table[i]; e != null; e = e.next) {
                values[j++] = e.value;
            }
        }
        return values;
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (int i = table.length; i-- > 0;) {
            for (Entry e = table[i]; e != null; e = e.next) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(e.key).append(":").append(e.value);
            }
        }
        return "{" + sb.toString() + "}";

    }

}
