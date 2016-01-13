package com.dotomi.util.collection.spatial;

import org.junit.Assert;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import static org.mockito.Mockito.*;

/**
 * Created by jcovert on 12/30/15.
 */
public class LockingRTreeTest {

    private static final Rect2D RECT_2_D_0 = new Rect2D(0, 0, 0, 0);
    private static final Rect2D RECT_2_D_1 = new Rect2D(1, 1, 1, 1);

    @Test
    public void testSearchLocking() {

        MockLock lock = new MockLock();
        MockSearch search = new MockSearch(lock);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        // asserts proper locking
        tree.search(RECT_2_D_0, new Rect2D[0]);
    }

    @Test
    public void testAddLocking() {

        MockLock lock = new MockLock();
        MockSearch search = new MockSearch(lock);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        // asserts proper locking
        tree.add(RECT_2_D_0);
    }

    @Test
    public void testRemoveLocking() {

        MockLock lock = new MockLock();
        MockSearch search = new MockSearch(lock);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        // asserts proper locking
        tree.remove(RECT_2_D_0);
    }

    @Test
    public void testUpdateLocking() {

        MockLock lock = new MockLock();
        MockSearch search = new MockSearch(lock);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        // asserts proper locking
        tree.update(RECT_2_D_0, RECT_2_D_1);
    }

    @Test
    public void testSearchLockingCount() {

        Lock readLock = mock(Lock.class);
        Lock writeLock = mock(Lock.class);
        ReadWriteLock lock = mock(ReadWriteLock.class);
        when(lock.readLock()).thenReturn(readLock);
        when(lock.writeLock()).thenReturn(writeLock);

        SpatialSearch<Rect2D> search = RTreeTest.createRect2DTree(RTree.Split.AXIAL, 2, 8);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        tree.search(RECT_2_D_0, new Rect2D[0]);

        verify(readLock, times(1)).lock();
        verify(readLock, times(1)).unlock();
        verify(writeLock, never()).lock();
        verify(writeLock, never()).unlock();
    }

    @Test
    public void testAddLockingCount() {

        Lock readLock = mock(Lock.class);
        Lock writeLock = mock(Lock.class);
        ReadWriteLock lock = mock(ReadWriteLock.class);
        when(lock.readLock()).thenReturn(readLock);
        when(lock.writeLock()).thenReturn(writeLock);

        SpatialSearch<Rect2D> search = RTreeTest.createRect2DTree(RTree.Split.AXIAL, 2, 8);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        tree.add(RECT_2_D_0);

        verify(readLock, never()).lock();
        verify(readLock, never()).unlock();
        verify(writeLock, times(1)).lock();
        verify(writeLock, times(1)).unlock();
    }

    @Test
    public void testRemoveLockingCount() {

        Lock readLock = mock(Lock.class);
        Lock writeLock = mock(Lock.class);
        ReadWriteLock lock = mock(ReadWriteLock.class);
        when(lock.readLock()).thenReturn(readLock);
        when(lock.writeLock()).thenReturn(writeLock);

        SpatialSearch<Rect2D> search = RTreeTest.createRect2DTree(RTree.Split.AXIAL, 2, 8);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        tree.remove(RECT_2_D_0);

        verify(readLock, never()).lock();
        verify(readLock, never()).unlock();
        verify(writeLock, times(1)).lock();
        verify(writeLock, times(1)).unlock();
    }

    @Test
    public void testUpdateLockingCount() {

        Lock readLock = mock(Lock.class);
        Lock writeLock = mock(Lock.class);
        ReadWriteLock lock = mock(ReadWriteLock.class);
        when(lock.readLock()).thenReturn(readLock);
        when(lock.writeLock()).thenReturn(writeLock);

        SpatialSearch<Rect2D> search = RTreeTest.createRect2DTree(RTree.Split.AXIAL, 2, 8);
        LockingRTree<Rect2D> tree = new LockingRTree<>(search, lock);

        tree.update(RECT_2_D_0, RECT_2_D_1);

        verify(readLock, never()).lock();
        verify(readLock, never()).unlock();
        verify(writeLock, times(1)).lock();
        verify(writeLock, times(1)).unlock();
    }

    private static class MockLock implements ReadWriteLock {
        boolean isLocked = false;
        int readers = 0;

        @Override
        public Lock readLock() {
            return new Lock() {

                @Override
                public void lock() {
                    Assert.assertFalse("Attempting to acquire read lock while write locked", isLocked);
                    readers++;
                }

                @Override
                public void lockInterruptibly() throws InterruptedException {
                    Assert.assertFalse("Attempting to acquire read lock while write locked", isLocked);
                    readers++;
                }

                @Override
                public boolean tryLock() {
                    Assert.assertFalse("Attempting to acquire read lock while write locked", isLocked);
                    readers++;
                    return true;
                }

                @Override
                public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
                    Assert.assertFalse("Attempting to acquire read lock while write locked", isLocked);
                    readers++;
                    return true;
                }

                @Override
                public void unlock() {
                    Assert.assertNotEquals("Attempting to unlock read lock without any readers", readers, 0);
                    readers--;
                }

                @Override
                public Condition newCondition() {
                    throw new NotImplementedException();
                }
            };
        }

        @Override
        public Lock writeLock() {
            return new Lock() {

                @Override
                public void lock() {
                    isLocked = true;
                    Assert.assertEquals("Attempting to acquire write lock while readers are reading", readers, 0);
                }

                @Override
                public void lockInterruptibly() throws InterruptedException {
                    isLocked = true;
                    Assert.assertEquals("Attempting to acquire write lock while readers are reading", readers, 0);
                }

                @Override
                public boolean tryLock() {
                    isLocked = true;
                    Assert.assertEquals("Attempting to acquire write lock while readers are reading", readers, 0);
                    return true;
                }

                @Override
                public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
                    isLocked = true;
                    Assert.assertEquals("Attempting to acquire write lock while readers are reading", readers, 0);
                    return true;
                }

                @Override
                public void unlock() {
                    Assert.assertTrue("Attempting to unlock write lock without any writers", isLocked);
                    isLocked = false;
                    Assert.assertEquals("Attempting to unlock write lock while readers are reading", readers, 0);
                }

                @Override
                public Condition newCondition() {
                    throw new NotImplementedException();
                }
            };
        }
    }

    private static final class MockSearch implements SpatialSearch {
        MockLock lock;

        public MockSearch(MockLock lock) {
            this.lock = lock;
        }

        @Override
        public int search(HyperRect rect, Object[] t) {
            Assert.assertNotEquals("Read lock should have reader while search in progress", lock.readers, 0);
            Assert.assertFalse("Attempting to read while writers are writing", lock.isLocked);
            return 0;
        }

        @Override
        public void add(Object o) {
            Assert.assertEquals("Read lock should have no readers while write in progress", lock.readers, 0);
            Assert.assertTrue("Attempting to write without write lock", lock.isLocked);
        }

        @Override
        public void remove(Object o) {
            Assert.assertEquals("Read lock should have no readers while write in progress", lock.readers, 0);
            Assert.assertTrue("Attempting to write without write lock", lock.isLocked);
        }

        @Override
        public void update(Object told, Object tnew) {
            Assert.assertEquals("Read lock should have no readers while write in progress", lock.readers, 0);
            Assert.assertTrue("Attempting to write without write lock", lock.isLocked);
        }
    }
}
