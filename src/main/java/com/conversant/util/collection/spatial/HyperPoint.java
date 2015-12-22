package com.conversant.util.collection.spatial;

/**
 * Created by jcairns on 5/5/15.
 */
public interface HyperPoint {
    int getNDim();

    <D extends Comparable<D>> D getCoord(int d);

    double distance(HyperPoint p);

    double distance(HyperPoint p, int d);

}
