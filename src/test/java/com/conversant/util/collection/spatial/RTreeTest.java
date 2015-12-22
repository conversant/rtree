package com.conversant.util.collection.spatial;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.text.html.HTMLDocument;
import java.util.Random;

/**
 * Created by jcairns on 4/30/15.
 */
public class RTreeTest {

    @Test
    public void pointSearchTest() {

        final RTree<Point> pTree = new RTree<>(new Point.Builder());

        for(int i=0; i<10; i++) {
            pTree.add(new Point(i, i));
        }

        final Rect2D rect = new Rect2D(new Point(2,2), new Point(8,8));
        final Point[] result = new Point[10];

        final int n = pTree.search(rect, result);
        Assert.assertEquals(7, n);

        for(int i=0; i<n; i++) {
            Assert.assertTrue(result[i].x >= 2);
            Assert.assertTrue(result[i].x <= 8);
            Assert.assertTrue(result[i].y >= 2);
            Assert.assertTrue(result[i].y <= 8);
        }
    }

    /**
     * Use an small bounding box to ensure that only expected rectangles are returned.
     * Verifies the count returned from search AND the number of rectangles results.
     */
    @Test
    public void rect2DSearchTest() {

        final int entryCount = 20;

        for (RTree.SPLIT_TYPE type : RTree.SPLIT_TYPE.values()) {
            RTree<Rect2D> rTree = createRect2DTree(type, 2, 8);
            for (int i = 0; i < entryCount; i++) {
                rTree.add(new Rect2D(i, i, i+3, i+3));
            }

            final Rect2D searchRect = new Rect2D(5, 5, 10, 10);
            Rect2D[] results = new Rect2D[entryCount];

            final int foundCount = rTree.search(searchRect, results);
            int resultCount = 0;
            for(int i = 0; i < results.length; i++) {
                if(results[i] != null) {
                    resultCount++;
                }
            }

            final int expectedCount = 9;
            Assert.assertEquals("[" + type + "] Search returned incorrect search result count - expected: " + expectedCount + " actual: " + foundCount, expectedCount, foundCount);
            Assert.assertEquals("[" + type + "] Search returned incorrect number of rectangles - expected: " + expectedCount + " actual: " + resultCount, expectedCount, resultCount);

            // If the order of nodes in the tree changes, this test may fail while returning the correct results.
            for (int i = 0; i < resultCount; i++) {
                Assert.assertTrue("Unexpected result found", results[i].min.x == i + 2 && results[i].min.y == i + 2 && results[i].max.x == i + 5 && results[i].max.y == i + 5);
            }
        }
    }

    /**
     * Use an enormous bounding box to ensure that every rectangle is returned.
     * Verifies the count returned from search AND the number of rectangles results.
     */
    @Test
    public void rect2DSearchAllTest() {

        final int entryCount = 10000;
        final Rect2D[] rects = generateRandomRects(entryCount);

        for (RTree.SPLIT_TYPE type : RTree.SPLIT_TYPE.values()) {
            RTree<Rect2D> rTree = createRect2DTree(type, 2, 8);
            for (int i = 0; i < rects.length; i++) {
                rTree.add(rects[i]);
            }

            final Rect2D searchRect = new Rect2D(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
            Rect2D[] results = new Rect2D[entryCount];

            final int foundCount = rTree.search(searchRect, results);
            int resultCount = 0;
            for(int i = 0; i < results.length; i++) {
                if(results[i] != null) {
                    resultCount++;
                }
            }

            final int expectedCount = entryCount;
            Assert.assertEquals("[" + type + "] Search returned incorrect search result count - expected: " + expectedCount + " actual: " + foundCount, expectedCount, foundCount);
            Assert.assertEquals("[" + type + "] Search returned incorrect number of rectangles - expected: " + expectedCount + " actual: " + resultCount, expectedCount, resultCount);
        }
    }

    /**
     * Collect stats making the structure of trees of each split type
     * more visible.
     */
    @Test
    public void treeStructureStatsTest() {

        final int entryCount = 50_000;

        final Rect2D[] rects = generateRandomRects(entryCount);
        for (RTree.SPLIT_TYPE type : RTree.SPLIT_TYPE.values()) {
            RTree<Rect2D> rTree = createRect2DTree(type, 2, 8);
            for (int i = 0; i < rects.length; i++) {
                rTree.add(rects[i]);
            }

            Stats stats = rTree.collectStats();
            stats.print(System.out);
        }
    }

    /**
     * Do a search and collect stats on how many nodes we hit and how many
     * bounding boxes we had to evaluate to get all the results.
     *
     * Preliminary findings:
     *  - Evals for QUADRATIC tree increases with size of the search bounding box.
     *  - QUADRATIC seems to be ideal for small search bounding boxes.
     */
    @Test
    public void treeSearchStatsTest() {

        final int entryCount = 5000;

        final Rect2D[] rects = generateRandomRects(entryCount);
        for (RTree.SPLIT_TYPE type : RTree.SPLIT_TYPE.values()) {
            RTree<Rect2D> rTree = createRect2DTree(type, 2, 8);
            for (int i = 0; i < rects.length; i++) {
                rTree.add(rects[i]);
            }

            rTree.instrumentTree();

            final Rect2D searchRect = new Rect2D(100, 100, 120, 120);
            Rect2D[] results = new Rect2D[entryCount];
            int foundCount = rTree.search(searchRect, results);

            CounterNode<Rect2D> root = (CounterNode<Rect2D>) rTree.getRoot();

            System.out.println("[" + type + "] searched " + root.searchCount + " nodes, returning " + foundCount + " entries");
            System.out.println("[" + type + "] evaluated " + root.bboxEvalCount + " b-boxes, returning " + foundCount + " entries");
        }
    }

    @Test
    public void treeRemovalTest() {
        final RTree<Rect2D> rTree = new RTree<Rect2D>(new Rect2D.Builder(), 2, 8, RTree.SPLIT_TYPE.QUADRATIC);

        Rect2D[] rects = new Rect2D[1000];
        for(int i = 0; i < rects.length; i++){
            rects[i] = new Rect2D(i, i, i+1, i+1);
            rTree.add(rects[i]);
        }
        for(int i = 0; i < rects.length; i++) {
            rTree.remove(rects[i]);
        }
        Rect2D[] searchResults = new Rect2D[10];
        for(int i = 0; i < rects.length; i++) {
            Assert.assertTrue("Found hyperRect that should have been removed on search " + i, rTree.search(rects[i], searchResults) == 0);
        }

        rTree.add(new Rect2D(0,0,5,5));
        Assert.assertTrue("Found hyperRect that should have been removed on search ", rTree.getEntryCount() != 0);
    }

    @Test
    public void treeSingelRemovalTest() {
        final RTree<Rect2D> rTree = new RTree<Rect2D>(new Rect2D.Builder(), 2, 8, RTree.SPLIT_TYPE.QUADRATIC);

        Rect2D rect = new Rect2D(0,0,2,2);
        rTree.add(rect);
        Assert.assertTrue("Did not add HyperRect to Tree", rTree.getEntryCount() > 0);
        rTree.remove(rect);
        Assert.assertTrue("Did not remove HyperRect from Tree", rTree.getEntryCount() == 0);
        rTree.add(rect);
        Assert.assertTrue("Tree nulled out and could not add HyperRect back in", rTree.getEntryCount() > 0);
    }

    @Test
    public void treeRemoveAndRebalanceTest() {
        final RTree<Rect2D> rTree = new RTree<Rect2D>(new Rect2D.Builder(), 2, 8, RTree.SPLIT_TYPE.QUADRATIC);

        Rect2D[] rect = new Rect2D[65];
        for(int i = 0; i < rect.length; i++){
            if(i < 4){ rect[i] = new Rect2D(0,0,1,1); }
            else if(i < 8) { rect[i] = new Rect2D(2, 2, 4, 4); }
            else if(i < 12) { rect[i] = new Rect2D(4,4,5,5); }
            else if(i < 16) { rect[i] = new Rect2D(5,5,6,6); }
            else if(i < 20) { rect[i] = new Rect2D(6,6,7,7); }
            else if(i < 24) { rect[i] = new Rect2D(7,7,8,8); }
            else if(i < 28) { rect[i] = new Rect2D(8,8,9,9); }
            else if(i < 32) { rect[i] = new Rect2D(9,9,10,10); }
            else if(i < 36) { rect[i] = new Rect2D(2,2,4,4); }
            else if(i < 40) { rect[i] = new Rect2D(4,4,5,5); }
            else if(i < 44) { rect[i] = new Rect2D(5,5,6,6); }
            else if(i < 48) { rect[i] = new Rect2D(6,6,7,7); }
            else if(i < 52) { rect[i] = new Rect2D(7,7,8,8); }
            else if(i < 56) { rect[i] = new Rect2D(8,8,9,9); }
            else if(i < 60) { rect[i] = new Rect2D(9,9,10,10); }
            else if(i < 65) { rect[i] = new Rect2D(1,1,2,2); }
        }
        for(int i = 0; i < rect.length; i++){
            rTree.add(rect[i]);
        }
        Stats stat = rTree.collectStats();
        stat.print(System.out);
        for(int i = 0; i < 5; i++){
            rTree.remove(rect[64]);
        }
        Stats stat2 = rTree.collectStats();
        stat2.print(System.out);
    }

    @Test
    public void treeUpdateTest() {
        final RTree<Rect2D> rTree = new RTree<Rect2D>(new Rect2D.Builder(), 2, 8, RTree.SPLIT_TYPE.QUADRATIC);

        Rect2D rect = new Rect2D(0, 1, 2, 3);
        rTree.add(rect);
        Rect2D oldRect = new Rect2D(0,1,2,3);
        Rect2D newRect = new Rect2D(1,2,3,4);
        rTree.update(oldRect, newRect);
        Rect2D[] results = new Rect2D[2];
        int num = rTree.search(newRect, results);
        Assert.assertTrue("Did not find the updated HyperRect", num == 1);
        String st = results[0].toString();
        System.out.print(st);
    }

        /**
         * Generate 'count' random rectangles with fixed ranges.
         *
         * @param count - number of rectangles to generate
         * @return array of generated rectangles
         */
    public static Rect2D[] generateRandomRects(int count) {
        final Random rand = new Random(13);

        // changing these values changes the rectangle sizes and consequently the distribution density
        final int minX = 500;
        final int minY = 500;
        final int maxXRange = 25;
        final int maxYRange = 25;

        final double hitProb = 1.0 * count * maxXRange * maxYRange / (minX * minY);

        final Rect2D[] rects = new Rect2D[count];
        for (int i = 0; i < count; i++) {
            final int x1 = rand.nextInt(minX);
            final int y1 = rand.nextInt(minY);
            final int x2 = x1 + rand.nextInt(maxXRange);
            final int y2 = y1 + rand.nextInt(maxYRange);
            rects[i] = new Rect2D(x1, y1, x2, y2);
        }

        return rects;
    }


    /**
     * Create a tree capable of holding rectangles with default minM (2) and maxM (8) values.
     *
     * @param splitType - type of leaf to use (affects how full nodes get split)
     * @return tree
     */
    public static RTree<Rect2D> createRect2DTree(RTree.SPLIT_TYPE splitType) {
        return createRect2DTree(splitType, 2, 8);
    }

    /**
     * Create a tree capable of holding rectangles with specified m and M values.
     *
     * @param splitType - type of leaf to use (affects how full nodes get split)
     * @param minM - minimum number of entries in each leaf
     * @param maxM - maximum number of entries in each leaf
     * @return tree
     */
    public static RTree<Rect2D> createRect2DTree(RTree.SPLIT_TYPE splitType, int minM, int maxM) {
        return new RTree<>(new Rect2D.Builder(), minM, maxM, splitType);
    }
}
