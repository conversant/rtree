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

import java.util.function.Consumer;

/**
 * <p>Data structure to make range searching more efficient. Indexes multi-dimensional information
 * such as geographical coordinates or rectangles. Groups information and represents them with a
 * minimum bounding rectangle (mbr). When searching through the tree, any query that does not
 * intersect an mbr can ignore any data entries in that mbr.</p>
 * <p>More information can be @see <a href="https://en.wikipedia.org/wiki/R-tree">https://en.wikipedia.org/wiki/R-tree</a></p>
 * <p>
 * Created by jcairns on 4/30/15.</p>
 */
public final class RTree<T> implements SpatialSearch<T> {
    private static final double EPSILON = 1e-12;

    private final int mMin;
    private final int mMax;
    private final RectBuilder<T> builder;
    private Node<T> root;
    private Split splitType;
    private int entryCount;

    protected RTree(final RectBuilder<T> builder, final int mMin, final int mMax, final Split splitType) {
        this.mMin = mMin;
        this.mMax = mMax;
        this.builder = builder;
        this.splitType = splitType;
        this.entryCount = 0;
        root = Leaf.create(builder, mMin, mMax, splitType);
    }

    @Override
    public int search(final HyperRect rect, final T[] t) {
        return root.search(rect, t, 0);

    }

    @Override
    public void add(final T t) {
        root = root.add(t);
        entryCount++;
    }

    @Override
    public void remove(final T t) {
        Node<T> removed = root.remove(t);
        if (removed != null) {
            entryCount--;
        }
    }

    @Override
    public void update(final T told, final T tnew) {
        root.update(told, tnew);
    }

    /**
     * returns whether or not the HyperRect will enclose all of the data entries in t
     *
     * @param rect HyperRect to contain entries
     * @param t    Data entries to be evaluated
     * @return Whether or not all entries lie inside rect
     */
    public boolean contains(final HyperRect rect, final T[] t) {
        for (int i = 0; i < t.length; i++) {
            if (!rect.contains(builder.getBBox(t[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return number of data entries stored in the RTree
     */
    public int getEntryCount() {
        return entryCount;
    }

    final static boolean isEqual(final double a, final double b) {
        return isEqual(a, b, EPSILON);
    }

    final static boolean isEqual(final double a, final double b, final double eps) {
        return Math.abs(a - b) <= ((Math.abs(a) < Math.abs(b) ? Math.abs(b) : Math.abs(a)) * eps);
    }

    public void forEach(Consumer<T> consumer) {
        root.forEach(consumer);
    }

    public void forEach(Consumer<T> consumer, HyperRect rect) {
        root.forEach(consumer, rect);
    }

    void instrumentTree() {
        root = root.instrument();
        ((CounterNode<T>) root).searchCount = 0;
        ((CounterNode<T>) root).bboxEvalCount = 0;
    }

    public Stats collectStats() {
        Stats stats = new Stats();
        stats.setType(splitType);
        stats.setMaxFill(mMax);
        stats.setMinFill(mMin);
        root.collectStats(stats, 0);
        return stats;
    }

    public Node<T> getRoot() {
        return this.root;
    }


    /**
     * Different methods for splitting nodes in an RTree.
     * AXIAL has been shown to give the best performance and should be used
     * in the AdServer
     * <p>
     * Created by ewhite on 10/28/15.
     */
    public enum Split {
        AXIAL,
        LINEAR,
        QUADRATIC,
    }
}
