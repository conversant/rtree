package com.conversant.util.collection.spatial;

import java.util.function.Consumer;

/**
 * RTree node that contains leaf nodes
 *
 * Created by jcairns on 4/30/15.
 */
final class Branch<T> implements Node<T> {
    private HyperRect mbr;
    private final Node[] child;
    private int size;
    private final RectBuilder<T> builder;
    private final int mMax;
    private final int mMin;
    private final RTree.Split splitType;

    Branch(final RectBuilder<T> builder, final int mMin, final int mMax, final RTree.Split splitType) {
        this.mMin = mMin;
        this.mMax = mMax;
        this.builder = builder;
        this.mbr = null;
        this.size = 0;
        this.child = new Node[mMax];
        this.splitType = splitType;
    }

    /**
     * Add a new node to this branch's list of children
     *
     * @param n node to be added (can be leaf or branch)
     * @return position of the added node
     */
    protected int addChild(final Node<T> n) {
        if(size < mMax) {
            child[size++] = n;

            if(mbr != null) {
                mbr = mbr.getMbr(n.getRect());
            }
            else {
                mbr = n.getRect();
            }
            return size - 1;
        }
        else {
            throw new RuntimeException("Too many children");
        }
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public HyperRect getRect() {
        return mbr;
    }

    /**
     * Adds a data entry to one of the child nodes of this branch
     *
     * @param t data entry to add
     * @return Node that the entry was added to
     */
    @Override
    public Node<T> add(final T t) {
        final HyperRect tRect = builder.getBBox(t);
        if(size < mMin) {
            for(int i=0; i<size; i++) {
                if(child[i].getRect().contains(tRect)) {
                    child[i] = child[i].add(t);
                    mbr = mbr.getMbr(child[i].getRect());
                    return child[i];
                }
            }
            // no overlapping node - grow
            final Node<T> nextLeaf = Leaf.create(builder, mMin, mMax, splitType);
            nextLeaf.add(t);
            final int nextChild = addChild(nextLeaf);
            mbr = mbr.getMbr(child[nextChild].getRect());

            return this;

        } else {
            final int bestLeaf = chooseLeaf(t, tRect);

            child[bestLeaf] = child[bestLeaf].add(t);

            mbr = mbr.getMbr(child[bestLeaf].getRect());

            // optimize on split to remove the extra created branch when there
            // is space for the children here
            if(child[bestLeaf].size() == 2 &&
                    size < mMax &&
                    child[bestLeaf] instanceof Branch) {
                final Branch<T> branch = (Branch<T>)child[bestLeaf];
                child[bestLeaf] = branch.child[0];
                child[size++] = branch.child[1];
            }

            return this;
        }
    }

    @Override
    public Node<T> remove(final T t) {
        final HyperRect tRect = builder.getBBox(t);
        Node<T> returned = null;
        for (int i = 0; i < size; i++) {
            if (child[i].getRect().contains(tRect)) {
                returned =  child[i].remove(t);

                // Replace a Branch Node with 1 child with it's child
                // Will not work for a RTree with mMin > 2
                if(returned != null) {
                    if (returned.size() == 0) {
                        child[i] = null;
                        if (i < (size - 1)) {
                            child[i] = child[size - 1];
                            child[size - 1] = null;
                        }
                        --size;
                    }
                    if (size == 1) {
                        return child[0];
                    }
                    if (child[i] != null) {
                        if (child[i].size() == 1 && returned.isLeaf()) {
                            child[i] = returned;
                        }
                    }
                }
            }
        }
        return returned;
    }

    @Override
    public Node<T> update(final T told, final T tnew) {
        final HyperRect tRect = builder.getBBox(told);
        for(int i = 0; i < size; i++){
            if(child[i].getRect().contains(tRect)){
                child[i] = child[i].update(told, tnew);
                mbr = mbr.getMbr(child[i].getRect());
                return child[i];
            }
        }
        return this;
    }

    @Override
    public int search(final HyperRect rect, final T[] t, int n) {
        final int tLen = t.length;
        final int n0 = n;
        for(int i=0; i < size && n < tLen; i++) {
            if (rect.intersects(child[i].getRect())) {
                n += child[i].search(rect, t, n);
            }
        }
        return n-n0;
    }

    /**
     * @return number of child nodes
     */
    @Override
    public int size() {
        return size;
    }

    private int chooseLeaf(final T t, final HyperRect tRect) {
        if(size > 0) {
            int bestNode = 0;
            HyperRect childMbr = child[0].getRect().getMbr(tRect);
            double leastEnlargement = childMbr.cost() - (child[0].getRect().cost() + tRect.cost());
            double leastPerimeter   = childMbr.perimeter();

            for(int i = 1; i<size; i++) {
                childMbr = child[i].getRect().getMbr(tRect);
                final double nodeEnlargement = childMbr.cost() - (child[i].getRect().cost() + tRect.cost());
                if (nodeEnlargement < leastEnlargement) {
                    leastEnlargement = nodeEnlargement;
                    leastPerimeter  = childMbr.perimeter();
                    bestNode = i;
                }
                else if(RTree.isEqual(nodeEnlargement, leastEnlargement)) {
                    final double childPerimeter = childMbr.perimeter();
                    if (childPerimeter < leastPerimeter) {
                        leastEnlargement = nodeEnlargement;
                        leastPerimeter = childPerimeter;
                        bestNode = i;
                    }
                } // else its not the least

            }
            return bestNode;
        }
        else {
            final Node<T> n = Leaf.create(builder, mMin, mMax, splitType);
            n.add(t);
            child[size++] = n;

            if(mbr == null) {
                mbr = n.getRect();
            }
            else {
                mbr = mbr.getMbr(n.getRect());
            }

            return size-1;
        }
    }

    /**
     * Return child nodes of this branch.
     *
     * @return array of child nodes (leaves or branches)
     */
    public Node[] getChildren() {
        return child;
    }

    @Override
    public void forEach(Consumer<T> consumer) {
        for(int i = 0; i < size; i++) {
            child[i].forEach(consumer);
        }
    }

    @Override
    public void forEach(Consumer<T> consumer, HyperRect rect) {
        for(int i = 0; i < size; i++) {
            if(rect.intersects(child[i].getRect())) {
                child[i].forEach(consumer, rect);
            }
        }
    }

    @Override
    public void collectStats(Stats stats, int depth) {
        for(int i = 0; i < size; i++) {
            child[i].collectStats(stats, depth + 1);
        }
        stats.countBranchAtDepth(depth);
    }

    @Override
    public Node<T> instrument() {
        for(int i = 0; i < size; i++) {
            child[i] = child[i].instrument();
        }
        return new CounterNode<>(this);
    }
}
