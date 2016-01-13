package com.dotomi.util.collection.spatial;

/**
 * N dimensional point used to signify the bounds of a HyperRect
 *
 * Created by jcairns on 5/5/15.
 */
public interface HyperPoint {
    int getNDim();

    <D extends Comparable<D>> D getCoord(int d);

    double distance(HyperPoint p);

    double distance(HyperPoint p, int d);

}
