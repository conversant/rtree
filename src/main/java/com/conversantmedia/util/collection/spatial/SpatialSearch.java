package com.conversantmedia.util.collection.spatial;

/*
 * #%L
 * Conversant RTree
 * ~~
 * Conversantmedia.com © 2016, Conversant, Inc. Conversant® is a trademark of Conversant, Inc.
 * ~~
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.function.Consumer;
import java.util.Collection;

/**
 * Created by jcovert on 12/30/15.
 */
public interface SpatialSearch<T> {
    /**
     * Search for entries intersecting given bounding rect
     *
     * @param rect - Bounding rectangle to use for querying
     * @param t - Array to store found entries
     *
     * @return Number of results found
     */
    int intersects(HyperRect rect, T[] t);

    /**
     * Search for entries intersecting given bounding rect
     *
     * @param rect - Bounding rectangle to use for querying
     * @param consumer - callback to receive intersecting objects
     *
     */
    void intersects(HyperRect rect, Consumer<T> consumer);

    /**
     * Search for entries contained by the given bounding rect
     *
     * @param rect - Bounding rectangle to use for querying
     * @param t - Array to store found entries
     *
     * @return Number of results found
     */
    int search(HyperRect rect, T[] t);

    /**
     * Search for entries contained by the given bounding rect
     *
     * @param rect - Bounding rectangle to use for querying
     * @param consumer - callback to receive intersecting objects
     *
     */
    void search(HyperRect rect, Consumer<T> consumer);

    /**
     * Search for entries contained by the given bounding rect
     *
     * @param rect - Bounding rectangle to use for querying
     * @param collection - collection to receive results
     *
     */
    void search(HyperRect rect, Collection<T> collection);

    /**
     * returns whether or not the HyperRect will enclose all of the data entries in t
     *
     * @param t - entry
     *
     * @return boolean - Whether or not all entries lie inside rect
     */
    boolean contains(T t);

    /**
     * Add the data entry to the SpatialSearch structure
     *
     * @param t Data entry to be added
     */
    void add(T t);

    /**
     * Remove the data entry from the SpatialSearch structure
     *
     * @param t Data entry to be removed
     */
    void remove(T t);

    /**
     * Update entry in tree
     *
     * @param told - Entry to update
     * @param tnew - Entry to update it to
     */
    void update(T told, T tnew);

    /**
     * Get the number of entries in the tree
     *
     * @return entry count
     */
    int getEntryCount();

    /**
     * Iterate over all entries in the tree
     *
     * @param consumer - callback for each element
     */
    void forEach(Consumer<T> consumer);

    Stats collectStats();

}
