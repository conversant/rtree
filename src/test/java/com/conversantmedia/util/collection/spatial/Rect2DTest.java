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

import com.conversantmedia.util.collection.geometry.Rect2d;
import org.junit.Test;
import org.junit.Assert;

/**
 * Created by jcovert on 6/16/15.
 */
public class Rect2DTest {

    @Test
    public void centroidTest() {

        Rect2d rect = new Rect2d(0, 0, 4, 3);

        HyperPoint centroid = rect.getCentroid();
        double x = centroid.getCoord(0);
        double y = centroid.getCoord(1);
        Assert.assertTrue("Bad X-coord of centroid - expected " + 2.0 + " but was " + x, RTree.isEqual(x, 2.0d));
        Assert.assertTrue("Bad Y-coord of centroid - expected " + 1.5 + " but was " + y, RTree.isEqual(y, 1.5d));
    }

    @Test
    public void mbrTest() {

        Rect2d rect = new Rect2d(0, 0, 4, 3);

        // shouldn't effect MBR
        Rect2d rectInside = new Rect2d(0, 0, 1, 1);
        HyperRect mbr = rect.getMbr(rectInside);
        double expectedMinX = rect.getMin().getCoord(0);
        double expectedMinY = rect.getMin().getCoord(1);
        double expectedMaxX = rect.getMax().getCoord(0);
        double expectedMaxY = rect.getMax().getCoord(1);
        double actualMinX = mbr.getMin().getCoord(0);
        double actualMinY = mbr.getMin().getCoord(1);
        double actualMaxX = mbr.getMax().getCoord(0);
        double actualMaxY = mbr.getMax().getCoord(1);
        Assert.assertTrue("Bad minX - Expected: " + expectedMinX + " Actual: " + actualMinX, actualMinX == expectedMinX);
        Assert.assertTrue("Bad minY - Expected: " + expectedMinY + " Actual: " + actualMinY, actualMinY == expectedMinY);
        Assert.assertTrue("Bad maxX - Expected: " + expectedMaxX + " Actual: " + actualMaxX, actualMaxX == expectedMaxX);
        Assert.assertTrue("Bad maxY - Expected: " + expectedMaxY + " Actual: " + actualMaxY, actualMaxY == expectedMaxY);

        // should affect MBR
        Rect2d rectOverlap = new Rect2d(3, 1, 5, 4);
        mbr = rect.getMbr(rectOverlap);
        expectedMinX = 0.0d;
        expectedMinY = 0.0d;
        expectedMaxX = 5.0d;
        expectedMaxY = 4.0d;
        actualMinX = mbr.getMin().getCoord(0);
        actualMinY = mbr.getMin().getCoord(1);
        actualMaxX = mbr.getMax().getCoord(0);
        actualMaxY = mbr.getMax().getCoord(1);
        Assert.assertTrue("Bad minX - Expected: " + expectedMinX + " Actual: " + actualMinX, actualMinX == expectedMinX);
        Assert.assertTrue("Bad minY - Expected: " + expectedMinY + " Actual: " + actualMinY, actualMinY == expectedMinY);
        Assert.assertTrue("Bad maxX - Expected: " + expectedMaxX + " Actual: " + actualMaxX, actualMaxX == expectedMaxX);
        Assert.assertTrue("Bad maxY - Expected: " + expectedMaxY + " Actual: " + actualMaxY, actualMaxY == expectedMaxY);
    }

    @Test
    public void rangeTest() {

        Rect2d rect = new Rect2d(0, 0, 4, 3);

        double xRange = rect.getRange(0);
        double yRange = rect.getRange(1);
        Assert.assertTrue("Bad range in dimension X - expected " + 4.0 + " but was " + xRange, xRange == 4.0d);
        Assert.assertTrue("Bad range in dimension Y - expected " + 3.0 + " but was " + yRange, yRange == 3.0d);
    }


    @Test
    public void containsTest() {

        Rect2d rect = new Rect2d(0, 0, 4, 3);

        // shares an edge on the outside, not contained
        Rect2d rectOutsideNotContained = new Rect2d(4, 2, 5, 3);
        Assert.assertTrue("Shares an edge but should not be 'contained'", !rect.contains(rectOutsideNotContained));

        // shares an edge on the inside, not contained
        Rect2d rectInsideNotContained = new Rect2d(0, 1, 4, 5);
        Assert.assertTrue("Shares an edge but should not be 'contained'", !rect.contains(rectInsideNotContained));

        // shares an edge on the inside, contained
        Rect2d rectInsideContained = new Rect2d(0, 1, 1, 2);
        Assert.assertTrue("Shares an edge and should be 'contained'", rect.contains(rectInsideContained));

        // intersects
        Rect2d rectIntersects = new Rect2d(3, 2, 5, 4);
        Assert.assertTrue("Intersects but should not be 'contained'", !rect.contains(rectIntersects));

        // contains
        Rect2d rectContained = new Rect2d(1, 1, 2, 2);
        Assert.assertTrue("Contains and should be 'contained'", rect.contains(rectContained));

        // does not contain or intersect
        Rect2d rectNotContained = new Rect2d(5, 0, 6, 1);
        Assert.assertTrue("Does not contain and should not be 'contained'", !rect.contains(rectNotContained));
    }

    @Test
    public void intersectsTest() {

        Rect2d rect = new Rect2d(0, 0, 4, 3);

        // shares an edge on the outside, intersects
        Rect2d rectOutsideIntersects = new Rect2d(4, 2, 5, 3);
        Assert.assertTrue("Shares an edge and should 'intersect'", rect.intersects(rectOutsideIntersects));

        // shares an edge on the inside, intersects
        Rect2d rectInsideIntersects = new Rect2d(0, 1, 4, 5);
        Assert.assertTrue("Shares an edge and should 'intersect'", rect.intersects(rectInsideIntersects));

        // shares an edge on the inside, intersects
        Rect2d rectInsideIntersectsContained = new Rect2d(0, 1, 1, 2);
        Assert.assertTrue("Shares an edge and should 'intersect'", rect.intersects(rectInsideIntersectsContained));

        // intersects
        Rect2d rectIntersects = new Rect2d(3, 2, 5, 4);
        Assert.assertTrue("Intersects and should 'intersect'", rect.intersects(rectIntersects));

        // contains
        Rect2d rectContained = new Rect2d(1, 1, 2, 2);
        Assert.assertTrue("Contains and should 'intersect'", rect.intersects(rectContained));

        // does not contain or intersect
        Rect2d rectNotIntersects = new Rect2d(5, 0, 6, 1);
        Assert.assertTrue("Does not intersect and should not 'intersect'", !rect.intersects(rectNotIntersects));
    }

    @Test
    public void costTest() {

        Rect2d rect = new Rect2d(0, 0, 4, 3);
        double cost = rect.cost();
        Assert.assertTrue("Bad cost - expected " + 12.0 + " but was " + cost, cost == 12.0d);
    }
}
