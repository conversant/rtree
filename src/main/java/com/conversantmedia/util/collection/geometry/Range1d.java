package com.conversantmedia.util.collection.geometry;

/*
 * #%L
 * Conversant RTree
 * ~~
 * Conversantmedia.com © 2018, Conversant, Inc. Conversant® is a trademark of Conversant, Inc.
 * John Cairns <john@2ad.com> © 2018
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

import com.conversantmedia.util.collection.spatial.HyperPoint;
import com.conversantmedia.util.collection.spatial.HyperRect;
import com.conversantmedia.util.collection.spatial.RectBuilder;


// 1D rectangle for range searching
public final class Range1d implements HyperRect {
    final Point1d min, max;

    public Range1d(final Point1d p1, final Point1d p2) {
        if(p1.x < p2.x) {
            this.min = new Point1d(p1.x);
            this.max = new Point1d(p2.x);
        } else {
            this.min = new Point1d(p2.x);
            this.max = new Point1d(p1.x);
        }
    }


    public Range1d(final double x1, final double x2) {
        if(x1 < x2) {
            min = new Point1d(x1);
            max = new Point1d(x2);
        } else {
            min = new Point1d(x2);
            max = new Point1d(x1);
        }
    }

    @Override
    public HyperRect getMbr(final HyperRect r) {
        final Range1d r2 = (Range1d)r;
        final double minT = Math.min(min.x, r2.min.x);
        final double maxT = Math.max(max.x, r2.max.x);

        return new Range1d(minT, maxT);

    }

    @Override
    public int getNDim() {
        return 1;
    }

    @Override
    public HyperPoint getCentroid() {
        final double dx = min.x + (max.x - min.x)/2.0;

        return new Point1d(dx);
    }

    @Override
    public HyperPoint getMin() {
        return min;
    }

    @Override
    public HyperPoint getMax() {
        return max;
    }

    @Override
    public double getRange(final int d) {
        if(d == 0) {
            return max.x - min.x;
        } else {
            throw new IllegalArgumentException("Invalid dimension");
        }
    }

    @Override
    public boolean contains(final HyperRect r) {
        final Range1d r2 = (Range1d)r;

        return min.x <= r2.min.x &&
                max.x >= r2.max.x;
    }

    @Override
    public boolean intersects(final HyperRect r) {
        final Range1d r2 = (Range1d)r;

        if(min.x > r2.max.x ||
                r2.min.x > max.x) {
            return false;
        }

        return true;
    }

    @Override
    public double cost() {
        final double dx = max.x - min.x;
        return Math.abs(dx);
    }

    @Override
    public double perimeter() {
        double p = 0.0;
        final int nD = this.getNDim();
        for(int d = 0; d<nD; d++) {
            p += this.getRange(d);
        }
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Range1d rect2D = (Range1d) o;

        return min.equals(rect2D.min) &&
                max.equals(rect2D.max);
    }

    @Override
    public int hashCode() {
        return min.hashCode() ^ 31*max.hashCode();
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(Double.toString(min.x));
        sb.append(')');
        sb.append(' ');
        sb.append('(');
        sb.append(Double.toString(max.x));
        sb.append(')');

        return sb.toString();
    }

    public final static class Builder implements RectBuilder<Range1d> {

        @Override
        public HyperRect getBBox(final Range1d rect2D) {
            return rect2D;
        }

        @Override
        public HyperRect getMbr(final HyperPoint p1, final HyperPoint p2) {
            return new Range1d(p1.getCoord(0), p2.getCoord(0));
        }
    }
}