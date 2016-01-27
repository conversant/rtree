package com.conversantmedia.util.collection.spatial;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Created by jcovert on 12/30/15.
 */
public class LockingRTree<T> implements SpatialSearch<T> {

    private final SpatialSearch<T> rTree;
    private final Lock readLock;
    private final Lock writeLock;

    /**
     * Create a new protected R-Tree with default values for m, M, and split type
     *
     * @param builder
     */
    public LockingRTree(RectBuilder<T> builder) {
        this(new RTree<>(builder), new ReentrantReadWriteLock(true));
    }

    /**
     * Create a new protected R-Tree with default values for m, M, and split type
     *
     * @param builder
     */
    public LockingRTree(RectBuilder<T> builder, int minM, int maxM, RTree.Split splitType) {
        this(new RTree<>(builder, minM, maxM, splitType), new ReentrantReadWriteLock(true));
    }

    /**
     * Protect parameter R-Tree with parameter Lock.
     * Mainly available for tests, but also for other constructors.
     *
     * @param rTree - R-Tree to protect
     * @param lock - Lock used to protect the R-Tree
     */
    LockingRTree(SpatialSearch<T> rTree, ReadWriteLock lock) {
        this.rTree = rTree;
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public int search(HyperRect rect, T[] t) {
        readLock.lock();
        try {
            return rTree.search(rect, t);
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public void add(T t) {
        writeLock.lock();
        try {
            rTree.add(t);
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public void remove(T t) {
        writeLock.lock();
        try {
            rTree.remove(t);
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public void update(T told, T tnew) {
        writeLock.lock();
        try {
            rTree.update(told, tnew);
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Non-blocking locked search
     *
     * @param rect - HyperRect to search
     * @param t - array to hold results
     *
     * @return number of entries found or -1 if lock was not acquired
     */
    public int trySearch(HyperRect rect, T[] t) {
        if(readLock.tryLock()) {
            try {
                return rTree.search(rect, t);
            } finally {
                readLock.unlock();
            }
        }
        return -1;
    }

    /**
     * Non-blocking locked add
     *
     * @param t - entry to add
     *
     * @return true if lock was acquired, false otherwise
     */
    public boolean tryAdd(T t) {
        if(writeLock.tryLock()) {
            try {
                rTree.add(t);
            } finally {
                writeLock.unlock();
            }
            return true;
        }
        return false;
    }

    /**
     * Non-blocking locked remove
     *
     * @param t - entry to remove
     *
     * @return true if lock was acquired, false otherwise
     */
    public boolean tryRemove(T t) {
        if(writeLock.tryLock()) {
            try {
                rTree.remove(t);
            } finally {
                writeLock.unlock();
            }
            return true;
        }
        return false;
    }

    /**
     * Non-blocking locked update
     *
     * @param told - entry to update
     * @param tnew - entry with new values
     *
     * @return true if lock was acquired, false otherwise
     */
    public boolean tryUpdate(T told, T tnew) {
        if(writeLock.tryLock()) {
            try {
                rTree.update(told, tnew);
            } finally {
                writeLock.unlock();
            }
            return true;
        }
        return false;
    }
}
