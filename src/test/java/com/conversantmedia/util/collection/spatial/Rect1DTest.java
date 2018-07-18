package com.conversantmedia.util.collection.spatial;

/*
 * #%L
 * Conversant RTree
 * ~~
 * Conversantmedia.com © 2018, Conversant, Inc. Conversant® is a trademark of Conversant, Inc.
 * John Cairns <john@2ad.com> © 2018
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


import com.conversantmedia.util.collection.geometry.Range1d;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jcairns on 7/18/18.
 */
public class Rect1DTest {

    @Test
    public void centroidTest() {
        Range1d rect = new Range1d(0D, 1D);
        Assert.assertTrue(RTree.isEqual(.5D, rect.getCentroid().getCoord(0)));
    }

    @Test
    public void mbrTest() {
        Range1d r1 = new Range1d(0, 5);
        Range1d r2 = new Range1d(4, 7);
        Range1d r3 = new Range1d(-4, 6);

        Range1d mbr = (Range1d)r1.getMbr(r2);
        mbr = (Range1d)mbr.getMbr(r3);

        Assert.assertTrue(RTree.isEqual(-4, mbr.getMin().getCoord(0)));
        Assert.assertTrue(RTree.isEqual(7, mbr.getMax().getCoord(0)));
    }

    @Test
    public void checkContains() {

        Range1d r1 = new Range1d(0, 5);

        Range1d r2 = new Range1d(2, 3);

        Range1d r3 = new Range1d(3, 10);

        Assert.assertTrue(r1.contains(r2));
        Assert.assertFalse(r2.contains(r1));
        Assert.assertFalse(r1.contains(r3));

    }

    @Test
    public void checkIntersects() {

        Range1d r1 = new Range1d(0, 5);

        Range1d r2 = new Range1d(2, 3);

        Range1d r3 = new Range1d(3, 10);

        Assert.assertTrue(r1.intersects(r2));
        Assert.assertTrue(r2.intersects(r1));
        Assert.assertTrue(r1.intersects(r3));

    }

}
