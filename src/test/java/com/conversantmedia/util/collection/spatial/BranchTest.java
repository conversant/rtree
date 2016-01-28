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

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jcovert on 6/12/15.
 */
public class BranchTest {

    /**
     * Code was added to Branch remove an extra that's created during splitting.
     */
    @Test
    public void branchOptimizationTest() {

        for(RTree.Split type : RTree.Split.values()) {
            RTree<Rect2D> rTree = RTreeTest.createRect2DTree(type);
            Rect2D[] rects = RTreeTest.generateRandomRects(80);

            int i = 0;
            // cause no splits, fill up leaf
            while (i < 8) {
                rTree.add(rects[i++]);
            }
            Assert.assertEquals("[" + type + "] Expected 0 branches at this time", 0, rTree.collectStats().getBranchCount());

            // leaf was full, first split
            rTree.add(rects[i++]);
            Assert.assertEquals("[" + type + "] Expected 1 branch at this time", 1, rTree.collectStats().getBranchCount());

            // cause another split, extra branches get optimized out
            while (i < 30) {
                rTree.add(rects[i++]);
            }
            Assert.assertEquals("[" + type + "] Expected 1 branch at this time", 1, rTree.collectStats().getBranchCount());

            // cause enough additional splits to force new branch creation
            while (i < 80) {
                rTree.add(rects[i++]);
            }
            Assert.assertEquals("[" + type + "] Expected 6 branches at this time", 6, rTree.collectStats().getBranchCount());
        }
    }
}
