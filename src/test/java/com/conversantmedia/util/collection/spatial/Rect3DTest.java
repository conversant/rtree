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

import com.conversantmedia.util.collection.geometry.Point3d;
import com.conversantmedia.util.collection.geometry.Rect2d;
import com.conversantmedia.util.collection.geometry.Rect3d;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jcovert on 6/16/15.
 */
public class Rect3DTest {

    @Test
    public void centroidTest() {

        Rect3d rect = new Rect3d(0, 0, 0, 4, 3, 2);

        HyperPoint centroid = rect.getCentroid();
        double x = centroid.getCoord(Point3d.X);
        double y = centroid.getCoord(Point3d.Y);
        double z = centroid.getCoord(Point3d.Z);
        Assert.assertTrue("Bad X-coord of centroid - expected " + 2.0 + " but was " + x, RTree.isEqual(x, 2.0d));
        Assert.assertTrue("Bad Y-coord of centroid - expected " + 1.5 + " but was " + y, RTree.isEqual(y, 1.5d));
        Assert.assertTrue("Bad Z-coord of centroid - expected " + 1.0 + " but was " + y, RTree.isEqual(z, 1.0d));
    }

    @Test
    public void mbrTest() {

        Rect3d rect = new Rect3d(0, 0, 0, 4, 3, 2);

        // shouldn't affect MBR
        Rect3d rectInside = new Rect3d(0, 0, 0, 1, 1, 1);
        HyperRect mbr = rect.getMbr(rectInside);
        double expectedMinX = rect.getMin().getCoord(Point3d.X);
        double expectedMinY = rect.getMin().getCoord(Point3d.Y);
        double expectedMinZ = rect.getMin().getCoord(Point3d.Z);
        double expectedMaxX = rect.getMax().getCoord(Point3d.X);
        double expectedMaxY = rect.getMax().getCoord(Point3d.Y);
        double expectedMaxZ = rect.getMin().getCoord(Point3d.Z);
        double actualMinX = mbr.getMin().getCoord(Point3d.X);
        double actualMinY = mbr.getMin().getCoord(Point3d.Y);
        double actualMinZ = mbr.getMin().getCoord(Point3d.Z);
        double actualMaxX = mbr.getMax().getCoord(Point3d.X);
        double actualMaxY = mbr.getMax().getCoord(Point3d.Y);
        double actualMaxZ = mbr.getMax().getCoord(Point3d.Z);
        Assert.assertTrue("Bad minX - Expected: " + expectedMinX + " Actual: " + actualMinX, RTree.isEqual(actualMinX, expectedMinX));
        Assert.assertTrue("Bad minY - Expected: " + expectedMinY + " Actual: " + actualMinY, RTree.isEqual(actualMinY, expectedMinY));
        Assert.assertTrue("Bad maxX - Expected: " + expectedMaxX + " Actual: " + actualMaxX, RTree.isEqual(actualMaxX, expectedMaxX));
        Assert.assertTrue("Bad maxY - Expected: " + expectedMaxY + " Actual: " + actualMaxY, RTree.isEqual(actualMaxY, expectedMaxY));

        // should affect MBR
        Rect3d rectOverlap = new Rect3d(3, 1, 3, 5, 4, 4);
        mbr = rect.getMbr(rectOverlap);
        expectedMinX = 0.0d;
        expectedMinY = 0.0d;
        expectedMinZ = 0.0d;
        expectedMaxX = 5.0d;
        expectedMaxY = 4.0d;
        expectedMaxZ = 4.0d;
        actualMinX = mbr.getMin().getCoord(Point3d.X);
        actualMinY = mbr.getMin().getCoord(Point3d.Y);
        actualMinZ = mbr.getMin().getCoord(Point3d.Z);
        actualMaxX = mbr.getMax().getCoord(Point3d.X);
        actualMaxY = mbr.getMax().getCoord(Point3d.Y);
        actualMaxZ = mbr.getMax().getCoord(Point3d.Z);
        Assert.assertTrue("Bad minX - Expected: " + expectedMinX + " Actual: " + actualMinX, RTree.isEqual(actualMinX, expectedMinX));
        Assert.assertTrue("Bad minY - Expected: " + expectedMinY + " Actual: " + actualMinY, RTree.isEqual(actualMinY, expectedMinY));
        Assert.assertTrue("Bad maxX - Expected: " + expectedMaxX + " Actual: " + actualMaxX, RTree.isEqual(actualMaxX, expectedMaxX));
        Assert.assertTrue("Bad maxY - Expected: " + expectedMaxY + " Actual: " + actualMaxY, RTree.isEqual(actualMaxY, expectedMaxY));
    }

    @Test
    public void rangeTest() {

        Rect3d rect = new Rect3d(0, 0, 0, 4, 3, 2);

        double xRange = rect.getRange(Point3d.X);
        double yRange = rect.getRange(Point3d.Y);
        double zRange = rect.getRange(Point3d.Z);
        Assert.assertTrue("Bad range in dimension X - expected " + 4.0 + " but was " + xRange, RTree.isEqual(xRange, 4.0d));
        Assert.assertTrue("Bad range in dimension Y - expected " + 3.0 + " but was " + yRange, RTree.isEqual(yRange, 3.0d));
        Assert.assertTrue("Bad range in dimension Y - expected " + 2.0 + " but was " + zRange, RTree.isEqual(zRange, 2.0d));
    }


    @Test
    public void containsTest() {

        Rect3d rect = new Rect3d(0, 0, 0, 4, 3, 2);

        // shares an edge on the outside, not contained
        Rect3d rectOutsideNotContained = new Rect3d(4, 2, 4, 5, 3, 5);
        Assert.assertTrue("Shares an edge but should not be 'contained'", !rect.contains(rectOutsideNotContained));

        // shares an edge on the inside, not contained
        Rect3d rectInsideNotContained = new Rect3d(0, 1, 0, 4, 5, 0);
        Assert.assertTrue("Shares an edge but should not be 'contained'", !rect.contains(rectInsideNotContained));

        // shares an edge on the inside, contained
        Rect3d rectInsideContained = new Rect3d(0, 1, 0, 1, 2, 0);
        Assert.assertTrue("Shares an edge and should be 'contained'", rect.contains(rectInsideContained));

        // intersects
        Rect3d rectIntersects = new Rect3d(3, 2, 0, 5, 4, 0);
        Assert.assertTrue("Intersects but should not be 'contained'", !rect.contains(rectIntersects));

        // contains
        Rect3d rectContained = new Rect3d(1, 1, 1, 2, 2, 2);
        Assert.assertTrue("Contains and should be 'contained'", rect.contains(rectContained));

        // does not contain or intersect
        Rect3d rectNotContained = new Rect3d(5, 0, 0, 6, 1, 0);
        Assert.assertTrue("Does not contain and should not be 'contained'", !rect.contains(rectNotContained));
    }

    @Test
    public void intersectsTest() {

        Rect3d rect = new Rect3d(0, 0, 0, 4, 3, 0);

        // shares an edge on the outside, intersects
        Rect3d rectOutsideIntersects = new Rect3d(4, 2, 0, 5, 3, 0);
        Assert.assertTrue("Shares an edge and should 'intersect'", rect.intersects(rectOutsideIntersects));

        // shares an edge on the inside, intersects
        Rect3d rectInsideIntersects = new Rect3d(0, 1, 0, 4, 5, 0);
        Assert.assertTrue("Shares an edge and should 'intersect'", rect.intersects(rectInsideIntersects));

        // shares an edge on the inside, intersects
        Rect3d rectInsideIntersectsContained = new Rect3d(0, 1, 0, 1, 2, 0);
        Assert.assertTrue("Shares an edge and should 'intersect'", rect.intersects(rectInsideIntersectsContained));

        // intersects
        Rect3d rectIntersects = new Rect3d(3, 2, 0, 5, 4, 0);
        Assert.assertTrue("Intersects and should 'intersect'", rect.intersects(rectIntersects));

        // contains
        Rect3d rectContained = new Rect3d(1, 1, 0, 2, 2, 0);
        Assert.assertTrue("Contains and should 'intersect'", rect.intersects(rectContained));

        // does not contain or intersect
        Rect3d rectNotIntersects = new Rect3d(5, 0, 0, 6, 1, 0);
        Assert.assertTrue("Does not intersect and should not 'intersect'", !rect.intersects(rectNotIntersects));
    }

    @Test
    public void costTest() {

        Rect3d rect = new Rect3d(0, 0, 0, 4, 3, 2);
        double cost = rect.cost();
        Assert.assertTrue("Bad cost - expected " + 24.0 + " but was " + cost, cost == 24D);
    }
}
