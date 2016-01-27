package com.conversantmedia.util.collection.spatial;

/*
 * #%L
 * Conversant RTree
 * ~~
 * Conversantmedia.com © 2016, Conversant, Inc. Conversant® is a trademark of Conversant, Inc.
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

/**
 * Created by jcovert on 6/15/15.
 */
public class Rect2D implements HyperRect {
    final Point min, max;

    Rect2D(final Point p) {
        min = new Point(p.x, p.y);
        max = new Point(p.x, p.y);
    }

    Rect2D(final double x1, final double y1, final double x2, final double y2) {
        min = new Point(x1, y1);
        max = new Point(x2, y2);
    }

    Rect2D(final Point p1, final Point p2) {
        final double minX, minY, maxX, maxY;

        if(p1.x < p2.x) {
            minX = p1.x;
            maxX = p2.x;
        } else {
            minX = p2.x;
            maxX = p2.x;
        }

        if(p1.y < p2.y) {
            minY = p1.y;
            maxY = p2.y;
        } else {
            minY = p2.y;
            maxY = p2.y;
        }

        min = new Point(minX, minY);
        max = new Point(maxX, maxY);
    }

    @Override
    public HyperRect getMbr(final HyperRect r) {
        final Rect2D r2 = (Rect2D)r;
        final double minX = Math.min(min.x, r2.min.x);
        final double minY = Math.min(min.y, r2.min.y);
        final double maxX = Math.max(max.x, r2.max.x);
        final double maxY = Math.max(max.y, r2.max.y);

        return new Rect2D(minX, minY, maxX, maxY);

    }

    @Override
    public int getNDim() {
        return 2;
    }

    @Override
    public HyperPoint getCentroid() {
        final double dx = min.x + (max.x - min.x)/2.0;
        final double dy = min.y + (max.y - min.y)/2.0;

        return new Point(dx, dy);
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
        } else if(d == 1) {
            return max.y - min.y;
        } else {
            throw new IllegalArgumentException("Invalid dimension");
        }
    }

    @Override
    public boolean contains(final HyperRect r) {
        final Rect2D r2 = (Rect2D)r;

        return min.x <= r2.min.x &&
                max.x >= r2.max.x &&
                min.y <= r2.min.y &&
                max.y >= r2.max.y;
    }

    @Override
    public boolean intersects(final HyperRect r) {
        final Rect2D r2 = (Rect2D)r;

        if(min.x > r2.max.x ||
                r2.min.x > max.x ||
                min.y > r2.max.y ||
                r2.min.y > max.y) {
            return false;
        }

        return true;
    }

    @Override
    public double cost() {
        final double dx = max.x - min.x;
        final double dy = max.y - min.y;
        return Math.abs(dx)*Math.abs(dy);
    }

    @Override
    public double perimeter() {
        double p = 0.0;
        final int nD = this.getNDim();
        for(int d = 0; d<nD; d++) {
            p += 2.0 * this.getRange(d);
        }
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rect2D rect2D = (Rect2D) o;

        return min.x == rect2D.min.x &&
               max.x == rect2D.max.x &&
               min.y == rect2D.min.y &&
               max.y == rect2D.max.y;
    }

    @Override
    public int hashCode() {
        int result = min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(Double.toString(min.x));
        sb.append(',');
        sb.append(Double.toString(min.y));
        sb.append(')');
        sb.append(' ');
        sb.append('(');
        sb.append(Double.toString(max.x));
        sb.append(',');
        sb.append(Double.toString(max.y));
        sb.append(')');

        return sb.toString();
    }

    public final static class Builder implements RectBuilder<Rect2D> {

        @Override
        public HyperRect getBBox(final Rect2D rect2D) {
            return rect2D;
        }

        @Override
        public HyperRect getMbr(final HyperPoint p1, final HyperPoint p2) {
            return new Rect2D(p1.getCoord(0), p1.getCoord(1), p2.getCoord(0), p2.getCoord(1));
        }
    }
}