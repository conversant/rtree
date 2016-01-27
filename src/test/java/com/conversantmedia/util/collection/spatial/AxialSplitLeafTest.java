package com.conversantmedia.util.collection.spatial;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by jcovert on 6/12/15.
 */
public class AxialSplitLeafTest {

    private static final RTree.Split TYPE = RTree.Split.AXIAL;

    /**
     * Adds enough entries to force a single split and confirms that
     * no entries are lost.
     */
    @Test
    public void basicSplitTest() {

        RTree<Rect2D> rTree = RTreeTest.createRect2DTree(TYPE);
        rTree.add(new Rect2D(0, 0, 1, 1));
        rTree.add(new Rect2D(1, 1, 2, 2));
        rTree.add(new Rect2D(2, 2, 3, 3));
        rTree.add(new Rect2D(3, 3, 4, 4));
        rTree.add(new Rect2D(4, 4, 5, 5));
        rTree.add(new Rect2D(5, 5, 6, 6));
        rTree.add(new Rect2D(6, 6, 7, 7));
        rTree.add(new Rect2D(7, 7, 8, 8));
        // 9 entries guarantees a split
        rTree.add(new Rect2D(8, 8, 9, 9));

        Stats stats = rTree.collectStats();
        Assert.assertTrue("Unexpected max depth after basic split", stats.getMaxDepth() == 1);
        Assert.assertTrue("Unexpected number of branches after basic split", stats.getBranchCount() == 1);
        Assert.assertTrue("Unexpected number of leaves after basic split", stats.getLeafCount() == 2);
        Assert.assertTrue("Unexpected number of entries per leaf after basic split", stats.getEntriesPerLeaf() == 4.5);
    }

    @Test
    public void splitCorrectnessTest() {

        RTree<Rect2D> rTree = RTreeTest.createRect2DTree(TYPE, 2, 4);
        rTree.add(new Rect2D(0, 0, 3, 3));
        rTree.add(new Rect2D(1, 1, 2, 2));
        rTree.add(new Rect2D(2, 2, 4, 4));
        rTree.add(new Rect2D(4, 0, 5, 1));
        // 5 entrees guarantees a split
        rTree.add(new Rect2D(0, 2, 1, 4));

        Branch root = (Branch) rTree.getRoot();
        Node<Rect2D>[] children = root.getChildren();
        int childCount = 0;
        for(Node c : children) {
            if (c != null) {
                childCount++;
            }
        }
        Assert.assertEquals("Expected different number of children after split", 2, childCount);

        Node<Rect2D> child1 = children[0];
        Rect2D child1Mbr = (Rect2D) child1.getRect();
        Rect2D expectedChild1Mbr = new Rect2D(0, 0, 3, 4);
        Assert.assertEquals("Child 1 size incorrect after split", 3, child1.size());
        Assert.assertEquals("Child 1 mbr incorrect after split", expectedChild1Mbr, child1Mbr);

        Node<Rect2D> child2 = children[1];
        Rect2D child2Mbr = (Rect2D) child2.getRect();
        Rect2D expectedChild2Mbr = new Rect2D(2, 0, 5, 4);
        Assert.assertEquals("Child 2 size incorrect after split", 2, child2.size());
        Assert.assertEquals("Child 2 mbr incorrect after split", expectedChild2Mbr, child2Mbr);
    }

    /**
     * Adds several overlapping rectangles and confirms that no entries
     * are lost during insert/split.
     */
    @Test
    public void overlappingEntryTest() {

        final RTree<Rect2D> rTree = RTreeTest.createRect2DTree(TYPE);
        rTree.add(new Rect2D(0, 0, 1, 1));
        rTree.add(new Rect2D(0, 0, 2, 2));
        rTree.add(new Rect2D(0, 0, 2, 2));
        rTree.add(new Rect2D(0, 0, 3, 3));
        rTree.add(new Rect2D(0, 0, 3, 3));

        rTree.add(new Rect2D(0, 0, 4, 4));
        rTree.add(new Rect2D(0, 0, 5, 5));
        rTree.add(new Rect2D(0, 0, 6, 6));
        rTree.add(new Rect2D(0, 0, 7, 7));
        rTree.add(new Rect2D(0, 0, 7, 7));

        rTree.add(new Rect2D(0, 0, 8, 8));
        rTree.add(new Rect2D(0, 0, 9, 9));
        rTree.add(new Rect2D(0, 1, 2, 2));
        rTree.add(new Rect2D(0, 1, 3, 3));
        rTree.add(new Rect2D(0, 1, 4, 4));

        rTree.add(new Rect2D(0, 1, 4, 4));
        rTree.add(new Rect2D(0, 1, 5, 5));

        // 17 entries guarantees *at least* 2 splits when max leaf size is 8
        final int expectedEntryCount = 17;

        final Stats stats = rTree.collectStats();
        Assert.assertEquals("Unexpected number of entries in " + TYPE + " split tree: " + stats.getEntryCount() + " entries - expected: " + expectedEntryCount + " actual: " + stats.getEntryCount(), expectedEntryCount, stats.getEntryCount());
    }

    /**
     * Adds many random entries and confirm that no entries
     * are lost during insert/split.
     */
    @Test
    public void randomEntryTest() {

        final int entryCount = 50000;
        final Rect2D[] rects = RTreeTest.generateRandomRects(entryCount);

        final RTree<Rect2D> rTree = RTreeTest.createRect2DTree(TYPE);
        for (int i = 0; i < rects.length; i++) {
            rTree.add(rects[i]);
        }

        final Stats stats = rTree.collectStats();
        Assert.assertEquals("Unexpected number of entries in " + TYPE + " split tree: " + stats.getEntryCount() + " entries - expected: " + entryCount + " actual: " + stats.getEntryCount(), entryCount, stats.getEntryCount());
        stats.print(System.out);
    }

    /**
     * This test previously caused a StackOverflowException on LINEAR leaf.
     * It has since been fixed, but keeping the test here to ensure this leaf type
     * never falls victim to the same issue.
     */
    @Test
    public void causeLinearSplitOverflow() {
        final RTree<Rect2D> rTree = RTreeTest.createRect2DTree(TYPE);
        final Random rand = new Random(13);
        for (int i = 0; i < 500; i++) {
            final int x1 = rand.nextInt(10);
            final int y1 = rand.nextInt(10);
            final int x2 = x1 + rand.nextInt(200);
            final int y2 = y1 + rand.nextInt(200);

            rTree.add(new Rect2D(x1, y1, x2, y2));
        }
        final Stats stats = rTree.collectStats();
        stats.print(System.out);
    }


}
