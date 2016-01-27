package com.conversant.util.collection.spatial;

/**
 * An N dimensional rectangle or "hypercube" that is a representation of a data entry.
 *
 * Created by jcairns on 4/30/15.
 */
public interface HyperRect<D extends Comparable<D>> {

    /**
     * Calculate the resulting mbr when combining param HyperRect with this HyperRect
     *
     * @param r - mbr to add
     *
     * @return new HyperRect representing mbr of both HyperRects combined
     */
    HyperRect getMbr(HyperRect r);

    /**
     * Get number of dimensions used in creating the HyperRect
     *
     * @return
     */
    int getNDim();

    /**
     * Get the minimum HyperPoint of this HyperRect
     *
     * @return  min HyperPoint
     */
    HyperPoint getMin();

    /**
     * Get the minimum HyperPoint of this HyperRect
     *
     * @return  min HyperPoint
     */
    HyperPoint getMax();

    /**
     * Get the HyperPoint representing the center point in all dimensions of this HyperRect
     *
     * @return  middle HyperPoint
     */
    HyperPoint getCentroid();

    /**
     * Calculate the distance between the min and max HyperPoints in given dimension
     *
     * @param d - dimension to calculate
     *
     * @return
     */
    double getRange(final int d);

    /**
     * Determines if this HyperRect fully contains parameter HyperRect
     *
     * @param r - HyperRect to test
     *
     * @return true if contains, false otherwise
     */
    boolean contains(HyperRect r);

    /**
     * Determines if this HyperRect intersects parameter HyperRect
     *
     * @param r - HyperRect to test
     *
     * @return true if intersects, false otherwise
     */
    boolean intersects(HyperRect r);

    /**
     * Calculate the "cost" of this HyperRect - usually the area across all dimensions
     *
     * @return - cost
     */
    double cost();

    /**
     * Calculate the perimeter of this HyperRect - across all dimesnions
     *
     * @return - perimeter
     */
    double perimeter();
}
