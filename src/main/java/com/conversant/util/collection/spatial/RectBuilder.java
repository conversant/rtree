package com.conversant.util.collection.spatial;

/**
 * Created by jcairns on 4/30/15.
 */
public interface RectBuilder<T> {
    HyperRect getBBox(T t);

    HyperRect getMbr(HyperPoint p1, HyperPoint p2);
}
