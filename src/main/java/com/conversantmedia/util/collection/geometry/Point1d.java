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
import com.conversantmedia.util.collection.spatial.RTree;
import com.conversantmedia.util.collection.spatial.RectBuilder;

public final class Point1d implements HyperPoint {

    final double x;

    public Point1d(final double x) {
        this.x = x;
    }

    @Override
    public int getNDim() {
        return 1;
    }

    @Override
    public Double getCoord(final int d) {
        if(d==0) {
            return x;
        } else {
            throw new IllegalArgumentException("Invalid dimension");
        }
    }

    @Override
    public double distance(final HyperPoint p) {
        final Point1d p2 = (Point1d)p;

        final double dx = p2.x - x;
        return dx;
    }

    @Override
    public double distance(final HyperPoint p, final int d) {
        final Point1d p2 = (Point1d)p;
        if(d == 0) {
            return distance(p);
        } else {
            throw new IllegalArgumentException("Invalid dimension");
        }
    }

    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o==null || getClass() != o.getClass()) return false;

        final Point1d p = (Point1d) o;
        return RTree.isEqual(x, p.x);
    }


    public int hashCode() {
        return Double.hashCode(x);
    }

    public final static class Builder implements RectBuilder<Point1d> {

        @Override
        public HyperRect getBBox(final Point1d point) {
            return new Range1d(point, point);
        }

        @Override
        public HyperRect getMbr(final HyperPoint p1, final HyperPoint p2) {
            final Point1d point1 = (Point1d)p1;
            final Point1d point2 = (Point1d)p2;
            return new Range1d(point1, point2);
        }
    }
}