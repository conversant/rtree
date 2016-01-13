package com.dotomi.util.collection.spatial;

import java.util.function.Consumer;

/**
 * Created by jcairns on 4/30/15.
 */
interface Node<T> {
    /**
     * @return boolean - true if this node is a leaf
     */
    boolean isLeaf();

    /**
     * @return Rect - the bounding rectangle for this node
     */
    HyperRect getRect();

    /**
     * Add t to the index
     *
     * @param t - value to add to index
     */
    Node<T> add(T t);

    /**
     * Remove t from the index
     *
     * @param t - value to remove from index
     */
    Node<T> remove(T t);

    /**
     * update an existing t in the index
     *
     * @param told - old index to be updated
     * @param tnew - value to update old index to
     */
    Node<T> update(T told, T tnew);

    /**
     * Search for rect within node
     *
     * @param rect
     * @param t
     * @param n
     * @return
     */

    int search(HyperRect rect, T[] t, int n);

    int size();

    void forEach(Consumer<T> consumer);

    void forEach(Consumer<T> consumer, HyperRect rect);

    void collectStats(Stats stats, int depth);

    Node<T> instrument();
}
