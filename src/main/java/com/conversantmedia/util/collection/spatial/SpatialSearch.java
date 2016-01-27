package com.conversantmedia.util.collection.spatial;

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
    int search(final HyperRect rect, final T[] t);


    /**
     * Add the data entry to the SpatialSearch structure
     *
     * @param t Data entry to be added
     */
    void add(final T t);

    /**
     * Remove the data entry from the SpatialSearch structure
     *
     * @param t Data entry to be removed
     */
    void remove(final T t);

    /**
     * Update entry in tree - performs a remove and an add
     *
     * @param told - Entry to update
     * @param tnew - Entry to update it to
     */
    void update(final T told, final T tnew);

}
