package com.conversantmedia.util.collection.spatial;

/**
 * Created by jcairns on 4/30/15.
 */
public interface RectBuilder<T> {

    /**
     * Build a bounding rectangle for the given element
     *
     * @param t - element to bound
     *
     * @return HyperRect impl for this entry
     */
    HyperRect getBBox(T t);


    /**
     * Build a bounding rectangle for given points (min and max, usually)
     *
     * @param p1 - first point (top-left point, for example)
     * @param p2 - second point (bottom-right point, for example)
     *
     * @return HyperRect impl defined by two points
     */
    HyperRect getMbr(HyperPoint p1, HyperPoint p2);
}
