package com.dotomi.util.collection.spatial;

/**
 * An N dimensional rectangle or "hypercube" that is a representation of a data entry.
 *
 * Created by jcairns on 4/30/15.
 */
public interface HyperRect<D extends Comparable<D>> {
    HyperRect getMbr(HyperRect r);

    int getNDim();

    HyperPoint getMin();
    HyperPoint getMax();

    HyperPoint getCentroid();

    double getRange(final int d);

    boolean contains(HyperRect r);

    boolean intersects(HyperRect r);

    double cost(); // aka area

    static <D extends Comparable<D>> double perimeter(final HyperRect<D> rect) {
        double p = 0.0;
        final int nD = rect.getNDim();
        for(int d = 0; d<nD; d++) {
            p += 2.0*rect.getRange(d);
        }
        return p;
    }
}
