package com.conversantmedia.util.collection.spatial;

import java.util.function.Consumer;

/**
 * Created by jcovert on 6/18/15.
 */
final class CounterNode<T> implements Node<T> {
    final Node<T> node;

    static int searchCount = 0;
    static int bboxEvalCount = 0;

    CounterNode(final Node<T> node) {
        this.node = node;
    }

    @Override
    public boolean isLeaf() {
        return this.node.isLeaf();
    }

    @Override
    public HyperRect getRect() {
        return this.node.getRect();
    }

    @Override
    public Node<T> add(T t) {
        return this.node.add(t);
    }

    @Override
    public Node<T> remove(T t) { return this.node.remove(t); }

    @Override
    public Node<T> update(T told, T tnew) { return this.node.update(told, tnew); }

    @Override
    public int search(HyperRect rect, T[] t, int n) {
        searchCount++;
        bboxEvalCount += this.node.size();
        return this.node.search(rect, t, n);
    }

    @Override
    public int size() {
        return this.node.size();
    }

    @Override
    public void forEach(Consumer<T> consumer) {
        this.node.forEach(consumer);
    }

    @Override
    public void forEach(Consumer<T> consumer, HyperRect rect) {
        this.node.forEach(consumer, rect);
    }

    @Override
    public void collectStats(Stats stats, int depth) {
        this.node.collectStats(stats, depth);
    }

    @Override
    public Node<T> instrument() {
        return this;
    }
}
