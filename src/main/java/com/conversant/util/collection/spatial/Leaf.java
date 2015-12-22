package com.conversant.util.collection.spatial;

import java.util.function.Consumer;

/**
 * Created by jcairns on 4/30/15.
 */
abstract class Leaf<T> implements Node<T> {
    protected final int mMax;       // max entries per node
    protected final int mMin;       // least number of entries per node
    protected HyperRect mbr;
    protected final HyperRect[] r;
    protected final T[]    entry;
    protected final RectBuilder<T> builder;
    protected int size;
    protected RTree.SPLIT_TYPE splitType;

    protected Leaf(final RectBuilder<T> builder, final int mMin, final int mMax, final RTree.SPLIT_TYPE splitType) {
        this.mMin = mMin;
        this.mMax = mMax;
        this.mbr = null;
        this.builder = builder;
        this.r  = new HyperRect[mMax];
        this.entry = (T[]) new Object[mMax];
        this.size = 0;
        this.splitType = splitType;
    }

    @Override
    public Node<T> add(final T t) {
        if(size < mMax) {
            final HyperRect tRect = builder.getBBox(t);
            if(mbr != null) {
                mbr = mbr.getMbr(tRect);
            } else {
                mbr = tRect;
            }

            r[size] = tRect;
            entry[size++] = t;
        } else {
            for(int i = 0; i < size; i++){
                if(entry[i] == null){
                    entry[i] = t;
                    r[i] = builder.getBBox(t);
                    mbr = mbr.getMbr(r[i]);
                    return this;
                }
            }
            return split(t);
        }

        return this;
    }

    @Override
    public Node<T> remove(final T t) {
        for(int i = 0; i < size; i++){
            if(entry[i].equals(t)){
                entry[i] = null;
                r[i] = null;
                if(i < (size-1)){
                    entry[i] = entry[size-1];
                    r[i] = r[size-1];
                    entry[size-1] = null;
                    r[size-1] = null;
                }
                size--;
                if(size > 0) {
                    mbr = r[0];
                    for (i = 1; i < size; i++) {
                        mbr = mbr.getMbr(r[i]);
                    }
                }
                return this;
            }
        }
        return null;
    }

    @Override
    public Node<T> update(final T told, final T tnew) {
        for(int i = 0 ; i < this.size; i++){
            if(entry[i].equals(told)){
                entry[i] = tnew;
                r[i] = builder.getBBox(tnew);
            }
        }
        return this;
    }

    @Override
    public int search(final HyperRect rect, final T[] t, int n) {
        final int tLen = t.length;
        final int n0 = n;

        for(int i=0; i<size && n<tLen; i++) {
            if(rect.intersects(r[i])) {
                t[n++] = entry[i];
            }
        }
        return n - n0;
    }

    @Override
    public int size() {
        return size;
    }

    public T getEntry(final int dx) {
        return entry[dx];
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public HyperRect getRect() {
        return mbr;
    }

    static <R> Node<R> create(final RectBuilder<R> builder, final int mMin, final int M, final RTree.SPLIT_TYPE splitType) {

        switch(splitType) {
            case AXIAL:
                return new AxialSplitLeaf<>(builder, mMin, M);
            case LINEAR:
                return new LinearSplitLeaf<>(builder, mMin, M);
            case QUADRATIC:
                return new QuadraticSplitLeaf<>(builder, mMin, M);
            default:
                return new AxialSplitLeaf<>(builder, mMin, M);

        }
    }

    protected abstract Node<T> split(final T t);

    @Override
    public void forEach(Consumer<T> consumer) {
        for(int i = 0; i < size; i++) {
            consumer.accept(entry[i]);
        }
    }

    @Override
    public void forEach(Consumer<T> consumer, HyperRect rect) {
        for(int i = 0; i < size; i++) {
            if(rect.intersects(r[i])) {
                consumer.accept(entry[i]);
            }
        }
    }

    @Override
    public void collectStats(Stats stats, int depth) {
        if (depth > stats.getMaxDepth()) {
            stats.setMaxDepth(depth);
        }
        stats.countLeafAtDepth(depth);
        stats.countEntriesAtDepth(size, depth);
    }

    protected final void classify(final Node<T> l1Node, final Node<T> l2Node, final T t) {
        final HyperRect tRect = builder.getBBox(t);
        final HyperRect l1Mbr = l1Node.getRect().getMbr(tRect);
        final HyperRect l2Mbr = l2Node.getRect().getMbr(tRect);
        final double l1CostInc = Math.max(l1Mbr.cost() - (l1Node.getRect().cost() + tRect.cost()), 0.0);
        final double l2CostInc = Math.max(l2Mbr.cost() - (l2Node.getRect().cost() + tRect.cost()), 0.0);
        if(l2CostInc > l1CostInc) {
            l1Node.add(t);
        }
        else if(RTree.isEqual(l1CostInc, l2CostInc)) {
            final double l1MbrCost = l1Mbr.cost();
            final double l2MbrCost = l2Mbr.cost();
            if(l1MbrCost < l2MbrCost) {
                l1Node.add(t);
            } else if(RTree.isEqual(l1MbrCost, l2MbrCost)) {
                final double l1MbrMargin = HyperRect.perimeter(l1Mbr);
                final double l2MbrMargin = HyperRect.perimeter(l2Mbr);
                if(l1MbrMargin < l2MbrMargin) {
                    l1Node.add(t);
                } else if(RTree.isEqual(l1MbrMargin, l2MbrMargin)) {
                    // break ties with least number
                    if (l1Node.size() < l2Node.size()) {
                        l1Node.add(t);
                    } else {
                        l2Node.add(t);
                    }
                } else {
                    l2Node.add(t);
                }
            } else {
                l2Node.add(t);
            }
        }
        else {
            l2Node.add(t);
        }

    }

    @Override
    public Node<T> instrument() {
        return new CounterNode<>(this);
    }
}
