package com.conversant.util.collection.spatial;

import java.util.function.Consumer;

/**
 * Created by jcairns on 4/30/15.
 */
public final class RTree<T> {
    private static final double EPSILON = 1e-12;

    private final int mMin;
    private final int mMax;
    private final RectBuilder<T> builder;
    private Node<T> root;
    private SPLIT_TYPE splitType;
    private int entryCount;

    public RTree(final RectBuilder<T> builder) {
        this(builder, 2, 8, SPLIT_TYPE.QUADRATIC);
    }

    public RTree(final RectBuilder<T> builder, final int mMin, final int mMax, final SPLIT_TYPE splitType) {
        this.mMin = mMin;
        this.mMax = mMax;
        this.builder = builder;
        this.splitType = splitType;
        this.entryCount = 0;
        root = Leaf.create(builder, mMin, mMax, splitType);
    }

    public int search(final HyperRect rect, final T[] t) {
        return root.search(rect, t, 0);
    }

    public boolean contains(final HyperRect rect, final T[] t) {
        for (int i = 0; i < t.length; i++) {
            if (!rect.contains(builder.getBBox(t[i]))) {
                return false;
            }
        }
        return true;
    }

    public void add(final T t) {
        root = root.add(t);
        entryCount++;
    }

    public void remove(final T t) {
        Node<T> removed = root.remove(t);
        if(removed != null)
            entryCount--;
    }

    public void update(final T told, final T tnew) { root.update(told, tnew); }

    public int getEntryCount() { return entryCount; }

    final static boolean isEqual(final double a, final double b) {
        return isEqual(a, b, EPSILON);
    }

    final static boolean isEqual(final double a, final double b, final double eps) {
        return Math.abs(a - b) <= ( (Math.abs(a) < Math.abs(b) ? Math.abs(b) : Math.abs(a))*eps);
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

    public enum SPLIT_TYPE {
        AXIAL,
        LINEAR,
        QUADRATIC,
    }
}
